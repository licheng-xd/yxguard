package com.netease.yxguard.strategy;

import com.netease.yxguard.client.ProviderStrategy;
import com.netease.yxguard.client.ServiceInstance;
import com.netease.yxguard.impl.InstanceProvider;

import java.util.List;

/**
 * 随机策略
 *
 * Created by lc on 16/6/17.
 */
public class ConsistentHashStrategy implements ProviderStrategy {

    @Override
    public ServiceInstance getInstance(InstanceProvider instanceProvider, Object key) throws Exception {
        List<ServiceInstance> instances = instanceProvider.getInstances();
        if (instances.size() == 0) {
            return null;
        }
        return instances.get(getHash(key) % instances.size());
    }

    private int getHash(Object key) {
        return key.hashCode();
    }
}
