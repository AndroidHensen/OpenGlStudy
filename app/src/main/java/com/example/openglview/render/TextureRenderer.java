package com.example.openglview.render;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.example.openglview.MainActivity;
import com.example.openglview.R;
import com.example.openglview.utils.EGLUtils;
import com.example.openglview.utils.ShaderUtils;
import com.example.openglview.utils.TextureUtils;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.Matrix.orthoM;

public class TextureRenderer implements GLSurfaceView.Renderer {

    public final float[] projectionMatrix = new float[16];

    //顶点，按逆时针顺序排列
    private float[] vertex = {
            -1f, 1f, 0.0f,
            -1f, -1f, 0.0f,
            1f, -1f, 0.0f,
            1f, 1f, 0.0f
    };
    //纹理坐标，（s,t），t坐标方向和顶点y坐标反着
    public float[] textureCoord = {
            0.0f, 0.0f,
            0.0f, 0.5f,
            0.5f, 0.5f,
            0.5f, 0.0f
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
        program = ShaderUtils.createProgram(MainActivity.textureVertex, MainActivity.textureFragment);
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

        int vertexLoc = GLES20.glGetAttribLocation(program, "a_Position");
        int textureLoc = GLES20.glGetAttribLocation(program, "a_texCoord");

        GLES20.glVertexAttribPointer(vertexLoc, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glVertexAttribPointer(textureLoc, 2, GLES20.GL_FLOAT, false, 0, textureCoordBuffer);

        GLES20.glEnableVertexAttribArray(vertexLoc);
        GLES20.glEnableVertexAttribArray(textureLoc);

        //绑定纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, TextureUtils.loadTexture(R.drawable.texture));
        //Set the sampler texture unit to 0
        GLES20.glUniform1i(GLES20.glGetUniformLocation(program, "s_texture"), 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertex.length / 3);

        GLES20.glDisableVertexAttribArray(vertexLoc);
        GLES20.glDisableVertexAttribArray(textureLoc);
    }
}
