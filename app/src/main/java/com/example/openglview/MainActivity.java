package com.example.openglview;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.openglview.render.TriangleRenderer;
import com.example.openglview.utils.ShaderUtils;

public class MainActivity extends AppCompatActivity {
    private GLSurfaceView mGLSurfaceView;
    public static String vertex = "";
    public static String fragment = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLSurfaceView = new GLSurfaceView(this);

        // 加载顶点和片源着色器
        vertex = ShaderUtils.loadFromAssetsFile("vertex.sh", getResources());
        fragment = ShaderUtils.loadFromAssetsFile("fragment.sh", getResources());

        // 请求一个OpenGL ES 2.0兼容的上下文
        mGLSurfaceView.setEGLContextClientVersion(3);
        // 设置渲染器
        mGLSurfaceView.setRenderer(new TriangleRenderer());

        setContentView(mGLSurfaceView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }
}
