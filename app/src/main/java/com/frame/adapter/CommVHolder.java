package com.frame.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.method.MovementMethod;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Universal view holder.
 * Created by Cheney on 19/2/22.
 */
public class CommVHolder {

    private SparseArray<View> childViews = new SparseArray<>();
    public final View itemView;

    CommVHolder(View itemView) {
        this.itemView = itemView;
    }

    public static CommVHolder get(View convertView, View itemView) {
        CommVHolder holder;
        if (convertView == null) {
            holder = new CommVHolder(itemView);
            convertView = itemView;
            convertView.setTag(holder);
        } else {
            holder = (CommVHolder) convertView.getTag();
        }
        return holder;
    }

    /**
     * @param id  View id
     * @param <T> Subclass of View
     * @return Child view
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T findViewById(int id) {
        View childView = childViews.get(id);
        if (childView == null) {
            childView = itemView.findViewById(id);
            if (childView != null)
                childViews.put(id, childView);
            else
                return null;
        }
        return (T) childView;
    }

    public CommVHolder setText(int viewId, CharSequence text) {
        TextView textView = findViewById(viewId);
        textView.setText(text);
        return this;
    }

    public CommVHolder setTextColor(int viewId, int textColor) {
        TextView view = findViewById(viewId);
        view.setTextColor(textColor);
        return this;
    }

    public CommVHolder setTextColor(int viewId, ColorStateList colorStateList) {
        TextView view = findViewById(viewId);
        view.setTextColor(colorStateList);
        return this;
    }

    public CommVHolder setMovementMethod(int viewId, MovementMethod method) {
        TextView textView = findViewById(viewId);
        textView.setMovementMethod(method);
        return this;
    }

    public CommVHolder setImageResource(int viewId, @DrawableRes int resId) {
        ImageView view = findViewById(viewId);
        view.setImageResource(resId);
        return this;
    }

    public CommVHolder setImageDrawable(int viewId, Drawable drawable) {
        ImageView view = findViewById(viewId);
        view.setImageDrawable(drawable);
        return this;
    }

    public CommVHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView view = findViewById(viewId);
        view.setImageBitmap(bitmap);
        return this;
    }

    public CommVHolder setImageUri(int viewId, Uri imageUri) {
        ImageView view = findViewById(viewId);
        view.setImageURI(imageUri);
        return this;
    }

    public CommVHolder setScaleType(int viewId, ImageView.ScaleType type) {
        ImageView view = findViewById(viewId);
        view.setScaleType(type);
        return this;
    }

    public CommVHolder setBackgroundColor(int viewId, @ColorInt int bgColor) {
        View view = findViewById(viewId);
        view.setBackgroundColor(bgColor);
        return this;
    }

    public CommVHolder setBackgroundResource(int viewId, @DrawableRes int bgRes) {
        View view = findViewById(viewId);
        view.setBackgroundResource(bgRes);
        return this;
    }

    public CommVHolder setColorFilter(int viewId, ColorFilter colorFilter) {
        ImageView view = findViewById(viewId);
        view.setColorFilter(colorFilter);
        return this;
    }

    public CommVHolder setColorFilter(int viewId, int colorFilter) {
        ImageView view = findViewById(viewId);
        view.setColorFilter(colorFilter);
        return this;
    }

    public CommVHolder setAlpha(int viewId, @FloatRange(from = 0.0, to = 1.0) float value) {
        View view = findViewById(viewId);
        ViewCompat.setAlpha(view, value);
        return this;
    }

    public CommVHolder setVisibility(int viewId, int visibility) {
        View view = findViewById(viewId);
        view.setVisibility(visibility);
        return this;
    }

    public CommVHolder setMax(int viewId, int max) {
        ProgressBar view = findViewById(viewId);
        view.setMax(max);
        return this;
    }

    public CommVHolder setProgress(int viewId, int progress) {
        ProgressBar view = findViewById(viewId);
        view.setProgress(progress);
        return this;
    }

    public CommVHolder setRating(int viewId, float rating) {
        RatingBar view = findViewById(viewId);
        view.setRating(rating);
        return this;
    }

    public CommVHolder setTag(int viewId, Object tag) {
        View view = findViewById(viewId);
        view.setTag(tag);
        return this;
    }

    public CommVHolder setEnabled(int viewId, boolean enabled) {
        View view = findViewById(viewId);
        view.setEnabled(enabled);
        return this;
    }

    public CommVHolder setAdapter(int viewId, Adapter adapter) {
        AdapterView<Adapter> view = findViewById(viewId);
        view.setAdapter(adapter);
        return this;
    }

    public CommVHolder setAdapter(int viewId, RecyclerView.Adapter adapter) {
        RecyclerView view = findViewById(viewId);
        view.setAdapter(adapter);
        return this;
    }

    public CommVHolder setChecked(int viewId, boolean checked) {
        Checkable view = findViewById(viewId);
        view.setChecked(checked);
        return this;
    }

    public CommVHolder setOnClickListener(int viewId, View.OnClickListener listener) {
        findViewById(viewId).setOnClickListener(listener);
        return this;
    }

    public CommVHolder setOnLongClickListener(int viewId, View.OnLongClickListener listener) {
        findViewById(viewId).setOnLongClickListener(listener);
        return this;
    }

    public CommVHolder setOnTouchListener(int viewId, View.OnTouchListener listener) {
        findViewById(viewId).setOnTouchListener(listener);
        return this;
    }
    /**================================================= Original ===============================================*/










    /**================================================= Add_By_Self ===============================================*/


    public CommVHolder setLayoutParams(int viewId, ViewGroup.LayoutParams params) {
        View view = findViewById(viewId);
        view.setLayoutParams(params);
        return this;
    }

    public CommVHolder setGravity(int viewId, int gravity) {
        TextView textView = findViewById(viewId);
        textView.setGravity(gravity);
        return this;
    }

    public CommVHolder setTextSize(int viewId, float size) {
        TextView textView = findViewById(viewId);
        textView.setTextSize(size);
        return this;
    }

    public CommVHolder setHint(int viewId, CharSequence hint) {
        TextView textView = findViewById(viewId);
        textView.setHint(hint);
        return this;
    }


    /**
     * 动态设置TextView(EditText, Button)的drawable图标, 可设置图标大小
     *
     * @param viewId
     * @param resId
     * @param dpW       单位dp, -1 表示使用原始宽度
     * @param dpH       单位dp, -1 表示使用原始高度
     * @param dpPadding 图标文字间隔
     */
    public CommVHolder setTVDrawableLeft(int viewId, int resId, int dpW, int dpH, int dpPadding) {
        TextView tv = findViewById(viewId);
        Drawable[] ds = tv.getCompoundDrawables();
        tv.setCompoundDrawables(res2drawable(tv.getContext(), resId, dpW, dpH), ds[1], ds[2], ds[3]);
        tv.setCompoundDrawablePadding(ConvertUtils.dp2px(dpPadding));
        return this;
    }

    public CommVHolder setTVDrawableTop(int viewId, int resId, int dpW, int dpH, int dpPadding) {
        TextView tv = findViewById(viewId);
        Drawable[] ds = tv.getCompoundDrawables();
        tv.setCompoundDrawables(ds[0], res2drawable(tv.getContext(), resId, dpW, dpH), ds[2], ds[3]);
        tv.setCompoundDrawablePadding(ConvertUtils.dp2px(dpPadding));
        return this;
    }

    public CommVHolder setTVDrawableRight(int viewId, int resId, int dpW, int dpH, int dpPadding) {
        TextView tv = findViewById(viewId);
        Drawable[] ds = tv.getCompoundDrawables();
        tv.setCompoundDrawables(ds[0], ds[1], res2drawable(tv.getContext(), resId, dpW, dpH), ds[3]);
        tv.setCompoundDrawablePadding(ConvertUtils.dp2px(dpPadding));
        return this;
    }

    public CommVHolder setTVDrawableBottom(int viewId, int resId, int dpW, int dpH, int dpPadding) {
        TextView tv = findViewById(viewId);
        Drawable[] ds = tv.getCompoundDrawables();
        tv.setCompoundDrawables(ds[0], ds[1], ds[2], res2drawable(tv.getContext(), resId, dpW, dpH));
        tv.setCompoundDrawablePadding(ConvertUtils.dp2px(dpPadding));
        return this;
    }

    /**
     * @param dpW 单位dp, -1 表示使用原始宽度
     * @param dpH 单位dp, -1 表示使用原始高度
     */
    private Drawable res2drawable(Context context, int resId, int dpW, int dpH) {
        if (resId < 0) {
            return null;
        }
        Drawable drawable = context.getResources().getDrawable(resId);
        int width = dpW < 0 ? drawable.getMinimumWidth() : ConvertUtils.dp2px(dpW);
        int height = dpH < 0 ? drawable.getMinimumHeight() : ConvertUtils.dp2px(dpH);
        drawable.setBounds(0, 0, width, height); // 这一步必须要做,否则不会显示.
        return drawable;
    }


    /**========================== Return Object =============================*/

    public String getText(int viewId) {
        TextView textView = findViewById(viewId);
        return textView.getText().toString().trim();
    }

}
