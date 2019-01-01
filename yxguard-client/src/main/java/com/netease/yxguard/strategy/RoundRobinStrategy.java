package com.netease.yxguard.strategy;

import com.netease.yxguard.client.ProviderStrategy;
import com.netease.yxguard.client.ServiceInstance;
import com.netease.yxguard.impl.InstanceProvider;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询策略
 *
 * Created by lc on 16/6/17.
 */
public class RoundRobinStrategy implements ProviderStrategy {

    private AtomicInteger index = new AtomicInteger();

    @Override
    public ServiceInstance getInstance(InstanceProvider instanceProvider, Object key) throws Exception {
        List<ServiceInstance> instances = instanceProvider.getInstances();
        if (instances.isEmpty()) {
            return null;
        }
        int thisIdx = Math.abs(index.getAndIncrement());
        return instances.get(thisIdx % instances.size());
    }
}
