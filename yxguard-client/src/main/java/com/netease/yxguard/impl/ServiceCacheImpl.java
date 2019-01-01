package com.netease.yxguard.impl;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.netease.yxguard.client.ServiceCache;
import com.netease.yxguard.client.ServiceCacheListener;
import com.netease.yxguard.client.ServiceInstance;
import com.netease.yxguard.utils.CloseableUtil;
import com.netease.yxguard.utils.SerializerUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.utils.CloseableExecutorService;
import org.apache.curator.utils.ZKPaths;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by lc on 16/6/16.
 */
public class ServiceCacheImpl implements ServiceCache, PathChildrenCacheListener {

    private GuardClientImpl guard;

    private PathChildrenCache cache;

    private String name;

    private AtomicReference<State> state = new AtomicReference<State>(State.INIT);

    private ConcurrentMap<String, ServiceInstance> instances = Maps.newConcurrentMap();

    private final Set<ServiceCacheListener> listeners = Sets.newConcurrentHashSet();

    private enum State {
        INIT,
        STARTED,
        STOPPED;
    }

    ServiceCacheImpl(GuardClientImpl guard, String name, ThreadFactory threadFactory) {
        this.guard = Preconditions.checkNotNull(guard, "guard should not be null");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "name cannot be null");
        Preconditions.checkNotNull(threadFactory, "threadFactory cannot be null");
        this.name = name;
        cache = new PathChildrenCache(guard.getClient(), guard.pathForName(name), true, false, new CloseableExecutorService(
            Executors.newSingleThreadExecutor(threadFactory)));
        cache.getListenable().addListener(this);
    }

    @Override
    public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
        boolean needNotifyListener = false;
        switch (event.getType()) {
            case CHILD_ADDED:
            case CHILD_UPDATED: {
                addInstance(event.getData(), false);
                needNotifyListener = true;
                break;
            }
            case CHILD_REMOVED: {
                instances.remove(getInstanceIdFromData(event.getData()));
                needNotifyListener = true;
                break;
            }
        }
        // 通知listener
        if (needNotifyListener) {
            for (ServiceCacheListener listener : listeners) {
                listener.cacheChanged(event);
            }
        }

    }

    @Override
    public List<ServiceInstance> getInstances() {
        return Lists.newArrayList(instances.values());
    }

    @Override
    public void start() throws Exception {
        // 检查设置状态
        Preconditions.checkState(state.compareAndSet(State.INIT, State.STARTED), "ServiceCache can't be started more than once.");
        // 启动时初始化节点信息
        cache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        for (ChildData childData : cache.getCurrentData()) {
            addInstance(childData, true);
        }
        guard.cacheOpened(this);
    }


    public void close() throws IOException {
        Preconditions.checkState(state.compareAndSet(State.STARTED, State.STOPPED), "Already closed or has not been started");
        for (ServiceCacheListener listener : listeners) {
            guard.getClient().getConnectionStateListenable().removeListener(listener);
        }
        listeners.clear();
        CloseableUtil.closeQuietly(cache);
        guard.cacheClosed(this);
    }

    @Override
    public void addListener(ServiceCacheListener serviceCacheListener) {
        listeners.add(serviceCacheListener);
        guard.getClient().getConnectionStateListenable().addListener(serviceCacheListener);
    }

    @Override
    public void addListener(ServiceCacheListener serviceCacheListener, Executor executor) {
        listeners.add(serviceCacheListener);
        guard.getClient().getConnectionStateListenable().addListener(serviceCacheListener, executor);
    }

    @Override
    public void removeListener(ServiceCacheListener serviceCacheListener) {
        listeners.remove(serviceCacheListener);
        guard.getClient().getConnectionStateListenable().removeListener(serviceCacheListener);
    }

    private static String getInstanceIdFromData(ChildData childData) {
        return ZKPaths.getNodeFromPath(childData.getPath());
    }

    private void addInstance(ChildData childData, boolean ifAbsent)
        throws Exception {
        String id = getInstanceIdFromData(childData);
        ServiceInstance instance = SerializerUtil.deserialize(childData.getData());
        if (ifAbsent) {
            instances.putIfAbsent(id, instance);
        } else {
            instances.put(id, instance);
        }
        cache.clearDataBytes(childData.getPath(), childData.getStat().getVersion());
    }

    public String getName() {
        return this.name;
    }
}
