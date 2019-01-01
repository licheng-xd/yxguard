package com.netease.yxguard.client;

import com.netease.yxguard.impl.InstanceProvider;
import org.apache.curator.framework.listen.Listenable;

import java.io.Closeable;

/**
 * Created by lc on 16/6/16.
 */
public interface ServiceCache extends Closeable, Listenable<ServiceCacheListener>, InstanceProvider {

    /**
     * 使用前必须先调用start方法
     *
     * @throws Exception
     */
    public void start() throws Exception;

    public String getName();
}
