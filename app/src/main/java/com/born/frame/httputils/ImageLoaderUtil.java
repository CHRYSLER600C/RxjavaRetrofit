package com.born.frame.httputils;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

import okhttp3.OkHttpClient;

/**
 * 下载图片工具（不包含Cookie）
 */
public class ImageLoaderUtil {

	private static Picasso picasso;

	public static Picasso createPicasso(Context context) {
		if (picasso == null) {
			synchronized (ImageLoaderUtil.class) {
				if (picasso == null) {
					OkHttpClient picassoClient = new OkHttpClient.Builder().build();
					picasso = new Picasso.Builder(context).downloader(new OkHttp3Downloader(picassoClient)).build();
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
