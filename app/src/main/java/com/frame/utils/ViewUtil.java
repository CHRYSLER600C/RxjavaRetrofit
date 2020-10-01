package com.frame.utils;

import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by min on 2016/12/9.
 */

public class ViewUtil {

    public enum TextType {
        TYPE_TEXT, TYPE_HTML
    }

    /**
     * 设置TextView文本
     *
     * @param v    TextView, EditText, Button
     * @param text 设置的内容
     * @param hide 在text为空时是否隐藏
     * @param type TextType
     */
    public static void setViewText(View v, CharSequence text, boolean hide, TextType type) {
        if (v == null || text == null) {
            return;
        } else {
            if (TextUtils.isEmpty(text) && hide) {
                v.setVisibility(View.GONE);
                return;
            }
        }
        v.setVisibility(View.VISIBLE);

        if (type == TextType.TYPE_HTML) {
            text = Html.fromHtml((String) text);
        }
        if (v instanceof TextView) {
            ((TextView) v).setText(text);
        }
    }

    /**
     * 设置TextView文本, 不隐藏
     *
     * @param tv   TextView, EditText, Button
     * @param text 设置的内容
     */
    public static void setViewText(TextView tv, CharSequence text) {
        setViewText(tv, text, false, TextType.TYPE_TEXT); // 不隐藏
    }

    /**
     * 设置TextView文本, 不隐藏
     *
     * @param v    TextView, EditText, Button
     * @param text 设置的内容
     */
    public static void setViewHtml(View v, String text) {
        setViewText(v, text, false, TextType.TYPE_HTML); // 不隐藏
    }

    public static String getEtTrim(EditText et) {
        return et == null ? "" : et.getText().toString().trim();
    }

    /**
     * 长度不可大于15位，支持2位小数
     *
     * @param et
     */
    public static void addEditTextWatcher(EditText et) {
        et.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {
            }

            @Override
            public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {
            }

            @Override
            public void afterTextChanged(Editable paramEditable) {
                String temp = paramEditable.toString();
                int posDot = temp.indexOf(".");// 返回指定字符在此字符串中第一次出现处的索引
                if (posDot <= 0) {// 不包含小数点
                    if (temp.length() <= 15) {
                        return;// 小于五位数直接返回
                    } else {
                        paramEditable.delete(15, 16);// 大于五位数就删掉第六位（只会保留五位）
                        return;
                    }
                } else if (temp.length() - posDot - 1 > 2) {// 如果包含小数点
                    paramEditable.delete(posDot + 3, posDot + 4);// 删除小数点后的第三位
                }
            }
        });
    }

    /**
     * WebViewUtils: 调用JS方法
     */
    public static void callJavaScriptFunction(final WebView webView, final String script) {
        if (webView == null) {
            return;
        }
        webView.post(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webView.evaluateJavascript(script, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                        }
                    });
                } else {
                    //  String script = displayAlert('Hello World' , true);
                    webView.loadUrl("javascript:" + script);
                }
            }
        });
    }

    /**
     * WebViewUtils: 组装调用JS的代码
     */
    @NonNull
    public static String formatScript(@NonNull final String function, @Nullable final Object... params) {
        final StringBuilder builder = new StringBuilder(function).append('(');
        final int length = params.length;
        for (int i = 0; i < params.length; ++i) {
            if (params[i] instanceof String) {
                builder.append("\'" + params[i] + "\'");
            }
            if (i != length - 1) {
                builder.append(",");
            }
        }
        builder.append(')');
        return builder.toString();
    }
}
