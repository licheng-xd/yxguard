package com.netease.yxguard.impl;

import com.google.common.collect.Lists;
import com.netease.yxguard.config.DownInstanceConfig;
import com.netease.yxguard.client.InstanceFilter;
import com.netease.yxguard.client.ProviderStrategy;
import com.netease.yxguard.client.ServiceProvider;
import com.netease.yxguard.strategy.RandomStrategy;

import java.util.List;
import java.util.concurrent.ThreadFactory;

/**
 * {@link ServiceProvider} 构建器
 *
 * Created by lc on 16/6/16.
 */
public class ServiceProviderBuilder {

    private GuardClientImpl guard;

    private String serviceName;

    private ThreadFactory threadFactory;

    private List<InstanceFilter> filters = Lists.newArrayList();

    private DownInstanceConfig config = new DownInstanceConfig();

    private ProviderStrategy strategy = new RandomStrategy();

    ServiceProviderBuilder(GuardClientImpl guard) {
        this.guard = guard;
    }

    /**
     * 指定要发现的服务名
     *
     * @param serviceName
     * @return
     */
    public ServiceProviderBuilder serviceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public ServiceProviderBuilder threadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    public ServiceProviderBuilder instanceFilters(List<InstanceFilter> filters) {
        this.filters = filters;
        return this;
    }

    public ServiceProviderBuilder downInstanceConfig(DownInstanceConfig config) {
        this.config = config;
        return this;
    }

    public ServiceProviderBuilder providerStrategy(ProviderStrategy strategy) {
        this.strategy = strategy;
        return this;
    }

    public ServiceProviderImpl build() {
        return new ServiceProviderImpl(guard, serviceName, threadFactory, filters, config, strategy);
    }


}
