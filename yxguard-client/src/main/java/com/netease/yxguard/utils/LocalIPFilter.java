package com.netease.yxguard.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

/**
 * Created by lc on 16/6/4.
 */
public interface LocalIPFilter {
    boolean use(NetworkInterface networkInterface, InetAddress address) throws SocketException;
}
