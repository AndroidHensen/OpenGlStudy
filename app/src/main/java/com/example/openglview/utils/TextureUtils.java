package com.example.openglview.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;

import static android.opengl.GLUtils.texImage2D;

public class TextureUtils {

    private static Context context;

    public static void init(Context ctx) {
        context = ctx.getApplicationContext();
    }

    public static int loadTexture(int resId) {
        //创建纹理对象
        int[] textureObjIds = new int[1];
        //生成纹理：纹理数量、保存纹理的数组，数组偏移量
        GLES20.glGenTextures(1, textureObjIds, 0);
        if (textureObjIds[0] == 0) {
            throw new RuntimeException("创建纹理对象失败");
        }
        //原尺寸加载位图资源（禁止缩放）
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);
        if (bitmap == null) {
            //删除纹理对象
            GLES20.glDeleteTextures(1, textureObjIds, 0);
            throw new RuntimeException("加载位图失败");
        }
        //绑定纹理到opengl
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureObjIds[0]);
        //设置放大、缩小时的纹理过滤方式，必须设定，否则纹理全黑
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        //将位图加载到opengl中，并复制到当前绑定的纹理对象上
        texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        //创建 mip 贴图
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        //释放bitmap资源（上面已经把bitmap的数据复制到纹理上了）
        bitmap.recycle();
        //解绑当前纹理，防止其他地方以外改变该纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        //返回纹理对象
        return textureObjIds[0];
    }
}
