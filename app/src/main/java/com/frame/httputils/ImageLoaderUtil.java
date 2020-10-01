package com.frame.httputils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.blankj.utilcode.util.ImageUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.frame.R;
import com.frame.httputils.other.GlideEveryRoundTransform;
import com.frame.httputils.other.GlideRoundTransform;
import com.frame.other.ICallBack;
import com.frame.utils.CU;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 下载图片工具（不包含Cookie）
 */
public class ImageLoaderUtil {

    private static RequestOptions mRequestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL);


    /**
     * 加载图片
     *
     * @param resDef 0表示无占位图片
     * @param resErr 0表示无错误图片
     */
    public static void loadImage(Context context, String url, ImageView iv, int resDef, int resErr) {
        RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL);
        if (resDef > 0) options.placeholder(resDef);
        if (resErr > 0) options.error(resErr);
        Glide.with(context).load(url).apply(options).into(iv);
    }

    /**
     * 加载本地文件
     */
    public static void loadImageFile(Context context, File file, ImageView iv) {
        Glide.with(context).load(file).into(iv);
    }

    /**
     * 加载本地资源文件
     */
    public static void loadImageRes(Context context, ImageView iv, int resId) {
        Glide.with(context).load(resId).apply(mRequestOptions).into(iv);
    }

    /**
     * 加载图片，成功后回调
     */
    public static void loadImageCallBack(Context context, String url, ICallBack icb) {
        Glide.with(context).load(url).apply(mRequestOptions).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                if (icb != null) icb.dataCallback(resource);
            }
        });
    }

    /**
     * 加载圆角图片, 可设置每个角（加载后再做的处理）
     */
    public static void loadEveryCorner(Context context, String url, ImageView iv, float[] corners) {
        Bitmap bmpDefault = ImageUtils.getBitmap(R.drawable.ic_default);
        iv.setImageBitmap(CU.drawCorner(bmpDefault, corners));

        Glide.with(context).load(url).apply(mRequestOptions).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource,
                                        @Nullable Transition<? super Drawable> transition) {
                Bitmap bitmap = ImageUtils.drawable2Bitmap(resource);
                iv.setImageBitmap(CU.drawCorner(bitmap, corners));
            }
        });
    }

    /**
     * 先对图片CenterCrop，再对每个角做圆角处理
     *
     * @param corners 左上、右上、右下、左下四个点：new float[] {20, 20, 0, 0, 20, 20 ,0, 0 }
     * @param resDef  0表示无占位图片
     * @param resErr  0表示无错误图片
     */
    public static void loadCropEveryCorner(Context context, String url, ImageView iv, float[] corners,
                                           int resDef, int resErr) {
        RequestOptions options = RequestOptions.bitmapTransform(new GlideEveryRoundTransform(corners))
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        if (resDef > 0) options.placeholder(resDef);
        if (resErr > 0) options.error(resErr);
        Glide.with(context).load(url).apply(options).into(iv);
    }

    /**
     * 先对图片CenterCrop，再圆角处理（4个角一样）
     *
     * @param cornerSize 圆角大小，单位pixels，必须大于0
     * @param resDef     小于0表示无占位图片
     * @param resErr     小于0表示无错误图片
     */
    public static void loadCropRoundCorner(Context context, String url, ImageView imageView, int cornerSize,
                                                 int resDef, int resErr) {
        RequestOptions requestOptions = RequestOptions.bitmapTransform(
                new GlideRoundTransform(cornerSize < 1 ? 1 : cornerSize)).diskCacheStrategy(DiskCacheStrategy.ALL);
        if (resDef > 0) requestOptions.placeholder(resDef);
        if (resErr > 0) requestOptions.error(resErr);
        Glide.with(context).load(url).apply(requestOptions).into(imageView);
    }

    /**
     * 加载圆角图片（4个角一样）
     *
     * @param cornerSize 圆角大小，单位pixels，必须大于0
     * @param resDef     0表示无占位图片
     * @param resErr     0表示无错误图片
     */
    public static void loadRoundCorner(Context context, String url, ImageView iv, int cornerSize, int resDef,
                                       int resErr) {
        RequestOptions options = RequestOptions.bitmapTransform(new RoundedCorners(cornerSize < 1 ? 1 : cornerSize))
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        if (resDef > 0) options.placeholder(resDef);
        if (resErr > 0) options.error(resErr);
        Glide.with(context).load(url).apply(options).into(iv);
    }

    /**
     * 加载圆形图片（圆圆的）
     *
     * @param resDef 0表示无占位图片
     * @param resErr 0表示无错误图片
     */
    public static void loadCircleImage(Context context, String url, ImageView iv, int resDef, int resErr) {
        RequestOptions options = new RequestOptions().circleCrop().diskCacheStrategy(DiskCacheStrategy.ALL);
        if (resDef > 0) options.placeholder(resDef);
        if (resErr > 0) options.error(resErr);
        Glide.with(context).load(url).apply(options).into(iv);
    }


}
