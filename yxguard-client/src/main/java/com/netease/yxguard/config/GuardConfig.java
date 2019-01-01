package com.netease.yxguard.config;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by lc on 16/6/21.
 */
public class GuardConfig {

    /**
     * 只用于guard-client
     */
    private String serverUrl;

    private String zkAddr;

    private String basePath;

    private int sessionTimeoutMs;

    private int connectionTimeoutMs;

    private int baseSleepTimeMs;

    private int maxRetries;

    public GuardConfig() {

    }

    GuardConfig(String serverUrl, String zkAddr, String basePath, int sessionTimeoutMs, int connectionTimeoutMs,
        int baseSleepTimeMs, int maxRetries) {
        this.serverUrl = serverUrl;
        this.zkAddr = zkAddr;
        this.basePath = basePath;
        this.sessionTimeoutMs = sessionTimeoutMs;
        this.connectionTimeoutMs = connectionTimeoutMs;
        this.baseSleepTimeMs = baseSleepTimeMs;
        this.maxRetries = maxRetries;
    }

    public String getZkAddr() {
        return zkAddr;
    }

    public void setZkAddr(String zkAddr) {
        this.zkAddr = zkAddr;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public int getSessionTimeoutMs() {
        return sessionTimeoutMs;
    }

    public void setSessionTimeoutMs(int sessionTimeoutMs) {
        this.sessionTimeoutMs = sessionTimeoutMs;
    }

    public int getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }

    public void setConnectionTimeoutMs(int connectionTimeoutMs) {
        this.connectionTimeoutMs = connectionTimeoutMs;
    }

    public int getBaseSleepTimeMs() {
        return baseSleepTimeMs;
    }

    public void setBaseSleepTimeMs(int baseSleepTimeMs) {
        this.baseSleepTimeMs = baseSleepTimeMs;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public static GuardConfigBuilder builder() {
        return new GuardConfigBuilder();
    }

    public static GuardConfig newConfig(String serverUrl) {
        GuardConfig config = new GuardConfig();
        config.setServerUrl(serverUrl);
        return config;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public static GuardConfig fromJSON(String json) {
        return JSON.parseObject(json, GuardConfig.class);
    }

    public GuardConfig load(String path) {
        Properties prop = new Properties();
        try {
            prop.load(getClass().getClassLoader().getResourceAsStream(path));
            this.zkAddr = prop.getProperty("zkAddr");
            this.serverUrl = prop.getProperty("serverUrl");
            this.basePath = prop.getProperty("basePath");
            return this;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
