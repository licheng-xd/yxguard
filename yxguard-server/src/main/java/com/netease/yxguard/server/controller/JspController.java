package com.netease.yxguard.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by lc on 16/6/25.
 */
@Controller
public class JspController {

    @RequestMapping("/")
    public String index() {
        return "index";
    }
}
