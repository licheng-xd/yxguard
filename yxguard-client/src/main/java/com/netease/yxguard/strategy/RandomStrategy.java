package com.netease.yxguard.strategy;

import com.netease.yxguard.client.ProviderStrategy;
import com.netease.yxguard.client.ServiceInstance;
import com.netease.yxguard.impl.InstanceProvider;

import java.util.List;
import java.util.Random;

/**
 * 随机策略
 *
 * Created by lc on 16/6/17.
 */
public class RandomStrategy implements ProviderStrategy {

    private Random random = new Random();

    @Override
    public ServiceInstance getInstance(InstanceProvider instanceProvider, Object key) throws Exception {
        List<ServiceInstance> instances = instanceProvider.getInstances();
        if (instances.size() == 0) {
            return null;
        }
        int index = random.nextInt(instances.size());
        return instances.get(index);
    }
}
