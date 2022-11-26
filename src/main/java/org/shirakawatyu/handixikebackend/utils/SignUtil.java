package org.shirakawatyu.handixikebackend.utils;

import cn.hutool.crypto.digest.DigestUtil;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class SignUtil {
    public static String getSign(String seed) {
        byte[] bytes = seed.getBytes(StandardCharsets.UTF_8);
        long move = System.currentTimeMillis() / 60000;
        long[] longs = new long[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            longs[i] = bytes[i] + move;
        }
        return DigestUtil.md5Hex(Arrays.toString(longs));
    }
}
