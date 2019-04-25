package com.frame.dataclass.bean;

/**
 * Created by dongxie on 2017/3/23.
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Picker 的数据结构
 */
public class PickerValue implements Serializable {
    private static final long serialVersionUID = 1L;

    public boolean isRelation = false;                 // 每项之间是否关联，比如省市区是关联的
    public ArrayList<NameValue> list1;
    public HashMap<String, ArrayList<NameValue>> map2; // 如果和list1无关联，key值随意
    public HashMap<String, ArrayList<NameValue>> map3; // 如果和map2无关联，key值随意

    public PickerValue() {
    }

    public PickerValue(ArrayList<NameValue> list1) {
        this.list1 = list1;
    }

    public PickerValue(boolean isRelation, ArrayList<NameValue> list1, HashMap<String, ArrayList<NameValue>> map2,
                       HashMap<String, ArrayList<NameValue>> map3) {
        this.isRelation = isRelation;
        this.list1 = list1;
        this.map2 = map2;
        this.map3 = map3;
    }
}
