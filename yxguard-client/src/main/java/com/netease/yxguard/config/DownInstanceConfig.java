package com.netease.yxguard.config;

/**
 * 熔断配置,默认30s内出现两次错误就会熔断
 *
 * Created by lc on 16/6/17.
 */
public class DownInstanceConfig {

    private final long timeoutMs;

    private final int errorThreshold;

    private static final long DEFAULT_TIMEOUT_MS = 30000;

    private static final int DEFAULT_THRESHOLD = 2;

    public DownInstanceConfig() {
        this(DEFAULT_TIMEOUT_MS, DEFAULT_THRESHOLD);
    }

    public final static DownInstanceConfig DEFAULT = new DownInstanceConfig();

    public DownInstanceConfig(long timeoutMs, int errorThreshold) {
        this.timeoutMs = timeoutMs;
        this.errorThreshold = errorThreshold;
    }

    public long getTimeoutMs() {
        return timeoutMs;
    }

    public int getErrorThreshold() {
        return errorThreshold;
    }
}
