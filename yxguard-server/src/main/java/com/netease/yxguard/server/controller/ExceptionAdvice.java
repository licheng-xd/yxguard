package com.netease.yxguard.server.controller;

import com.alibaba.fastjson.JSONException;
import com.netease.yxguard.server.meta.RespBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import javax.servlet.ServletException;

/**
 * Created by lc on 16/1/19.
 */
@ControllerAdvice
public class ExceptionAdvice {
    private static final Logger logger = LoggerFactory.getLogger(
        ExceptionAdvice.class);

    @ResponseBody
    @ExceptionHandler(value = {MissingServletRequestParameterException.class})
    public RespBody handleMissingParameterException(
        MissingServletRequestParameterException e) {
        return RespBody.builder(HttpStatus.BAD_REQUEST).msg("MissingServletRequestParameterException " + e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = {Exception.class})
    public RespBody handleException(Exception e) {
        logger.error(e.getMessage(), e);
        return RespBody.builder(HttpStatus.INTERNAL_SERVER_ERROR).msg("Internal Exception");
    }

    @ResponseBody
    @ExceptionHandler(value = {JSONException.class})
    public RespBody handleJSONException(JSONException e) {
        logger.error(e.getMessage(), e);
        return RespBody.builder(HttpStatus.BAD_REQUEST).msg("JSONException " + e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = {IllegalArgumentException.class})
    public RespBody handleIllegalArgumentException(IllegalArgumentException e) {
        return RespBody.builder(HttpStatus.BAD_REQUEST).msg("IllegalArgumentException " + e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = {ServletException.class})
    public RespBody handleServletException(ServletException e) {
        return RespBody.builder(HttpStatus.BAD_REQUEST).msg("ServletException " + e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = {NoSuchRequestHandlingMethodException.class})
    public RespBody handleNoHandlingException(NoSuchRequestHandlingMethodException e) {
        return RespBody.builder(HttpStatus.BAD_REQUEST).msg("NoSuchRequestHandlingMethodException " + e.getMessage());
    }
}
