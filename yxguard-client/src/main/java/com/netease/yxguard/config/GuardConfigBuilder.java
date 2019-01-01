package com.netease.yxguard.config;

/**
 * Created by lc on 16/6/27.
 */
public class GuardConfigBuilder {

    private String serverUrl;

    private String zkAddr;

    private String basePath;

    private int sessionTimeoutMs = 60 * 1000;

    private int connectionTimeoutMs = 15 * 1000;

    private int baseSleepTimeMs = 1000;

    private int maxRetries = 3;

    public GuardConfigBuilder serverUrl(String serverUrl) {
        this.serverUrl = serverUrl;
        return this;
    }

    public GuardConfigBuilder zkAddr(String zkAddr) {
        this.zkAddr = zkAddr;
        return this;
    }

    public GuardConfigBuilder basePath(String basePath) {
        this.basePath = basePath;
        return this;
    }

    public GuardConfigBuilder sessionTimeoutMs(int sessionTimeoutMs) {
        this.sessionTimeoutMs = sessionTimeoutMs;
        return this;
    }

    public GuardConfigBuilder connectionTimeoutMs(int connectionTimeoutMs) {
        this.connectionTimeoutMs = connectionTimeoutMs;
        return this;
    }

    public GuardConfigBuilder baseSleepTimeMs(int baseSleepTimeMs) {
        this.baseSleepTimeMs = baseSleepTimeMs;
        return this;
    }

    public GuardConfigBuilder maxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
        return this;
    }

    public GuardConfig build() {
        return new GuardConfig(serverUrl, zkAddr, basePath, sessionTimeoutMs, connectionTimeoutMs, baseSleepTimeMs, maxRetries);
    }
}
