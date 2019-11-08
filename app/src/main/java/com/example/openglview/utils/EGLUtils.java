package com.example.openglview.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class EGLUtils {

    public static final int BYTES_PER_FLOAT = 4;

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
}
