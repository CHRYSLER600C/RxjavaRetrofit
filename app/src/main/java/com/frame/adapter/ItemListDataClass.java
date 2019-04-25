package com.frame.adapter;

import android.view.View;

import com.frame.dataclass.DataClass;
import com.frame.dataclass.bean.PickerItem;
import com.frame.dataclass.bean.PickerValue;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ItemListDataClass extends DataClass implements Serializable {
    private static final long serialVersionUID = 8826025509861200672L;

    /**
     * =============================== Types ===============================
     */
    public static final int TYPE_TEXT = 1;               // <分割线，可设置高度，颜色，文字，背景等>
    public static final int TYPE_TEXT_EDIT_TEXT = 2;     // <商品单价       5 元>
    public static final int TYPE_TEXT_SELECT = 3;        // <公司地址    请选择 请选择 请选择> 可用于地址选择
    public static final int TYPE_TEXT_SELECT_TIME = 4;   // <入学时间       请选择> 弹出时间选择框
    public static final int TYPE_TEXT_TEXT = 5;          // <图标 授信管理    未授信>
    public static final int TYPE_BUTTON = 6;             // <确定按钮>
    public static final int TYPE_TEXT_EDIT_BUTTON = 7;   // <手机号 139863524555 获取验证码>
    public static final int TYPE_CUSTOM = 8;             // 自定义View

    /**
     * =============================== Variable ===============================
     */
    private Map<Integer, ItemInfo> mMap = new HashMap<>();


    /**
     * =============================== Methods ===============================
     */

    public int getSize() {
        return mMap.size();
    }

    public Map<Integer, ItemInfo> getMap() {
        return mMap;
    }

    public void setMap(Map<Integer, ItemInfo> map) {
        mMap.clear();
        mMap = map;
    }

    public ItemInfo getItemInfo(int key) {
        return mMap.get(key);
    }

    public void addItemInfo(int key, ItemInfo info) {
        if (info != null) {
            mMap.put(key, info);
        }
    }

    public void removeItemInfo(int key) {
        mMap.remove(key);
    }

    /**
     * 添加一个TextView，可以作为分割线，也可设置高度，颜色，文字，背景等
     *
     * @param key
     * @param name   标题
     * @param value  高度-字体大小 eg:(100-20)可只传第一个
     * @param value2 字体颜色值-背景颜色值 eg:(#333333-#cccccc)可只传第一个
     * @param value3 标题位置（Gravity.LEFT, Gravity.CENTER, Gravity.RIGHT等, 不设置传-1）
     */
    public void addText(int key, String name, String value, Object value2, Object value3) {
        ItemInfo itemInfo = new ItemInfo();
        itemInfo.type = TYPE_TEXT;
        itemInfo.name = name;
        itemInfo.value = value;
        itemInfo.value2 = value2;
        itemInfo.value3 = value3;
        mMap.put(key, itemInfo);
    }

    /**
     * 添加一个TextView + EditText输入框 + TextView单位， eg：<商品单价       5 元>
     *
     * @param key
     * @param name   标题，传空隐藏
     * @param value  EditText的值
     * @param value2 单位，传空隐藏
     * @param value3 EditText的hint值, 传"USE_NAME"将使用默认的名字
     * @param isEdit EditText是否可编辑(1可编辑，0不可编辑，其它值自行解析)
     * @param isMust EditText是否为必填项(1必填，0非必填，其它值自行解析)
     */
    public void addTextEditText(int key, String name, String value, Object value2, Object value3, int isEdit, int
            isMust) {
        ItemInfo itemInfo = new ItemInfo();
        itemInfo.type = TYPE_TEXT_EDIT_TEXT;
        itemInfo.name = name;
        itemInfo.value = value;
        itemInfo.value2 = value2;
        itemInfo.value3 = value3;
        itemInfo.isEdit = isEdit;
        itemInfo.isMust = isMust;
        mMap.put(key, itemInfo);
    }

    /**
     * 添加一个TextView + 选择项
     *
     * @param key
     * @param name        标题，传空隐藏
     * @param value2      已选择的初始值，3个则显示3个选项
     * @param value       单位
     * @param pickerValue 供选择的值
     * @param isEdit      EditText是否可编辑(1可编辑，0不可编辑，其它值自行解析)
     * @param isMust      EditText是否为必填项(1必填，0非必填，其它值自行解析)
     */
    public void addTextSelect(int key, String name, PickerItem value2, String value, PickerValue pickerValue, int
            isEdit, int isMust) {
        ItemInfo itemInfo = new ItemInfo();
        itemInfo.type = TYPE_TEXT_SELECT;
        itemInfo.name = name;
        itemInfo.value2 = value2;
        itemInfo.value = value;
        itemInfo.value3 = pickerValue;
        itemInfo.isEdit = isEdit;
        itemInfo.isMust = isMust;
        mMap.put(key, itemInfo);
    }

    /**
     * 添加一个TextView + 时间选择项
     *
     * @param key
     * @param name   标题，传空隐藏
     * @param value  EditText的值
     * @param isEdit EditText是否可编辑(1可编辑，0不可编辑，其它值自行解析)
     * @param isMust EditText是否为必填项(1必填，0非必填，其它值自行解析)
     */
    public void addTextSelectTime(int key, String name, String value, int isEdit, int isMust) {
        ItemInfo itemInfo = new ItemInfo();
        itemInfo.type = TYPE_TEXT_SELECT_TIME;
        itemInfo.name = name;
        itemInfo.value = value;
        itemInfo.isEdit = isEdit;
        itemInfo.isMust = isMust;
        mMap.put(key, itemInfo);
    }

    /**
     * 添加一个 Icon + TextView1 +　TextView2 + 右箭头
     *
     * @param key
     * @param name     TextView1的值
     * @param value    TextView2的值
     * @param iconId   TextView1的图标
     * @param isEdit   是否可点击
     * @param callback 点击后回调
     */
    public void addTextText(int key, String name, String value, int iconId, int isEdit, IItemEventCallBack callback) {
        ItemInfo itemInfo = new ItemInfo();
        itemInfo.type = TYPE_TEXT_TEXT;
        itemInfo.name = name;
        itemInfo.value = value;
        itemInfo.value2 = iconId;
        itemInfo.isEdit = isEdit;
        itemInfo.callback = callback;
        mMap.put(key, itemInfo);
    }

    /**
     * 添加一个居中的 Button 按钮
     *
     * @param key
     * @param marginTop
     * @param marginBottom
     * @param name         button内容
     * @param resBg        背景样式，传-1表示使用默认的
     * @param isEdit       是否可点击
     * @param callback     点击后回调
     */
    public void addButton(int key, String name, String marginTop, String marginBottom, int resBg, int isEdit,
                          IItemEventCallBack callback) {
        ItemInfo itemInfo = new ItemInfo();
        itemInfo.type = TYPE_BUTTON;
        itemInfo.name = name;
        itemInfo.value = marginTop;
        itemInfo.value2 = marginBottom;
        itemInfo.value3 = resBg;
        itemInfo.isEdit = isEdit;
        itemInfo.callback = callback;
        mMap.put(key, itemInfo);
    }

    /**
     * 添加一个 TextView + EditView + Button 的样式 eg.<手机号 139863524555 获取验证码>
     *
     * @param key
     * @param name     标题，传空隐藏
     * @param value    EditText的值
     * @param value2   EditText的hint值, 传"USE_NAME"将使用默认的名字
     * @param isEdit   EditText+Button 是否可编辑(11都可编辑，0都不可编辑，其它值自行解析)
     * @param isMust   EditText是否为必填项(1必填，0非必填，其它值自行解析)
     * @param callback Button点击后回调，传空隐藏
     */
    public void addTextEditButton(int key, String name, String value, Object value2, int isEdit, int
            isMust, IItemEventCallBack callback) {
        ItemInfo itemInfo = new ItemInfo();
        itemInfo.type = TYPE_TEXT_EDIT_BUTTON;
        itemInfo.name = name;
        itemInfo.value = value;
        itemInfo.value2 = value2;
        itemInfo.isEdit = isEdit;
        itemInfo.isMust = isMust;
        itemInfo.value3 = callback;
        mMap.put(key, itemInfo);
    }

    /**
     * 添加自定义View
     *
     * @param key
     * @param layoutId 布局文件id
     * @param callback 回调函数
     */
    public void addCustomItem(int key, int layoutId, IItemCallBack callback) {
        ItemInfo itemInfo = new ItemInfo();
        itemInfo.type = TYPE_CUSTOM;
        itemInfo.value = "" + layoutId;
        itemInfo.callback = callback;
        mMap.put(key, itemInfo);
    }

    /**
     * Item 数据结构 =============================================================================
     */
    public class ItemInfo implements Serializable {
        private static final long serialVersionUID = 8826025509861200672L;

        public int type;                       // 类型
        public String name;                    // 名称
        public String value;                   // 输入或选择结果
        public int visible = View.VISIBLE;     // 默认可见
        public Object value2;                  // 备用：自行解析对应值
        public Object value3;                  // 备用：自行解析对应值
        public Object callback;                // 回调接口
        public int isEdit = 1;                 // 是否可编辑(1可编辑，0不可编辑，其它值自行解析)
        public int isMust = 0;                 // 是否为必填项(1必填，0非必填，其它值自行解析)
    }

    /**
     * Item里面的任何事件 回调函数 =============================================================================
     */
    public interface IItemEventCallBack {
        /**
         * @param value1 备用
         * @param value2 备用
         */
        void handleEvent(Object value1, Object value2);
    }

    /**
     * 自定义布局回调接口
     */
    public interface IItemCallBack {
        /**
         * @param convertView Inflater的View
         * @param holder      viewholder
         */
        void handleItem(View convertView, CommonViewHolder holder);
    }
}
