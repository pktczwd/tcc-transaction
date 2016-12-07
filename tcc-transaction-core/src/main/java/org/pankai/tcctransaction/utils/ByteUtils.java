package org.pankai.tcctransaction.utils;

import java.nio.ByteBuffer;

/**
 * Created by pktczwd on 2016/12/7.
 */
public class ByteUtils {

    public static byte[] longToBytes(long l) {
        return ByteBuffer.allocate(8).putLong(l).array();
    }

    public static long bytesToLong(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getLong();
    }
}
