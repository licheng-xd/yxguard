/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.netease.yxguard.strategy;

import com.netease.yxguard.client.ProviderStrategy;
import com.netease.yxguard.client.ServiceInstance;
import com.netease.yxguard.impl.InstanceProvider;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 一致性策略
 *
 * Created by lc on 16/6/17.
 */
public class StickyStrategy implements ProviderStrategy {
    private final ProviderStrategy masterStrategy;
    private final AtomicReference<ServiceInstance> ourInstance = new AtomicReference<ServiceInstance>(null);
    private final AtomicInteger instanceNumber = new AtomicInteger(-1);

    /**
     * @param masterStrategy 选择新实例时的策略
     */
    public StickyStrategy(ProviderStrategy masterStrategy) {
        this.masterStrategy = masterStrategy;
    }

    @Override
    public ServiceInstance getInstance(InstanceProvider instanceProvider, Object key) throws Exception {
        final List<ServiceInstance> instances = instanceProvider.getInstances();
        ServiceInstance localOurInstance = ourInstance.get();

        if (!instances.contains(localOurInstance)) {
            ourInstance.compareAndSet(localOurInstance, null);
        }

        if (ourInstance.get() == null) {
            ServiceInstance instance = masterStrategy.getInstance(new InstanceProvider() {
                    @Override
                    public List<ServiceInstance> getInstances() throws Exception {
                       return instances;
                    }
                }
            , key);
            if (ourInstance.compareAndSet(null, instance)) {
                instanceNumber.incrementAndGet();
            }
        }
        return ourInstance.get();
    }

    /**
     * 记录实例变更次数
     *
     * @return instance number
     */
    public int getInstanceNumber() {
        return instanceNumber.get();
    }
}
