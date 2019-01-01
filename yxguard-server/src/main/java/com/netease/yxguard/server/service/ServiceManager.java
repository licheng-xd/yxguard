package com.netease.yxguard.server.service;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.netease.yxguard.client.GuardClient;
import com.netease.yxguard.client.ServiceCache;
import com.netease.yxguard.client.ServiceCacheListener;
import com.netease.yxguard.client.ServiceInstance;
import com.netease.yxguard.config.GuardConfig;
import com.netease.yxguard.impl.GuardClientBuilder;
import com.netease.yxguard.utils.CloseableUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.utils.ZKPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 服务管理
 *
 * Created by lc on 16/6/21.
 */
//@Service("manager")
@SuppressWarnings("ALL")
public class ServiceManager implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(ServiceManager.class);

    private GuardClient client;

    private Map<String, ServiceCache> caches = Maps.newHashMap();

    private GuardConfig config;

    @Autowired
    private AlertService alertService;

    public ServiceManager(GuardConfig config) throws Exception {
        logger.info("init service manager with config: {}", config);
        this.config = config;
        client = GuardClientBuilder.builder().config(config).watchInstances(true).build();
        client.start();
        init();
    }

    public void init() throws Exception {
        // 监听服务列表
        PathChildrenCache rootCache = new PathChildrenCache(client.getClient(), config.getBasePath(), true);
        // 初始化服务列表
        rootCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        for (ChildData data : rootCache.getCurrentData()) {
            addService(data);
        }

        rootCache.getListenable().addListener(
            new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework curatorFramework,
                    PathChildrenCacheEvent event)
                    throws Exception {
                    // 刷新服务列表
                    logger.debug("service changed event: {}", event.getType());
                    String name = ZKPaths.getNodeFromPath(event.getData().getPath());
                    switch (event.getType()) {
                        case CHILD_UPDATED: {
                            logger.info("service updated: {}", name);
                            break;
                        }
                        case CHILD_ADDED: {
                            logger.info("service added: {}", name);
                            addService(event.getData());
                            alertService.newService(name);
                            break;
                        }
                        case CHILD_REMOVED: {
                            logger.info("service removed: {}", name);
                            caches.remove(name);
                            alertService.removeService(name);
                            break;
                        }
                    }
                }
            });
    }

    private void addService(ChildData data) {
        String name = ZKPaths.getNodeFromPath(data.getPath());
        if (!caches.containsKey(name)) {
            logger.info("new service: {}", name);
            final ServiceCache cache = client.serviceCacheBuilder().name(name)
                .build();
            cache.addListener(new ServiceCacheListener() {
                @Override
                public void cacheChanged(PathChildrenCacheEvent event) {
                    // 服务节点有变化
                    logger.info("service instance changed, name:{} type:{}", cache.getName(), event.getType());
                    try {
//                        ServiceInstance instance = SerializerUtil.deserialize(event.getData().getData());
//                        alertService.instanceChanged(instance, event.getType());
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }

                @Override
                public void stateChanged(CuratorFramework curatorFramework,
                    ConnectionState connectionState) {

                }
            });
            try {
                cache.start();
                caches.put(name, cache);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public Set<String> getServices() {
        // 过滤掉没有实例的服务
        Iterable<ServiceCache> filtered = Iterables.filter(caches.values(),
            new Predicate<ServiceCache>() {
                @Override
                public boolean apply(ServiceCache cache) {
                    try {
                        return cache.getInstances().size() > 0;
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        return false;
                    }
                }
            });

        Iterable<String> names = Iterables.transform(filtered, new Function<ServiceCache, String>() {
            @Override
            public String apply(ServiceCache serviceCache) {
                return serviceCache.getName();
            }
        });
        return ImmutableSet.copyOf(names);
    }

    public Set<String> getServicesAll() {
        return ImmutableSet.copyOf(caches.keySet());
    }

    public List<ServiceInstance> getInstances(String name) throws Exception {
        if (caches.containsKey(name)) {
            return caches.get(name).getInstances();
        }
        return Lists.newArrayList();
    }

    public ServiceInstance removeInstance(String name, String instanceid) throws Exception {
        ServiceInstance instance = client.getInstanceByNameId(name, instanceid);
        if (instance != null) {
            String path = client.pathForInstance(name, instanceid);
            try {
                client.getClient().delete().guaranteed().forPath(path);
            } catch (Exception e) {
                logger.error("delete node exception, ignore it.");
            }
        }
        return instance;
    }

    @Override
    public void close() throws IOException {
        CloseableUtil.closeQuietly(client);
    }
}
