package com.netease.yxguard.demo;

import com.netease.yxguard.client.GuardClient;
import com.netease.yxguard.client.ServiceInstance;
import com.netease.yxguard.config.GuardConfig;
import com.netease.yxguard.impl.GuardClientBuilder;

/**
 * 测试用
 *
 * Created by lc on 16/6/21.
 */
public class DemoServer {
    public static void main(String[] args) throws Exception {
        GuardConfig config = GuardConfig.newConfig("http://127.0.0.1:8000/yxguard");
        GuardClient client = GuardClientBuilder
            .builder().config(config)
            .build();
        try {
            client.start();

            ServiceInstance instance = ServiceInstance.builder()
                .name("demoservice2").port(2016).build();

            client.register(instance);
            System.out.println("demo server start");
            Thread.sleep(Integer.MAX_VALUE);
        } finally {
            client.close();
        }
    }

}
