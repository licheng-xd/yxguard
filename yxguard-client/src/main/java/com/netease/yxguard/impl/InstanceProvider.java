package com.netease.yxguard.impl;

import com.netease.yxguard.client.ServiceInstance;

import java.util.List;

/**
 * Created by lc on 16/6/16.
 */
public interface InstanceProvider {

    List<ServiceInstance> getInstances() throws Exception;
}
