package com.netease.yxguard.demo;

import com.netease.yxguard.client.GuardClient;
import com.netease.yxguard.impl.GuardClientBuilder;
import com.netease.yxguard.client.ServiceCache;
import com.netease.yxguard.client.ServiceInstance;
import com.netease.yxguard.config.GuardConfig;

/**
 * Created by lc on 16/6/21.
 */
public class DemoClient {
    public static void main(String[] args) throws Exception {
        GuardConfig config = GuardConfig.newConfig("http://127.0.0.1:8000/yxguard");
        GuardClient client = GuardClientBuilder
            .builder().config(config)
            .build();
        try {
            client.start();

            ServiceCache cache = client.serviceCacheBuilder()
                .name("demoservice1").build();
            cache.start();
            for (ServiceInstance instance : cache.getInstances()) {
                System.out.println(instance);
            }
        } finally {
            client.close();
        }
    }
}
