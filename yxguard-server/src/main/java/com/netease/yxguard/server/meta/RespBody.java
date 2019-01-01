package com.netease.yxguard.server.meta;

import com.alibaba.fastjson.JSON;
import org.springframework.http.HttpStatus;

/**
 * Created by lc on 14-7-17.
 */
public class RespBody {

    private int code;

    private String msg;

    private Object obj;

    private RespBody(int code) {
        this.code = code;
    }

    private RespBody() {

    }

    public int getCode() {
        return code;
    }

    public RespBody code(int code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public RespBody msg(String msg) {
        this.msg = msg;
        return this;
    }

    public Object getObj() {
        return obj;
    }

    public RespBody obj(Object obj) {
        this.obj = obj;
        return this;
    }

    public static RespBody success() {
        return new RespBody(HttpStatus.OK.value());
    }

    public static RespBody builder(HttpStatus status) {
        return new RespBody(status.value());
    }

    /**
     * 返回jsonp格式数据,前端跨域处理
     *
     * @param callback
     * @param data
     * @return
     */
    public static String jsonp(String callback, String data) {
        return callback + "(" + data + ");";
    }

    @Override public String toString() {
        return JSON.toJSONString(this);
    }
}
