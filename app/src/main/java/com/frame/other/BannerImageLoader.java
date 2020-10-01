package com.frame.other;

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
        ImageLoaderUtil.loadImage(context, (String)o, iv, R.drawable.ic_default, 0);
    }
}