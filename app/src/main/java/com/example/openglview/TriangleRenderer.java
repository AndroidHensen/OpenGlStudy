package com.example.openglview;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TriangleRenderer implements GLSurfaceView.Renderer {

    /**
     * 每个Float多少字节
     */
    private final int mBytesPerFloat = 4;
    /**
     * 顶点坐标数据
     */
    private final FloatBuffer mVertexBuffer;
    /**
     * 顶点坐标
     */
    private static final float[] vertices = {
            0.0f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f
    };

    {
        mVertexBuffer = ByteBuffer.allocateDirect(vertices.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mVertexBuffer
                .put(vertices)
                .position(0);
    }

    int program;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // 设置背景清理颜色为白色
        GLES30.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);

        program = ShaderUtils.createProgram(MainActivity.vertex, MainActivity.fragment);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //擦除屏幕
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        //使用程序
        GLES30.glUseProgram(program);
        //获取 vPosition 属性的位置
        int vposition = GLES30.glGetAttribLocation(program, "vPosition");
        //加载顶点数据到 vPosition 属性位置
        GLES30.glVertexAttribPointer(vposition, 3, GLES30.GL_FLOAT, false, 0, mVertexBuffer);
        GLES30.glEnableVertexAttribArray(vposition);
        //绘制
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3);
    }
}
