package com.frame.httputils;

import android.content.Context;
import android.widget.ImageView;

import com.frame.common.CommonData;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * 下载图片工具（不包含Cookie）
 */
public class ImageLoaderUtil {

    private static Picasso picasso;

    public static Picasso createPicasso(Context context) {
        if (picasso == null) {
            synchronized (ImageLoaderUtil.class) {
                if (picasso == null) {
                    Downloader downloader = new OkHttp3Downloader(new File(CommonData.BASE_DIR_SD + "/picasso"));
                    picasso = new Picasso.Builder(context).downloader(downloader).build();
                }
            }
        }
        return picasso;
    }

    public static void downloadImage(Context context, String url, ImageView image) {
        createPicasso(context).load(url).into(image);
    }

    public static void downloadImage(Context context, String url, ImageView image, int defaultDrawable) {
        createPicasso(context).load(url).placeholder(defaultDrawable).error(defaultDrawable).into(image);
    }

    // 显示本地图片
    public static void downloadImage(Context context, File file, ImageView image) {
        createPicasso(context).load(file).into(image);
    }

}
