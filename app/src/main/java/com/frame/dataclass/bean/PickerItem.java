package com.frame.dataclass.bean;

import java.io.Serializable;

/**
 * Picker 传入初始值结构体
 */
public class PickerItem implements Serializable {
    private static final long serialVersionUID = 1L;

    public NameValue nv1 = null;
    public NameValue nv2 = null;
    public NameValue nv3 = null;

    public PickerItem(NameValue nv1) {
        this.nv1 = nv1 == null ? new NameValue("", "") : nv1; // 第一项不能空
    }

    public PickerItem(NameValue nv1, NameValue nv2) {
        this.nv1 = nv1 == null ? new NameValue("", "") : nv1; // 第一项不能空
        this.nv2 = nv2;
    }

    /**
     * 传null不显示
     */
    public PickerItem(NameValue nv1, NameValue nv2, NameValue nv3) {
        this.nv1 = nv1 == null ? new NameValue("", "") : nv1; // 第一项不能空
        this.nv2 = nv2;
        this.nv3 = nv3;
    }
}
