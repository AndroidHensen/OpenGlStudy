package com.example.openglview.render;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import com.example.openglview.MainActivity;
import com.example.openglview.R;
import com.example.openglview.utils.EGLUtils;
import com.example.openglview.utils.ShaderUtils;
import com.example.openglview.utils.TextureUtils;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Texture3DRenderer implements GLSurfaceView.Renderer {

    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] modelViewProjectionMatrix = new float[16];

    private float[] mRotationMatrix = new float[16];

    private int[] resIds = {
            R.drawable.texture,
            R.drawable.texture,
            R.drawable.texture,
            R.drawable.texture,
            R.drawable.texture,
            R.drawable.texture,
    };

    //顶点坐标VBO
    private float[] vertex = {
            -1.0f, 1.0f, 1.0f,    //正面左上0
            -1.0f, -1.0f, 1.0f,   //正面左下1
            1.0f, -1.0f, 1.0f,    //正面右下2
            1.0f, 1.0f, 1.0f,     //正面右上3
            -1.0f, 1.0f, -1.0f,    //反面左上4
            -1.0f, -1.0f, -1.0f,   //反面左下5
            1.0f, -1.0f, -1.0f,    //反面右下6
            1.0f, 1.0f, -1.0f,     //反面右上7
    };
    //顶点索引IBO
    final short[] index = {
            // Front
            0,1,2,
            0,2,3,
            // Back
            5,4,7,
            5,7,6,
            // Left
            4,0,3,
            4,3,7,
            // Right
            1,5,6,
            1,6,2,
            // Top
            3,2,6,
            3,6,7,
            // Bottom
            4,5,1,
            4,1,0,
    };
    //纹理坐标
    public float[] textureCoord = {
            0.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, 0.0f, 1.0f
    };

    private ShortBuffer indexBuffer;
    private FloatBuffer vertexBuffer;
    private FloatBuffer textureCoordBuffer;

    {
        indexBuffer = EGLUtils.getShortBuffer(index);
        vertexBuffer = EGLUtils.getFloatBuffer(vertex);
        textureCoordBuffer = EGLUtils.getFloatBuffer(textureCoord);
    }

    int program;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置背景清理颜色为白色
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);

        //创建程序
        program = ShaderUtils.createProgram(MainActivity.texture3DVertex, MainActivity.texture3DFragment);
        //使用程序
        GLES20.glUseProgram(program);
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
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //擦除屏幕
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_TEXTURE_CUBE_MAP);

        //旋转
        long time = SystemClock.uptimeMillis() % 1000L;
        float angle = 0.00360f * ((int) time);
        Matrix.setRotateM(mRotationMatrix, 0, angle, 0, 0, -1.0f);
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, modelViewProjectionMatrix, 0, mRotationMatrix, 0);

        //传入模型、视图、投影矩阵
        int matrixLoc = GLES20.glGetUniformLocation(program, "matrix");
        GLES20.glUniformMatrix4fv(matrixLoc, 1, false, modelViewProjectionMatrix, 0);

        //顶点坐标和纹理坐标
        int vertexLoc = GLES20.glGetAttribLocation(program, "a_Position");
        int textureLoc = GLES20.glGetAttribLocation(program, "a_texCoord");
        GLES20.glVertexAttribPointer(vertexLoc, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glVertexAttribPointer(textureLoc, 3, GLES20.GL_FLOAT, false, 0, textureCoordBuffer);
        GLES20.glEnableVertexAttribArray(vertexLoc);
        GLES20.glEnableVertexAttribArray(textureLoc);

        //绑定纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, TextureUtils.loadTextures(resIds));
        GLES20.glUniform1i(GLES20.glGetUniformLocation(program, "s_texture"), 0);

        //绘制
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, index.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

        //销毁
        GLES20.glDisableVertexAttribArray(vertexLoc);
        GLES20.glDisableVertexAttribArray(textureLoc);
    }
}
