package com.netease.yxguard.impl;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.netease.yxguard.client.InstanceFilter;
import com.netease.yxguard.client.ServiceInstance;

import java.util.List;

/**
 * Created by lc on 16/6/16.
 */
public class FilterInstanceProvider implements InstanceProvider {

    private InstanceProvider cache;

    private Predicate<ServiceInstance> predicates;

    public FilterInstanceProvider(InstanceProvider cache, List<InstanceFilter> filters) {
        this.cache = cache;
        predicates = Predicates.and(filters);
    }

    public FilterInstanceProvider(InstanceProvider cache, Predicate<ServiceInstance> predicates) {
        this.cache = cache;
        this.predicates = predicates;
    }

    public List<ServiceInstance> getInstances() throws Exception {
        Iterable<ServiceInstance> filtered = Iterables.filter(cache.getInstances(), predicates);
        return ImmutableList.copyOf(filtered);
    }

    public void addFilter(InstanceFilter filter) {
        predicates = Predicates.and(predicates, filter);
    }

    public Predicate<ServiceInstance> getFilters() {
        return predicates;
    }
}
