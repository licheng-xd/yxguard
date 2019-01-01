package com.netease.yxguard.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * zk operations by curator
 *
 * Created by lc on 16/6/3.
 */
public class ZkManager {
    private static final Logger logger = LoggerFactory.getLogger(ZkManager.class);

    private CuratorFramework client;

    private Charset charset = Charset.forName("UTF-8");

    public ZkManager(ZkConfig config) {
        init(config);
    }

    private void init(ZkConfig config) {
        charset = config.getCharset();
        client = CuratorFrameworkFactory.builder()
            .connectString(config.getHosts())
            .namespace(config.getNamespace())
            .sessionTimeoutMs(config.getSessionTimeout())
            .connectionTimeoutMs(config.getConnectionTimeout())
            .retryPolicy(new ExponentialBackoffRetry(config.getBaseSleepTimeMs(), config.getMaxRetry()))
            .build();
        client.start();
    }

    public void create(String path, String value) {
        try {
            client.create().creatingParentsIfNeeded().forPath(path, value.getBytes(charset));
        } catch (Exception e) {
            logger.error("create failed. " + e.getMessage(), e);
            throw new ZkException("create failed", e);
        }
    }

    public void createWithMode(String path, CreateMode mode, String value) {
        try {
            client.create().withMode(mode).forPath(path, value.getBytes(charset));
        } catch (Exception e) {
            logger.error("createWithMode failed. " + e.getMessage(), e);
            throw new ZkException("createWithMode failed", e);
        }
    }

    public void set(String path, String value) {
        try {
            client.setData().forPath(path, value.getBytes(charset));
        } catch (Exception e) {
            logger.error("write failed. " + e.getMessage(), e);
            throw new ZkException("write failed", e);
        }
    }

    public boolean exist(String path) {
        try {
            return client.checkExists().forPath(path) != null;
        } catch (Exception e) {
            logger.error("exists failed. " + e.getMessage(), e);
            throw new ZkException("exists failed", e);
        }
    }

    public void delete(String path) {
        try {
            client.delete().deletingChildrenIfNeeded().forPath(path);
        } catch (Exception e) {
            logger.error("delete failed. " + e.getMessage(), e);
            throw new ZkException("delete failed", e);
        }
    }

    public void close() {
        CloseableUtils.closeQuietly(client);
    }

}
