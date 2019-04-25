package com.frame.utils;

import android.content.Context;
import android.widget.ImageView;

import com.frame.R;
import com.frame.httputils.ImageLoaderUtil;
import com.youth.banner.loader.ImageLoader;

/**
 * @author dongxie
 * @date 2019/2/1
 */

public class BannerImageLoader extends ImageLoader {

    @Override
    public void displayImage(Context context, Object o, ImageView iv) {
        ImageLoaderUtil.downloadImage(context, (String)o, iv, R.drawable.ic_default);
    }
}