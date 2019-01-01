package com.netease.yxguard.impl;

import java.util.concurrent.ThreadFactory;

/**
 * Created by lc on 16/6/16.
 */
public class ServiceCacheBuilder {

    private GuardClientImpl guard;
    private String name;
    private ThreadFactory threadFactory;

    ServiceCacheBuilder(GuardClientImpl guard) {
        this.guard = guard;
    }

    public ServiceCacheImpl build() {
        return new ServiceCacheImpl(guard, name, threadFactory);
    }

    public ServiceCacheBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ServiceCacheBuilder threadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

}
