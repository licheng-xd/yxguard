package com.netease.yxguard.client;

import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.state.ConnectionStateListener;

/**
 * 监听Service变化
 *
 * Created by lc on 16/6/16.
 */
public interface ServiceCacheListener extends ConnectionStateListener {

    public void cacheChanged(PathChildrenCacheEvent event);
}
