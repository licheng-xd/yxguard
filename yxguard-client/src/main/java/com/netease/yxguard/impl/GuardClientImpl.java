package com.netease.yxguard.impl;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.netease.yxguard.client.GuardClient;
import com.netease.yxguard.client.ServiceCache;
import com.netease.yxguard.client.ServiceInstance;
import com.netease.yxguard.client.ServiceProvider;
import com.netease.yxguard.config.GuardConfig;
import com.netease.yxguard.utils.CloseableUtil;
import com.netease.yxguard.utils.HttpUtil;
import com.netease.yxguard.utils.SerializerUtil;
import com.netease.yxguard.utils.ThreadUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * 服务发现入口类实现
 *
 * Created by lc on 16/6/5.
 */
@SuppressWarnings("ALL")
public class GuardClientImpl implements GuardClient {
    private static final Logger logger = LoggerFactory.getLogger(GuardClientImpl.class);

    private final CuratorFramework client;
    private final String basePath;
    private final ConcurrentMap<String, ServiceEntry> services = Maps.newConcurrentMap();
    // 为了保证并发性和唯一性,使用SetFromMap
    private final Collection<ServiceCache> caches = Sets.newConcurrentHashSet();
    private final Collection<ServiceProvider> providers = Sets.newConcurrentHashSet();
    private final boolean watchInstances;

    private final ConnectionStateListener connectionStateListener = new ConnectionStateListener() {
        public void stateChanged(CuratorFramework curatorFramework,
            ConnectionState connectionState) {
            if (connectionState == ConnectionState.CONNECTED || connectionState == ConnectionState.RECONNECTED) {
                // 重新注册
                try {
                    logger.debug("Do re-register instances after reconnection");
                    reRegister();
                } catch (Exception e) {
                    ThreadUtil.checkInterrupt(e);
                    logger.error("Can't re-register instances after reconnection", e);
                }
            } else if (connectionState == ConnectionState.LOST) {
                // TODO 连接丢失
                logger.warn("Connection lost");
            }

        }
    };

    /**
     * 只能通过builder构建
     *
     * @param zkAddr
     * @param basePath
     * @param serializer
     * @param mode
     */
    GuardClientImpl(GuardConfig config, boolean watchInstances) {
        Preconditions.checkNotNull(config, "guard config cannot be null");
        if (config.getServerUrl() != null) {
            // 从guard server获取配置,否则用本地配置
            config = HttpUtil.getConfig(config.getServerUrl() + "/client/config");
        }
        Preconditions
            .checkArgument(!Strings.isNullOrEmpty(config.getZkAddr()),
                "zkAddr cannot be null");
        Preconditions
            .checkArgument(!Strings.isNullOrEmpty(config.getBasePath()),
                "basePath cannot be null");
        this.basePath = config.getBasePath();
        this.watchInstances = watchInstances;

        client = CuratorFrameworkFactory.builder()
            .connectString(config.getZkAddr())
            .sessionTimeoutMs(config.getSessionTimeoutMs())
            .connectionTimeoutMs(config.getConnectionTimeoutMs())
            .retryPolicy(
                new ExponentialBackoffRetry(config.getBaseSleepTimeMs(), config.getMaxRetries()))
            .build();
        client.start();
    }

    /**
     * 使用前必须先调用start方法
     *
     * @throws Exception
     */
    public void start() throws Exception {
        client.getConnectionStateListenable().addListener(connectionStateListener);
        logger.info("guard client start");
    }

    /**
     * 注册服务
     *
     * @param instance
     * @throws Exception
     */
    public void register(ServiceInstance instance) throws Exception {
        ServiceEntry newEntry = new ServiceEntry(instance);
        ServiceEntry oldEntry = services.putIfAbsent(instance.getId(), newEntry);
        ServiceEntry useEntry = oldEntry == null ? newEntry : oldEntry;
        synchronized (useEntry) {
            if (useEntry == newEntry) {
                // 监听新服务
                useEntry.cache = makeNodeCache(instance);
            }
            doRegister(instance);
        }
    }

    /**
     * 注销服务,只有在这个client注册的服务,才能在这个client注销
     *
     * @param instance
     * @throws Exception
     */
    public void unregister(ServiceInstance instance) throws Exception {
        ServiceEntry entry = services.remove(instance.getId());
        doUnregister(entry);
    }

    /**
     * 更新服务
     *
     * @param instance
     * @throws Exception
     */
    public void update(ServiceInstance instance) throws Exception {
        ServiceEntry entry = services.get(instance.getId());
        if (entry == null) {
            throw new Exception("Service not registered: " + instance);
        }
        synchronized (entry) {
            entry.service = instance;
            byte[] bytes = SerializerUtil.serialize(instance);
            String path = pathForInstance(instance.getName(), instance.getId());
            client.setData().forPath(path, bytes);
        }
    }

    /**
     * 获取所有可用的服务名
     *
     * @return
     * @throws Exception
     */
    public Collection<String> getAllServiceName() throws Exception {
        List<String> names = client.getChildren().forPath(basePath);
        return ImmutableList.copyOf(names);
    }

    /**
     * 根据服务名获取所有服务实例
     *
     * @param name
     * @return
     * @throws Exception
     */
    public Collection<ServiceInstance> getInstancesByName(String name)
        throws Exception {
        ImmutableList.Builder<ServiceInstance> builder = ImmutableList.builder();
        String path = pathForName(name);
        List<String> instanceIds;

        try {
            instanceIds = client.getChildren().forPath(path);
        } catch (KeeperException.NoNodeException e) {
            instanceIds = Lists.newArrayList();
        }

        for (String id : instanceIds) {
            ServiceInstance instance = getInstanceByNameId(name, id);
            if (instance != null) {
                builder.add(instance);
            }
        }
        return builder.build();
    }

    /**
     * 根据服务名和id获取对应的服务实例
     *
     * @param name
     * @param id
     * @return
     * @throws Exception
     */
    public ServiceInstance getInstanceByNameId(String name, String id) throws Exception {
        String path = pathForInstance(name, id);
        try {
            byte[] bytes = client.getData().forPath(path);
            return SerializerUtil.deserialize(bytes);
        }
        catch (KeeperException.NoNodeException ignore) {
            // ignore
        }
        return null;
    }

    /**
     * 返回{@link ServiceCacheBuilder}实例
     *
     * @return
     */
    public ServiceCacheBuilder serviceCacheBuilder() {
        return new ServiceCacheBuilder(this).threadFactory(ThreadUtil.newThreadFactory("ServiceCache"));
    }

    /**
     * 返回{@link ServiceProviderBuilder}实例
     *
     * @return
     */
    public ServiceProviderBuilder serviceProviderBuilder() {
        return new ServiceProviderBuilder(this).threadFactory(ThreadUtil.newThreadFactory("ServiceProvider"));
    }

    /**
     * 关闭所有服务
     *
     * @throws IOException
     */
    public void close() throws IOException {
        for (ServiceCache cache : Lists.newArrayList(caches)) {
            CloseableUtil.closeQuietly(cache);
        }
        for (ServiceProvider provider : Lists.newArrayList(providers)) {
            CloseableUtil.closeQuietly(provider);
        }
        for (ServiceEntry entry : services.values()) {
            try {
                doUnregister(entry);
            } catch (Exception e) {
                ThreadUtil.checkInterrupt(e);
                logger.error("Can't unregister instance: " +
                    pathForInstance(entry.service.getName(), entry.service.getId()), e);
            }
        }
        client.getConnectionStateListenable().removeListener(connectionStateListener);
        client.close();
    }

    public CuratorFramework getClient() {
        return client;
    }

    /**
     * 开启ServiceCache
     *
     * @param cache
     */
    void cacheOpened(ServiceCache cache) {
        caches.add(cache);
    }

    /**
     * 关闭ServiceCache
     *
     * @param cache
     */
    void cacheClosed(ServiceCache cache) {
        caches.remove(cache);
    }

    /**
     * 开启ServiceProvider
     *
     * @param provider
     */
    void providerOpened(ServiceProvider provider) {
        providers.add(provider);
    }

    /**
     * 关闭ServiceProvider
     *
     * @param cache
     */
    void providerClosed(ServiceProvider cache) {
        providers.remove(cache);
    }

    private static class ServiceEntry {
        private volatile ServiceInstance service;
        private volatile NodeCache cache;

        private ServiceEntry(ServiceInstance service) {
            this.service = service;
        }
    }

    private void reRegister() throws Exception {
        for (ServiceEntry entry : services.values()) {
            synchronized (entry) {
                doRegister(entry.service);
            }
        }
    }

    private void doRegister(ServiceInstance instance) throws Exception {
        byte[] data = SerializerUtil.serialize(instance);
        String path = ZKPaths.makePath(pathForName(instance.getName()), instance.getId());

        final int MAX_RETRY = 2;
        boolean isDone = false;
        for (int i=0; !isDone && i<MAX_RETRY; i++) {
            try {
                CreateMode mode;
                switch (instance.getServiceType()) {
                    case DYNAMIC: {
                        mode = CreateMode.EPHEMERAL;
                        break;
                    }
                    case DYNAMIC_SEQUENTIAL: {
                        mode = CreateMode.EPHEMERAL_SEQUENTIAL;
                        break;
                    }
                    case PERSISTENT: {
                        mode = CreateMode.PERSISTENT;
                        break;
                    }
                    case PERSISTENT_SEQUENTIAL: {
                        mode = CreateMode.PERSISTENT_SEQUENTIAL;
                        break;
                    }
                    default: {
                        mode = CreateMode.EPHEMERAL;
                        break;
                    }
                }
                client.create().creatingParentContainersIfNeeded().withMode(mode).forPath(path, data);
                isDone = true;
            } catch (KeeperException.NodeExistsException e) {
                client.delete().forPath(path);
            }
        }
    }

    private void doUnregister(ServiceEntry entry) {
        if (entry != null) {
            synchronized (entry) {
                if (entry.cache != null) {
                    CloseableUtil.closeQuietly(entry.cache);
                    entry.cache = null;
                }
                String path = pathForInstance(entry.service.getName(), entry.service.getId());
                try {
                    client.delete().guaranteed().forPath(path);
                } catch (Exception e) {
                    logger.error("delete node exception, ignore it.");
                }
            }
        }
    }

    private NodeCache makeNodeCache(final ServiceInstance instance) {
        if (!watchInstances) {
            return null;
        }
        final NodeCache cache = new NodeCache(client, pathForInstance(instance.getName(), instance.getId()));
        try {
            cache.start(true);
        } catch (Exception e) {
            ThreadUtil.checkInterrupt(e);
            logger.error("Can't start node cache for: " + instance, e);
        }
        NodeCacheListener listener = new NodeCacheListener() {
            public void nodeChanged() throws Exception {
                logger.debug("{} node changed to {}", instance);
                if (cache.getCurrentData() != null) {
                    ServiceInstance newInstance = SerializerUtil.deserialize(cache.getCurrentData().getData());
                    ServiceEntry entry = services.get(newInstance.getId());
                    if (entry != null) {
                        synchronized (entry) {
                            entry.service = newInstance;
                        }
                    }
                } else {
                    logger.warn("instance data has been deleted: {}", instance);
                }
            }
        };
        cache.getListenable().addListener(listener);
        return cache;
    }

    public String pathForName(String name) {
        return ZKPaths.makePath(basePath, name);
    }

    public String pathForInstance(String name, String id) {
        return ZKPaths.makePath(pathForName(name), id);
    }

}
