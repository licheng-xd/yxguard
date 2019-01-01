package com.netease.yxguard.impl;

import com.netease.yxguard.client.GuardClient;
import com.netease.yxguard.config.GuardConfig;

/**
 * {@link GuardClient} 构建器
 *
 * Created by lc on 16/6/16.
 */
public class GuardClientBuilder {

    private GuardConfig config;

    private boolean watchInstances = false;

    private GuardClientBuilder() {
    }

    public static GuardClientBuilder builder() {
        return new GuardClientBuilder();
    }

    public GuardClientImpl build() {
        return new GuardClientImpl(config, watchInstances);
    }

    public GuardClientBuilder config(GuardConfig config) {
        this.config = config;
        return this;
    }

    public GuardClientBuilder watchInstances(boolean watchInstances) {
        this.watchInstances = watchInstances;
        return this;
    }
}
