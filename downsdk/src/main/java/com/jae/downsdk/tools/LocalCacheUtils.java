package com.jae.downsdk.tools;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * 三级缓存之本地缓存
 */
public class LocalCacheUtils {

    private static final String CACHE_PATH = Environment.getDataDirectory().getAbsolutePath()
            + File.separator + "sad";

    /**
     * 从本地读取图片
     */
    public static Bitmap getBitmapFromLocal(String url) {
        String fileName;//把图片的url当做文件名,并进行MD5加密
        try {
            fileName = MD5Encoder.encode(url);
            File file = new File(CACHE_PATH, fileName);

            return BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从网络获取图片后,保存至本地缓存
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void setBitmapToLocal(String url, Bitmap bitmap) {
        try {
            String fileName = MD5Encoder.encode(url);//把图片的url当做文件名,并进行MD5加密
            File file = new File(CACHE_PATH, fileName);

            //通过得到文件的父文件,判断父文件是否存在
            File parentFile = file.getParentFile();
            assert parentFile != null;
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }

            //把图片保存至本地
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
