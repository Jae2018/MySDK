package com.jae.downsdk.tools;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 三级缓存之网络缓存
 */
public class NetCacheUtils {

    private static BitmapTask task;

    /**
     * 从网络下载图片
     *
     * @param ivPic 显示图片的imageview
     * @param url   下载图片的网络地址
     */
    public static void getBitmapFromNet(ImageView ivPic, String url) {
        if (task == null) {
            task = new BitmapTask();
        }
        task.execute(ivPic, url);//启动AsyncTask
    }

    /**
     * AsyncTask就是对handler和线程池的封装
     * 第一个泛型:参数类型
     * 第二个泛型:更新进度的泛型
     * 第三个泛型:onPostExecute的返回结果
     */
    static class BitmapTask extends AsyncTask<Object, Void, Bitmap> {

        private Object ivPic;
        private String url;

        /**
         * 后台耗时操作,存在于子线程中
         */
        @Override
        protected Bitmap doInBackground(Object[] params) {
            ivPic = params[0];
            url = (String) params[1];

            return downLoadBitmap(url);
        }

        /**
         * 更新进度,在主线程中
         */
        @Override
        protected void onProgressUpdate(Void[] values) {
            super.onProgressUpdate(values);
        }

        /**
         * 耗时方法结束后执行该方法,主线程中
         */
        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                ((ImageView) ivPic).setImageBitmap(result);
                System.out.println("从网络缓存图片啦.....");

                //从网络获取图片后,保存至本地缓存
                LocalCacheUtils.setBitmapToLocal(url, result);
                //保存至内存中
                MemoryCacheUtils.setBitmapToMemory(url, result);

            }
        }
    }

    /**
     * 网络下载图片
     */
    static Bitmap downLoadBitmap(String url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                //图片压缩
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;//宽高压缩为原来的1/2
                options.inPreferredConfig = Bitmap.Config.ARGB_4444;
                return BitmapFactory.decodeStream(conn.getInputStream(), null, options);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null)
                conn.disconnect();
        }

        return null;
    }
}
