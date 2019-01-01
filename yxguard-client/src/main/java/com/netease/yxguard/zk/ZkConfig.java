package com.netease.yxguard.zk;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * zkmanger config
 */
public class ZkConfig implements Cloneable {
    private static final Logger logger = LoggerFactory.getLogger(ZkConfig.class);

    private ZkConfig(Builder builder) {
        this.hosts = builder.hosts;
        this.namespace = builder.namespace;
        this.sessionTimeout = builder.sessionTimeout;
        this.connectionTimeout = builder.connectionTimeout;
        this.baseSleepTimeMs = builder.baseSleepTimeMs;
        this.maxRetry = builder.maxRetry;
        this.charset = builder.charset;
    }

    public static Builder custom() {
        return new Builder();
    }

    /**
     * zk hosts
     */
    private String hosts;

    /**
     * namepsace
     */
    private String namespace;

    /**
     * session timeout
     */
    private int sessionTimeout;

    /**
     * connection timeout
     */
    private int connectionTimeout;

    /**
     * base sleep time when conntection retry
     */
    private int baseSleepTimeMs;

    /**
     * max retry times when connenction broken
     */
    private int maxRetry;

    /**
     * charset store in zk
     */
    private Charset charset;

    public String getHosts() {
        return hosts;
    }

    public String getNamespace() {
        return namespace;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getBaseSleepTimeMs() {
        return baseSleepTimeMs;
    }

    public int getMaxRetry() {
        return maxRetry;
    }

    public Charset getCharset() {
        return charset;
    }

    @Override
    protected ZkConfig clone() throws CloneNotSupportedException {
        return (ZkConfig) super.clone();
    }

    public static class Builder {
        Builder() {
            // init by default config
            sessionTimeout = 5000;
            connectionTimeout = 3000;
            baseSleepTimeMs = 1000;
            maxRetry = 3;
            charset = Charset.forName("UTF-8");
        }
        private String hosts;
        private String namespace;
        private int sessionTimeout;
        private int connectionTimeout;
        private int baseSleepTimeMs;
        private int maxRetry;
        private Charset charset;

        public Builder setHosts(String hosts) {
            this.hosts = hosts;
            return this;
        }

        public Builder setNamespace(String namespace) {
            this.namespace = namespace;
            return this;
        }

        public Builder setSessionTimeout(int sessionTimeout) {
            this.sessionTimeout = sessionTimeout;
            return this;
        }

        public Builder setConnectionTimeout(int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        public Builder setBaseSleepTimeMs(int baseSleepTimeMs) {
            this.baseSleepTimeMs = baseSleepTimeMs;
            return this;
        }

        public Builder setMaxRetry(int maxRetry) {
            this.maxRetry = maxRetry;
            return this;
        }

        public Builder setCharset(Charset charset) {
            this.charset = charset;
            return this;
        }

        public ZkConfig build() {
            if (Strings.isNullOrEmpty(hosts)) {
                logger.error("ZkConfig can't use empty hosts.");
                throw new ZkException("hosts may not be null");
            }
            return new ZkConfig(this);
        }
    }
}
