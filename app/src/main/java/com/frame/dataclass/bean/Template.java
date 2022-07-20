package com.frame.dataclass.bean;

import android.app.Activity;

import java.io.Serializable;

/**
 * 可跳转的模板
 */
public class Template implements Serializable {
    private static final long serialVersionUID = 1L;

    public int resId;
    public String content;
    public String url;//模块外跳转链接
    public Class<? extends Activity> cls;


    public Template(int resId, String content, String url, Class<? extends Activity> cls) {
        this.resId = resId;
        this.content = content;
        this.url = url;
        this.cls = cls;
    }

}
