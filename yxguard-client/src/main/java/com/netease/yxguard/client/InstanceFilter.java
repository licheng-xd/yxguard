package com.netease.yxguard.client;

import com.google.common.base.Predicate;

/**
 * 服务过滤器接口,用guava的Predicate实现,apply接口返回true表示可用
 *
 * Created by lc on 16/6/16.
 */
public interface InstanceFilter extends Predicate<ServiceInstance> {

}
