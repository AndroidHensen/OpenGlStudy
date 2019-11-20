package com.example.openglview;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.openglview.render.Texture3DRenderer;
import com.example.openglview.utils.ShaderUtils;
import com.example.openglview.utils.TextureUtils;
import com.example.openglview.view.ClickableGLSurfaceView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private ClickableGLSurfaceView mGLSurfaceView;
    private Texture3DRenderer mRenderer;

    public static String triangleVertex = "";
    public static String triangleFragment = "";
    public static String textureVertex = "";
    public static String textureFragment = "";
    public static String texture3DVertex = "";
    public static String texture3DFragment = "";

    private SensorManager mSensorManager;
    private Sensor mRotationSensor;
    private float[] mRotationMatrix = new float[16];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLSurfaceView = new ClickableGLSurfaceView(this);
        mGLSurfaceView.setEGLContextClientVersion(2);
        // 加载顶点和片源着色器
        triangleVertex = ShaderUtils.loadFromAssetsFile("triangleVertex.sh", getResources());
        triangleFragment = ShaderUtils.loadFromAssetsFile("triangleFragment.sh", getResources());
        textureVertex = ShaderUtils.loadFromAssetsFile("textureVertex.sh", getResources());
        textureFragment = ShaderUtils.loadFromAssetsFile("textureFragment.sh", getResources());
        texture3DVertex = ShaderUtils.loadFromAssetsFile("texture3DVertex.sh", getResources());
        texture3DFragment = ShaderUtils.loadFromAssetsFile("texture3DFragment.sh", getResources());

        // 工具类
        TextureUtils.init(this);

        // 设置渲染器
        mRenderer = new Texture3DRenderer();
        mGLSurfaceView.setRenderer(mRenderer);
        setContentView(mGLSurfaceView);

        // 陀螺仪
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        Matrix.setIdentityM(mRotationMatrix, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
        // 陀螺仪
        mSensorManager.registerListener(this, mRotationSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
        // 陀螺仪
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        SensorManager.getRotationMatrixFromVector(mRotationMatrix, event.values);
        if (mGLSurfaceView != null) {
            mGLSurfaceView.queueEvent(new Runnable() {
                @Override
                public void run() {
                    mRenderer.setRotationMatrix(mRotationMatrix);
                }
            });
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
