package com.netease.yxguard.server.service;

import com.netease.yxguard.client.ServiceInstance;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.springframework.stereotype.Service;

/**
 * 报警
 *
 * Created by lc on 16/6/22.
 */
@Service
public class AlertService {

    public void newService(String serviceName) {

    }

    public void removeService(String serviceName) {

    }

    public void instanceChanged(ServiceInstance instance, PathChildrenCacheEvent.Type type) {
        switch (type) {
            case CHILD_ADDED: {
                instanceAdd(instance);
                break;
            }
            case CHILD_UPDATED: {
                instanceUpdate(instance);
                break;
            }
            case CHILD_REMOVED: {
                instanceDown(instance);
                break;
            }
        }
    }

    public void instanceAdd(ServiceInstance instance) {

    }

    public void instanceDown(ServiceInstance instance) {

    }

    public void instanceUpdate(ServiceInstance instance) {

    }
}
