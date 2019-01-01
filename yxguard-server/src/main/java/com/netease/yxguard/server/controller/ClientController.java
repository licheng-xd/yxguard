package com.netease.yxguard.server.controller;

import com.netease.yxguard.config.GuardConfig;
import com.netease.yxguard.server.meta.RespBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * yxguard-client接口
 *
 * Created by lc on 16/6/21.
 */
@Controller
@RequestMapping(value = {"/yxguard/client"})
public class ClientController {

    @Autowired
    private GuardConfig config;

    /**
     * 获取配置
     */
    @ResponseBody
    @RequestMapping(value = {"/config"}, method = { RequestMethod.GET})
    public RespBody getConfig() {
        return RespBody.success().obj(config);
    }

    /**
     * 通过web接口发现服务
     */
    public void discover() {

    }

    /**
     * 汇报统计数据
     */
    public void report() {

    }


}
