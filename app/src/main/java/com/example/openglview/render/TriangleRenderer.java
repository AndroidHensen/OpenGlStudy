package com.example.openglview.render;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.example.openglview.MainActivity;
import com.example.openglview.utils.EGLUtils;
import com.example.openglview.utils.ShaderUtils;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.Matrix.orthoM;

public class TriangleRenderer implements GLSurfaceView.Renderer {

    public final float[] projectionMatrix = new float[16];
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
    /**
     * 顶点颜色数据
     */
    private final FloatBuffer verticeColorsBuffer;
    /**
     * 顶点颜色
     */
    private static final float[] verticeColors = {
            0.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f
    };

    {

        mVertexBuffer = EGLUtils.getFloatBuffer(vertices);
        verticeColorsBuffer = EGLUtils.getFloatBuffer(verticeColors);
    }

    int program;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置背景清理颜色为白色
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);

        //创建程序
        program = ShaderUtils.createProgram(MainActivity.triangleVertex, MainActivity.triangleFragment);
        //使用程序
        GLES20.glUseProgram(program);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //设置视口
        GLES20.glViewport(0, 0, width, height);
        //计算正交投影矩阵，修正变形
        float aspectRatio = width > height ?
                (float) width / (float) height : (float) height / (float) width;
        if (width > height) {
            orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
            orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //擦除屏幕
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        //传入正交矩阵修复变形
        int matrixLoc = GLES20.glGetUniformLocation(program, "matrix");
        GLES20.glUniformMatrix4fv(matrixLoc, 1, false, projectionMatrix, 0);

        //获取 vPosition 属性的位置
        int vposition = GLES20.glGetAttribLocation(program, "vPosition");
        //加载顶点数据到 vPosition 属性位置
        GLES20.glVertexAttribPointer(vposition, 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(vposition);

        //获取 vColor 属性位置
        int aColor = GLES20.glGetAttribLocation(program, "aColor");
        //加载顶点颜色数据到 vColor 属性位置
        GLES20.glVertexAttribPointer(aColor, 4, GLES20.GL_FLOAT, false, 0, verticeColorsBuffer);
        GLES20.glEnableVertexAttribArray(aColor);

        //绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }
}
