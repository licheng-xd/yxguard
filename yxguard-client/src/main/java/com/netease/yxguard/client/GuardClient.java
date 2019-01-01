package com.netease.yxguard.client;

import com.netease.yxguard.impl.ServiceCacheBuilder;
import com.netease.yxguard.impl.ServiceProviderBuilder;
import org.apache.curator.framework.CuratorFramework;

import java.io.Closeable;
import java.util.Collection;

/**
 * yxguard入口
 *
 * Created by lc on 16/6/5.
 */
public interface GuardClient extends Closeable {

    /**
     * 使用前必须先调用start方法
     *
     * @throws Exception
     */
    void start() throws Exception;


    /**
     * 注册服务
     *
     * @param instance
     * @throws Exception
     */
    void register(ServiceInstance instance) throws Exception;

    /**
     * 注销服务
     *
     * @param instance
     * @throws Exception
     */
    void unregister(ServiceInstance instance) throws Exception;

    /**
     * 更新服务
     *
     * @param instance
     * @throws Exception
     */
    void update(ServiceInstance instance) throws Exception;

    /**
     * 获取所有可用的服务名
     *
     * @return
     * @throws Exception
     */
    Collection<String> getAllServiceName() throws Exception;

    /**
     * 根据服务名获取所有服务实例
     *
     * @param name
     * @return
     * @throws Exception
     */
    Collection<ServiceInstance> getInstancesByName(String name) throws Exception;

    /**
     * 根据服务名和id获取对应的服务实例
     *
     * @param name
     * @param id
     * @return
     * @throws Exception
     */
    ServiceInstance getInstanceByNameId(String name, String id) throws Exception;

    // for builder
    /**
     * 返回{@link ServiceCacheBuilder}实例
     *
     * @return
     */
    ServiceCacheBuilder serviceCacheBuilder();

    /**
     * 返回{@link ServiceProviderBuilder}实例
     *
     * @return
     */
    ServiceProviderBuilder serviceProviderBuilder();

    /**
     * 返回zk操作客户端
     *
     * @return
     */
    CuratorFramework getClient();

    String pathForName(String name);

    String pathForInstance(String name, String id);
}
