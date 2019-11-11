package com.example.openglview;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.openglview.render.TextureRenderer;
import com.example.openglview.render.TriangleRenderer;
import com.example.openglview.utils.ShaderUtils;
import com.example.openglview.utils.TextureUtils;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView mGLSurfaceView;

    public static String triangleVertex = "";
    public static String triangleFragment = "";
    public static String textureVertex = "";
    public static String textureFragment = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLSurfaceView = new GLSurfaceView(this);
        mGLSurfaceView.setEGLContextClientVersion(2);
        // 加载顶点和片源着色器
        triangleVertex = ShaderUtils.loadFromAssetsFile("triangleVertex.sh", getResources());
        triangleFragment = ShaderUtils.loadFromAssetsFile("triangleFragment.sh", getResources());
        textureVertex = ShaderUtils.loadFromAssetsFile("textureVertex.sh", getResources());
        textureFragment = ShaderUtils.loadFromAssetsFile("textureFragment.sh", getResources());

        // 工具类
        TextureUtils.init(this);

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
