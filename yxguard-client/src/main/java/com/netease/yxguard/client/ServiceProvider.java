package com.netease.yxguard.client;

import java.io.Closeable;
import java.util.List;

/**
 * 服务提供入口,通常情况下服务调用方应使用这个入口实现服务发现功能
 *
 * Created by lc on 16/6/16.
 */
public interface ServiceProvider extends Closeable {

    /**
     * 使用前必须先调用start
     *
     * @throws Exception
     */
    void start() throws Exception;

    /**
     * 根据设置的均衡策略获取一个服务实例,默认随机策略
     *
     * @return
     * @throws Exception
     */
    ServiceInstance getInstance() throws Exception;

    /**
     * 根据传入的key值通过均衡策略获取一个服务实例
     *
     * @return
     * @throws Exception
     */
    ServiceInstance getInstance(Object key) throws Exception;

    /**
     * 根据过滤条件获取instance
     *
     * @param filter
     * @return
     * @throws Exception
     */
    ServiceInstance getInstanceWithFilter(InstanceFilter filter) throws Exception;

    /**
     * 根据过滤条件获取instance
     *
     * @param filter
     * @param key
     * @return
     * @throws Exception
     */
    ServiceInstance getInstanceWithFilter(InstanceFilter filter, Object key) throws Exception;

    /**
     * 获取过滤后的所有服务实例,如果想要获取没有经过过滤器的,应该直接使用{@link ServiceCache}
     *
     * @return
     * @throws Exception
     */
    List<ServiceInstance> getAllInstances() throws Exception;

    /**
     * 标记服务异常,用于熔断过滤
     *
     * @param instance
     */
    void noteError(ServiceInstance instance);
}
