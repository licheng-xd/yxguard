package com.netease.yxguard.utils;

import com.google.common.base.Throwables;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;

/**
 * 基于guava的线程工具
 *
 * Created by lc on 16/6/16.
 */
public final class ThreadUtil {
    private static final Logger logger = LoggerFactory.getLogger(ThreadUtil.class);

    public static void checkInterrupt(Exception e) {
        if (e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
    }

    public static ThreadFactory newThreadFactory(String processName) {
        return newGenericThreadFactory("YxGuard-" + processName);
    }

    public static ThreadFactory newGenericThreadFactory(String processName) {
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                logger.error("Unexpected exception in thread: " + t, e);
                Throwables.propagate(e);
            }
        };
        return new ThreadFactoryBuilder()
            .setNameFormat(processName + "-%d")
            .setDaemon(true)
            .setUncaughtExceptionHandler(uncaughtExceptionHandler)
            .build();
    }
}
