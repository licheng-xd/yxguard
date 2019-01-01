package com.netease.yxguard.utils;

import java.util.regex.Pattern;

/**
 * Created by lc on 16/6/21.
 */
public class RegexUtil {

    private static final String ipDigitPattern = "(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)";

    private static final Pattern internalIpPattern = Pattern
        .compile(String
            .format(
                "(10(\\.%s){3})|(172\\.(1[6-9]|2\\d|3[01])(\\.%s){2})|(192\\.168(\\.%s){2})|(127\\.0\\.0\\.%s)",
                ipDigitPattern, ipDigitPattern, ipDigitPattern, ipDigitPattern));

    public static boolean isInternalIp(String str) {
        return internalIpPattern.matcher(str).matches();
    }
}
