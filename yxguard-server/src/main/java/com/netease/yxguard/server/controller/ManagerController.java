package com.netease.yxguard.server.controller;

import com.netease.yxguard.client.ServiceInstance;
import com.netease.yxguard.server.meta.RespBody;
import com.netease.yxguard.server.service.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 服务管理接口
 *
 * Created by lc on 16/6/21.
 */
@Controller
@RequestMapping(value = {"/yxguard/manager"})
public class ManagerController {
    private static final Logger logger = LoggerFactory.getLogger(ManagerController.class);

    @Autowired
    private ServiceManager manager;

    /**
     * 下线服务实例
     */
    @ResponseBody
    @RequestMapping(value = {"/remove/{name}/{instanceid}"}, method = { RequestMethod.GET})
    public RespBody remove(@PathVariable String name, @PathVariable String instanceid) {
        try {
            ServiceInstance instance = manager.removeInstance(name, instanceid);
            return RespBody.success().obj(instance);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return RespBody.builder(HttpStatus.INTERNAL_SERVER_ERROR).msg(e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping(value = {"/register"}, method = { RequestMethod.POST})
    public RespBody register(@RequestParam String name,
        @RequestParam(required = false, defaultValue = "") String instanceid) {
        //TODO 通过web接口注册服务
        return null;
    }


}
