package com.example.openglview.render;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.example.openglview.MainActivity;
import com.example.openglview.R;
import com.example.openglview.utils.EGLUtils;
import com.example.openglview.utils.MatrixHelper;
import com.example.openglview.utils.ShaderUtils;
import com.example.openglview.utils.TextureUtils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TextureSkyBoxRenderer implements GLSurfaceView.Renderer {

    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMvpMatrix = new float[16];

    private float[] mRotationMatrix = new float[16];//旋转矩阵

    private int[] resIds = new int[]{
            R.drawable.right, R.drawable.left,
            R.drawable.top, R.drawable.bottom,
            R.drawable.front, R.drawable.back,
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

        Matrix.setIdentityM(mRotationMatrix, 0);
        Matrix.setIdentityM(mViewMatrix, 0);
        Matrix.setIdentityM(mProjectMatrix, 0);
        Matrix.setIdentityM(mMvpMatrix, 0);
    }

    int program;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //清理颜色
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        //创建程序
        program = ShaderUtils.createProgram(MainActivity.texture3DVertex, MainActivity.texture3DFragment);
        //创建纹理
        resIdTexture = TextureUtils.loadTextures(resIds);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //设置视口
        GLES20.glViewport(0, 0, width, height);
        //计算宽高比
        float ratio = (float) width / (float) height;
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0f, 1.0f, 0.0f);
        //设置透视矩阵
        MatrixHelper.perspectiveM(mProjectMatrix, 45, ratio, 1f, 300f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //擦除屏幕
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        //使用程序
        GLES20.glUseProgram(program);

        //变换相机位置
        calculateMatrix();

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

    /**
     * 变换
     */
    private void calculateMatrix() {
        Matrix.setIdentityM(mViewMatrix, 0);
        Matrix.setLookAtM(mViewMatrix, 0, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mViewMatrix, 0, mViewMatrix, 0, mRotationMatrix, 0);
        Matrix.rotateM(mViewMatrix, 0, 90, 1f, 0f, 0f);
        Matrix.multiplyMM(mMvpMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
    }

    /**
     * 旋转
     */
    public void setRotationMatrix(float[] matrix) {
        mRotationMatrix = matrix;
    }
}
