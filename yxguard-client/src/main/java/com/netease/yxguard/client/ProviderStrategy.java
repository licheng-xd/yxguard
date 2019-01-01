package com.netease.yxguard.client;

import com.netease.yxguard.impl.InstanceProvider;

/**
 * Created by lc on 16/6/16.
 */
public interface ProviderStrategy {

    ServiceInstance getInstance(InstanceProvider instanceProvider, Object key) throws Exception;
}
