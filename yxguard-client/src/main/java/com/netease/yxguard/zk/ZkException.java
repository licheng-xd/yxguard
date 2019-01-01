package com.netease.yxguard.zk;

/**
 * zk exception
 *
 * Created by lc on 16/6/4.
 */
public class ZkException extends IllegalArgumentException {

    public ZkException(String msg) {
        super(msg);
    }

    public ZkException(String msg, Throwable e) {
        super(msg, e);
    }
}
