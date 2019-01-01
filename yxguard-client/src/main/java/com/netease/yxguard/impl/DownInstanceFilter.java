package com.netease.yxguard.impl;

import com.google.common.collect.Maps;
import com.netease.yxguard.config.DownInstanceConfig;
import com.netease.yxguard.client.InstanceFilter;
import com.netease.yxguard.client.ServiceInstance;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 熔断过滤器,timeoutMs时间内最多只能出现errCount次错误,如果达到,在timeoutMs时间内,服务将会无效
 *
 * Created by lc on 16/6/16.
 */
public class DownInstanceFilter implements InstanceFilter {

    private ConcurrentMap<ServiceInstance, Status> statusMap = Maps.newConcurrentMap();

    private DownInstanceConfig config;

    private AtomicLong lastPurge = new AtomicLong(System.currentTimeMillis());

    private class Status {
        private long startMs = System.currentTimeMillis();

        private AtomicInteger errCount = new AtomicInteger();
    }

    public DownInstanceFilter(DownInstanceConfig config) {
        this.config = config;
    }

    public void add(ServiceInstance instance) {
        purge();

        Status newStatus = new Status();
        Status oldStatus = statusMap.putIfAbsent(instance, newStatus);
        Status useStatus = oldStatus == null ? newStatus : oldStatus;
        useStatus.errCount.incrementAndGet();
    }

    @Override
    public boolean apply(ServiceInstance instance) {
        Status status = statusMap.get(instance);
        return status == null || status.errCount.get() < config.getErrorThreshold();
    }

    private void purge() {
        long last = lastPurge.get();
        if (System.currentTimeMillis() - last < config.getTimeoutMs() / 2) {
            return;
        }
        lastPurge.set(System.currentTimeMillis());
        Iterator<Map.Entry<ServiceInstance, Status>> iter = statusMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<ServiceInstance, Status> entry = iter.next();
            if (System.currentTimeMillis() - entry.getValue().startMs > config.getTimeoutMs()) {
                iter.remove();
            }
        }
    }
}
