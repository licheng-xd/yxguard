package com.netease.yxguard.impl;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.netease.yxguard.client.*;
import com.netease.yxguard.config.DownInstanceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;

/**
 * Created by lc on 16/6/16.
 */
public class ServiceProviderImpl implements ServiceProvider {
    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderImpl.class);

    private final GuardClientImpl guard;

    private final ServiceCacheImpl cache; // 服务缓存

    private final FilterInstanceProvider instanceProvider;  // 所有过滤器,包含了下面的熔断过滤器

    private final DownInstanceFilter downInstanceFilter; // 熔断过滤器

    private final ProviderStrategy providerStrategy; // 服务选择策略

    ServiceProviderImpl(GuardClientImpl guard, String serviceName, ThreadFactory threadFactory, List<InstanceFilter> filters, DownInstanceConfig downInstanceConfig, ProviderStrategy providerStrategy) {
        this.guard = guard;
        cache = guard.serviceCacheBuilder().threadFactory(threadFactory).name(serviceName).build();
        downInstanceFilter = new DownInstanceFilter(downInstanceConfig);
        ArrayList<InstanceFilter> localFilters = Lists.newArrayList(filters);
        localFilters.add(downInstanceFilter);
        this.instanceProvider = new FilterInstanceProvider(cache, localFilters);
        this.providerStrategy = providerStrategy;
    }

    /**
     * 使用前必须先调用start
     *
     * @throws Exception
     */
    @Override
    public void start() throws Exception {
        cache.start();
        guard.providerOpened(this);
    }

    /**
     * 根据设置的均衡策略获取一个服务实例,默认随机策略
     *
     * @return
     * @throws Exception
     */
    @Override
    public ServiceInstance getInstance() throws Exception {
        return getInstance(null);
    }

    @Override
    public ServiceInstance getInstance(Object key) throws Exception {
        return providerStrategy.getInstance(instanceProvider, key);
    }

    @Override
    public ServiceInstance getInstanceWithFilter(InstanceFilter filter) throws Exception {
        return getInstanceWithFilter(filter, null);
    }

    @Override
    public ServiceInstance getInstanceWithFilter(InstanceFilter filter, Object key) throws Exception {
        InstanceProvider provider = new FilterInstanceProvider(cache, Predicates.and(instanceProvider.getFilters(), filter));
        return providerStrategy.getInstance(provider, key);
    }

    /**
     * 获取过滤后的所有服务实例,如果想要获取没有经过过滤器的,应该直接使用{@link ServiceCache}
     *
     * @return
     * @throws Exception
     */
    @Override
    public List<ServiceInstance> getAllInstances() throws Exception {
        return instanceProvider.getInstances();
    }

    /**
     * 标记服务异常,用于熔断过滤
     *
     * @param instance
     */
    @Override
    public void noteError(ServiceInstance instance) {
        downInstanceFilter.add(instance);
    }

    /**
     * 关闭Provider
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        guard.providerClosed(this);
        cache.close();
    }

}
