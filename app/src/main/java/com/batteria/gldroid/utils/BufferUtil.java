package com.batteria.gldroid.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * @author: yaobeihaoyu
 * @version: 1.0
 * @since: 2021/7/29
 * @description:
 */
public class BufferUtil {
    public static FloatBuffer floatToBuffer(float[] input) {
        FloatBuffer output = ByteBuffer.allocateDirect(input.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(input);
        output.position(0);
        return output;
    }

    public static IntBuffer intToBuffer(int[] input) {
        IntBuffer output = ByteBuffer.allocateDirect(input.length * 4)
                .order(ByteOrder.nativeOrder()).asIntBuffer().put(input);
        output.position(0);
        return output;
    }
}