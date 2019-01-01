package com.netease.yxguard.server.controller;

import com.netease.yxguard.server.meta.RespBody;
import com.netease.yxguard.server.service.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 界面展示接口
 *
 * Created by lc on 16/6/21.
 */
@Controller
@RequestMapping(value = {"/yxguard/show"})
public class ShowController {
    private static final Logger logger = LoggerFactory.getLogger(ShowController.class);

    @Autowired
    private ServiceManager manager;

    /**
     * 获取所有可用服务
     */
    @ResponseBody
    @RequestMapping(value = {"/services"}, method = { RequestMethod.GET})
    public RespBody getServices() {
        return RespBody.success().obj(manager.getServices());
    }

    /**
     * 获取所有服务
     */
    @ResponseBody
    @RequestMapping(value = {"/servicesall"}, method = { RequestMethod.GET})
    public RespBody getServicesAll() {
        return RespBody.success().obj(manager.getServicesAll());
    }

    /**
     * 获取服务的所有实例
     */
    @ResponseBody
    @RequestMapping(value = {"/instances/{name}"}, method = { RequestMethod.GET})
    public RespBody getInstances(@PathVariable String name) {
        try {
            return RespBody.success().obj(manager.getInstances(name));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return RespBody.builder(HttpStatus.INTERNAL_SERVER_ERROR).msg(e.getMessage());
        }
    }

    /**
     * 获取数据统计报告
     */
    public void getReport() {

    }
}
