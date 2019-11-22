package com.example.openglview.render;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.example.openglview.MainActivity;
import com.example.openglview.R;
import com.example.openglview.utils.EGLUtils;
import com.example.openglview.utils.ShaderUtils;
import com.example.openglview.utils.TextureUtils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TextureCubeRenderer implements GLSurfaceView.Renderer {

    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMvpMatrix = new float[16];

    //图片尺寸2048x2048的jpg才显示得出来
    private int[] resIds = new int[]{
            R.drawable.texture, R.drawable.texture,
            R.drawable.texture, R.drawable.texture,
            R.drawable.texture, R.drawable.texture,
    };
    private int resIdTexture;//纹理ID

    //顶点坐标VBO
    private float[] vertex = new float[]{
            -1, 1, 1,     // (0) Top-left near
            1, 1, 1,     // (1) Top-right near
            -1, -1, 1,     // (2) Bottom-left near
            1, -1, 1,     // (3) Bottom-right near
            -1, 1, -1,     // (4) Top-left far
            1, 1, -1,     // (5) Top-right far
            -1, -1, -1,     // (6) Bottom-left far
            1, -1, -1      // (7) Bottom-right far
    };
    //顶点索引IBO
    final byte[] index = new byte[]{
            // Front
            1, 3, 0,
            0, 3, 2,

            // Back
            4, 6, 5,
            5, 6, 7,

            // Left
            0, 2, 4,
            4, 2, 6,

            // Right
            5, 7, 1,
            1, 7, 3,

            // Top
            5, 1, 4,
            4, 1, 0,

            // Bottom
            6, 2, 7,
            7, 2, 3
    };

    private ByteBuffer indexBuffer;
    private FloatBuffer vertexBuffer;

    {
        indexBuffer = EGLUtils.getByteBuffer(index);
        vertexBuffer = EGLUtils.getFloatBuffer(vertex);

        Matrix.setIdentityM(mViewMatrix, 0);
        Matrix.setIdentityM(mProjectMatrix, 0);
        Matrix.setIdentityM(mMvpMatrix, 0);
    }

    int program;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置背景清理颜色为白色
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        //创建程序
        program = ShaderUtils.createProgram(MainActivity.textureCubeVertex, MainActivity.textureCubeFragment);
        //创建纹理
        resIdTexture = TextureUtils.loadTextures(resIds);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //设置视口
        GLES20.glViewport(0, 0, width, height);

        //计算宽高比
        float ratio = (float) width / height;
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 20);
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 5.0f, 5.0f, 10.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMvpMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //擦除屏幕
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_TEST);
        //使用程序
        GLES20.glUseProgram(program);

        //传入模型、视图、投影矩阵
        int matrixLoc = GLES20.glGetUniformLocation(program, "u_Matrix");
        GLES20.glUniformMatrix4fv(matrixLoc, 1, false, mMvpMatrix, 0);

        //绑定纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, resIdTexture);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(program, "u_TextureUnit"), 0);

        //顶点坐标
        int vertexLoc = GLES20.glGetAttribLocation(program, "a_Position");
        GLES20.glEnableVertexAttribArray(vertexLoc);
        GLES20.glVertexAttribPointer(vertexLoc, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        //绘制
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 36, GLES20.GL_UNSIGNED_BYTE, indexBuffer);

        //移除程序
        GLES20.glUseProgram(0);
    }
}
