package com.frame.dataclass.bean;

import java.io.Serializable;

/**
 * 可跳转的模板
 */
public class Template implements Serializable {
    private static final long serialVersionUID = 1L;

    public int resId;
    public String content;
    public String url;//模块外跳转链接
    public Class cls;


    public Template(int resId, String content, String url, Class<?> cls) {
        this.resId = resId;
        this.content = content;
        this.url = url;
        this.cls = cls;
    }

}
