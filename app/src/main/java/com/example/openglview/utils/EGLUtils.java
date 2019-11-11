package com.example.openglview.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class EGLUtils {

    public static final int BYTES_PER_FLOAT = 4;
    public static final int BYTES_PER_SHORT = 2;

    public static FloatBuffer getFloatBuffer(float[] array) {
        FloatBuffer buffer = ByteBuffer
                .allocateDirect(array.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        buffer
                .put(array)
                .position(0);
        return buffer;
    }

    public static ShortBuffer getShortBuffer(short[] array) {
        ShortBuffer buffer = ByteBuffer
                .allocateDirect(array.length * BYTES_PER_SHORT)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        buffer
                .put(array)
                .position(0);
        return buffer;
    }
}
