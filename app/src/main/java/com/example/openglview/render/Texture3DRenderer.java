package com.example.openglview.render;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.example.openglview.MainActivity;
import com.example.openglview.R;
import com.example.openglview.utils.EGLUtils;
import com.example.openglview.utils.ShaderUtils;
import com.example.openglview.utils.TextureUtils;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Texture3DRenderer implements GLSurfaceView.Renderer {

    public final float[] modelViewProjectionMatrix = new float[16];

    //顶点坐标
    private float[] vertex = {
            -1f, 1f, 0.0f,
            -1f, -1f, 0.0f,
            1f, -1f, 0.0f,
            1f, 1f, 0.0f
    };
    //纹理坐标
    public float[] textureCoord = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f
    };

    private FloatBuffer vertexBuffer;
    private FloatBuffer textureCoordBuffer;

    {
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

        Matrix.perspectiveM(modelViewProjectionMatrix, 0, 45, (float) width / height, 0.1f, 100f);
        Matrix.translateM(modelViewProjectionMatrix, 0, 0f, 0f, -2.5f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //擦除屏幕
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        //传入模型、视图、投影矩阵
        int matrixLoc = GLES20.glGetUniformLocation(program, "matrix");
        GLES20.glUniformMatrix4fv(matrixLoc, 1, false, modelViewProjectionMatrix, 0);

        //顶点坐标和纹理坐标
        int vertexLoc = GLES20.glGetAttribLocation(program, "a_Position");
        int textureLoc = GLES20.glGetAttribLocation(program, "a_texCoord");
        GLES20.glVertexAttribPointer(vertexLoc, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glVertexAttribPointer(textureLoc, 2, GLES20.GL_FLOAT, false, 0, textureCoordBuffer);
        GLES20.glEnableVertexAttribArray(vertexLoc);
        GLES20.glEnableVertexAttribArray(textureLoc);

        //绑定纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, TextureUtils.loadTexture(R.drawable.texture));
        GLES20.glUniform1i(GLES20.glGetUniformLocation(program, "s_texture"), 0);

        //绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertex.length / 3);

        //销毁
        GLES20.glDisableVertexAttribArray(vertexLoc);
        GLES20.glDisableVertexAttribArray(textureLoc);
    }
}
