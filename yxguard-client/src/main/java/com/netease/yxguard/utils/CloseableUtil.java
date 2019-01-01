package com.netease.yxguard.utils;

import com.google.common.io.Closeables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * CloseableUtil
 *
 * Created by lc on 16/6/16.
 */
public final class CloseableUtil {
    private static final Logger logger = LoggerFactory.getLogger(CloseableUtil.class);

    public static void closeQuietly(Closeable closeable) {
        try {
            Closeables.close(closeable, true);
        } catch (IOException e) {
            logger.error("IOException thrown, this should not happen.", e);
        }
    }
}
