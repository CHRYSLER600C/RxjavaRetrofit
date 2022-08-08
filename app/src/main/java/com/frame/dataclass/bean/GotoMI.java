package com.frame.dataclass.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

/**
 * 跳转型数据模板 Goto Multi Item
 */
public class GotoMI implements Serializable, MultiItemEntity {

    private static final long serialVersionUID = 1L;

    private static final int BASE = 1;
    public static final int TITLE = BASE;
    public static final int TIME_PICKER = BASE << 1;        //TOP
    public static final int MULTI_PICKER = BASE << 2;     //CENTER
    public static final int IMAGE = BASE << 3;     //BOTTOM
    public static final int SPACE = BASE << 4;     //SPACE

    public int resId;
    public String content;
    public Object data;     //数据存储
    public int itemType;
    public Object extend;      //扩展备用
    public Class<?> cls;

    public GotoMI(int resId, String content, Object data, int itemType, Object extend, Class<?> cls) {
        this.resId = resId;
        this.content = content;
        this.data = data;
        this.itemType = itemType;
        this.extend = extend;
        this.cls = cls;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
