package com.born.frame.adapter;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.born.frame.R;
import com.born.frame.activity.BaseActivity;
import com.born.frame.adapter.ItemListDataClass.IItemCallBack;
import com.born.frame.adapter.ItemListDataClass.IItemEventCallBack;
import com.born.frame.adapter.ItemListDataClass.ItemInfo;
import com.born.frame.adapter.ViewHolderClass.ButtonViewHolder;
import com.born.frame.adapter.ViewHolderClass.TextTextViewHolder;
import com.born.frame.adapter.ViewHolderClass.LineViewHolder;
import com.born.frame.adapter.ViewHolderClass.SelectTimeViewHolder;
import com.born.frame.adapter.ViewHolderClass.SelectViewHolder;
import com.born.frame.adapter.ViewHolderClass.TextEditButtonViewHolder;
import com.born.frame.adapter.ViewHolderClass.TextEditTextViewHolder;
import com.born.frame.dataclass.bean.NameValue;
import com.born.frame.dataclass.bean.PickerItem;
import com.born.frame.dataclass.bean.PickerValue;
import com.born.frame.utils.DisplayUtil;
import com.born.frame.utils.ImageUtil;
import com.born.frame.utils.JudgeUtil;
import com.born.frame.utils.ViewUtil;
import com.born.frame.view.dialog.PickerDialog;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ItemListAdapter {

    private Context mContext;
    private LinearLayout mLlParent;         // 父布局
    private ItemListDataClass mILdc;        // 布局数据
    private LayoutInflater mInflater;
    private Object[] mKeys;

    /**
     * ========================================== public function ==========================================
     *
     * @param mLlParent 父布局
     * @param ildc      布局数据
     */
    public ItemListAdapter(LinearLayout mLlParent, ItemListDataClass ildc) {
        this.mContext = mLlParent.getContext();
        this.mLlParent = mLlParent;
        this.mILdc = ildc;
        this.mInflater = LayoutInflater.from(mContext);
    }

    private void refreshKeys() {
        mILdc.setMap(sortMapByKey(mILdc.getMap()));  // key值排序
        mKeys = mILdc.getMap().keySet().toArray();
    }

    /**
     * 刷新所有View，刷新少量数据慎用
     */
    public void refreshAllItemView() {
        mLlParent.removeAllViews();
        refreshKeys();
        Observable.from(mKeys)
                .compose(((BaseActivity) mContext).bindToLifecycle())  // Rxlifecycle自动取消订阅，防止内存泄露
                .map(new Func1<Object, View>() {
                    @Override
                    public View call(Object o) {
                        return getView((int) o, null);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<View>() {
                    @Override
                    public void call(View view) {
                        mLlParent.addView(view);
                    }
                });
    }

    /**
     * 刷新单个item
     */
    public void refreshItemView(int key) {
        for (int i = 0; i < mKeys.length; i++) {
            if (key == (int) mKeys[i]) {
                View convertView = mLlParent.getChildAt(i);
                getView(key, convertView);
                break;
            }
        }
    }

    /**
     * 添加item布局到LinearLayout（请先添加数据)
     */
    public void addItemView(int key) {
        refreshKeys();
        for (int i = 0; i < mKeys.length; i++) {
            if (key == (int) mKeys[i] && mLlParent.getChildCount() < mKeys.length) {
                mLlParent.addView(getView(key, null), i);
                break;
            }
        }
    }

    /**
     * 从LinearLayout移除某个布局及数据
     */
    public void removeItemView(int key) {
        for (int i = 0; i < mKeys.length; i++) {
            if (key == (int) mKeys[i]) {
                mLlParent.removeViewAt(i);
                mILdc.removeItemInfo(key);
                break;
            }
        }
        refreshKeys();
    }

    /**
     * 设置item VISIBLE
     */
    public void setItemViewVisible(int key, int visible) {
        for (int i = 0; i < mKeys.length; i++) {
            if (key == (int) mKeys[i]) {
                mILdc.getItemInfo(key).visible = visible;
                View convertView = mLlParent.getChildAt(i);
                convertView.setVisibility(visible);
                break;
            }
        }
    }

    /**
     * 获取指定类的 Key List
     *
     * @param obj     需要获取的类实例
     * @param filters 根据此数组过滤
     */
    public static ArrayList<Integer> getKeyList(Object obj, String[] filters) {
        ArrayList<Integer> keyList = new ArrayList<>();
        Class clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String name = field.getName();
            for (String filter : filters) {
                if (name.startsWith(filter)) {
                    try {
                        keyList.add(field.getInt(name));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        Collections.sort(keyList, new Comparator<Integer>() {
            @Override
            public int compare(Integer integer1, Integer integer2) {
                return integer1 - integer2;
            }
        });
        return keyList;
    }

    public static String getItemValue(ItemListDataClass iLdc, int key) {
        ItemListDataClass.ItemInfo itemInfo = iLdc.getItemInfo(key);
        return itemInfo != null ? itemInfo.value : "";
    }

    public static PickerItem getPickerItem(ItemListDataClass iLdc, int key) {
        ItemListDataClass.ItemInfo itemInfo = iLdc.getItemInfo(key);
        return itemInfo != null ? (PickerItem) itemInfo.value2 : new PickerItem(new NameValue("", ""), new NameValue
                ("", ""), new NameValue("", ""));
    }

    /**
     * ========================================== private function ==========================================
     * 绘制每一个ItemView
     */
    private View getView(final int key, View convertView) {
        ViewHolderView vhv = null;
        final ItemInfo itemInfo = mILdc.getItemInfo(key);

        switch (itemInfo.type) {
            case ItemListDataClass.TYPE_TEXT:
                vhv = getViewHolder(R.layout.common_item_line, LineViewHolder.class, itemInfo, convertView);
                LineViewHolder vhLine = (LineViewHolder) vhv.viewHolder;

                if (JudgeUtil.isNotEmpty(itemInfo.name)) {
                    ViewUtil.setViewText(vhLine.tvItemLine, itemInfo.name);
                }
                if (JudgeUtil.isNotEmpty(itemInfo.value)) { // 高度-字体大小 eg:(100-20)可只传第一个
                    String[] values = itemInfo.value.split("-");
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            Integer.parseInt(values[0]));
                    vhLine.tvItemLine.setLayoutParams(lp);
                    if (values.length > 1) {
                        vhLine.tvItemLine.setTextSize(Integer.parseInt(values[1]));
                    }
                }
                if (JudgeUtil.isNotEmpty(itemInfo.value2)) { // 字体颜色值-背景颜色值 eg:(#333333-#cccccc)可只传第一个
                    String[] values2 = ((String) itemInfo.value2).split("-");
                    vhLine.tvItemLine.setTextColor(Color.parseColor(values2[0]));
                    if (values2.length > 1) {
                        vhLine.tvItemLine.setBackgroundColor(Color.parseColor(values2[1]));
                    }
                }
                if ((Integer) itemInfo.value3 > 0) {
                    vhLine.tvItemLine.setGravity((Integer) itemInfo.value3);
                }
                break;

            case ItemListDataClass.TYPE_TEXT_EDIT_TEXT:
                vhv = getViewHolder(R.layout.common_item_text_edit_text, TextEditTextViewHolder.class, itemInfo,
                        convertView);
                TextEditTextViewHolder vhTet = (TextEditTextViewHolder) vhv.viewHolder;
                setItemBackground(itemInfo, vhv.convertView);// 设置背景

                ViewUtil.setViewText(vhTet.tvItemNameTet, itemInfo.name);
                ViewUtil.setViewText(vhTet.etItemContentTet, itemInfo.value);
                vhTet.etItemContentTet.setEnabled(itemInfo.isEdit == 1);
                if (itemInfo.isEdit == 1 && JudgeUtil.isNotEmpty(itemInfo.value3)) {
                    if ("USE_NAME".equals(itemInfo.value3)) {
                        vhTet.etItemContentTet.setHint("请输入" + itemInfo.name); // 设置Hint值
                    } else {
                        vhTet.etItemContentTet.setHint((String) itemInfo.value3); // 设置Hint值
                    }
                }
                if (JudgeUtil.isNotEmpty(itemInfo.value2)) {
                    ViewUtil.setViewText(vhTet.tvItemUnitTet, (String) itemInfo.value2); // 单位
                } else {
                    vhTet.tvItemUnitTet.setVisibility(View.GONE);
                }
                addEditTextChangedListener(key, vhTet.etItemContentTet, itemInfo);
                break;

            case ItemListDataClass.TYPE_TEXT_SELECT:
                vhv = getViewHolder(R.layout.common_item_select, SelectViewHolder.class, itemInfo, convertView);
                final SelectViewHolder vhSelect = (SelectViewHolder) vhv.viewHolder;
                setItemBackground(itemInfo, vhv.convertView);// 设置背景

                ViewUtil.setViewText(vhSelect.tvSelectName, itemInfo.name);
                ViewUtil.setViewText(vhSelect.tvSelectUnit, itemInfo.value);
                PickerItem pickerItem = (PickerItem) itemInfo.value2;
                if (pickerItem != null) {
                    ViewUtil.setViewText(vhSelect.tvSelectContent1, pickerItem.nv1.name);
                    if (pickerItem.nv2 == null) {
                        vhSelect.tvSelectContent2.setVisibility(View.GONE);
                    } else {
                        ViewUtil.setViewText(vhSelect.tvSelectContent2, pickerItem.nv2.name);
                    }
                    if (pickerItem.nv3 == null) {
                        vhSelect.tvSelectContent3.setVisibility(View.GONE);
                    } else {
                        ViewUtil.setViewText(vhSelect.tvSelectContent3, pickerItem.nv3.name);
                    }
                }
                vhSelect.llSelectContent.setEnabled(itemInfo.isEdit == 1);

                final PickerValue pickerValue = (PickerValue) itemInfo.value3;
                final PickerDialog.Builder pickerBuilder = new PickerDialog.Builder(mContext);
                pickerBuilder.setBtnOk(pickerValue, "bottom", new PickerDialog.IPickerDialogOkCallBack() {
                    @Override
                    public void handleBtnOk(NameValue nv1, NameValue nv2, NameValue nv3) {
                        itemInfo.value2 = new PickerItem(nv1, nv2, nv3);
                        vhSelect.tvSelectContent1.setText(nv1.name);
                        vhSelect.tvSelectContent2.setText(nv2.name);
                        vhSelect.tvSelectContent3.setText(nv3.name);
                        if (null != itemInfo.callback) { // 选择后回调处理数据
                            IItemEventCallBack callback = (IItemEventCallBack) itemInfo.callback;
                            callback.handleEvent(itemInfo.value2, "");
                        }
                    }
                }).setPickedData((PickerItem) itemInfo.value2);
                if (itemInfo.isEdit == 1) {
                    vhSelect.llSelectContent.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            pickerBuilder.setPickedData((PickerItem) itemInfo.value2);
                            if (pickerValue != null && pickerValue.list1.size() == 0) {
                                Toast.makeText(mContext, "暂无数据", Toast.LENGTH_SHORT).show();
                            } else {
                                DisplayUtil.showAnimatDialog(pickerBuilder.create());
                            }
                        }
                    });
                } else {
                    vhSelect.tvSelectContent1.setTextColor(Color.GRAY);
                    vhSelect.tvSelectContent2.setTextColor(Color.GRAY);
                    vhSelect.tvSelectContent3.setTextColor(Color.GRAY);
                }
                break;

            case ItemListDataClass.TYPE_TEXT_SELECT_TIME:
                vhv = getViewHolder(R.layout.common_item_select_time, SelectTimeViewHolder.class, itemInfo,
                        convertView);
                final SelectTimeViewHolder vhSelTime = (SelectTimeViewHolder) vhv.viewHolder;
                setItemBackground(itemInfo, vhv.convertView);// 设置背景

                ViewUtil.setViewText(vhSelTime.tvSelectTimeName, itemInfo.name);
                ViewUtil.setViewText(vhSelTime.tvSelectTimeContent, itemInfo.value);
                vhSelTime.tvSelectTimeContent.setEnabled(itemInfo.isEdit == 1);
                vhSelTime.tvSelectTimeContent.setHint("请选择");

                if (itemInfo.isEdit == 1) {
                    vhSelTime.tvSelectTimeContent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String str[] = itemInfo.value.split("-");

                            int yearInt, monthInt, dayInt;
                            if (str.length == 3) {
                                yearInt = Integer.parseInt(str[0]);
                                monthInt = Integer.parseInt(str[1]) - 1;
                                dayInt = Integer.parseInt(str[2]);
                            } else {
                                Calendar c = Calendar.getInstance();
                                yearInt = c.get(Calendar.YEAR);
                                monthInt = c.get(Calendar.MONTH);
                                dayInt = c.get(Calendar.DATE);
                            }

                            final DatePickerDialog datePickerDialog = new DatePickerDialog(mContext,
                                    new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePicker view, int year, int month, int day) {
                                            month++;
                                            String monthStr = month > 9 ? "" : "0" + month;
                                            String dayStr = day > 9 ? "" : "0" + day;

                                            itemInfo.value = year + "-" + monthStr + "-" + dayStr;
                                            ViewUtil.setViewText(vhSelTime.tvSelectTimeContent, itemInfo.value);
                                        }
                                    }, yearInt, monthInt, dayInt);

                            Window window = datePickerDialog.getWindow();
                            window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
                            window.setWindowAnimations(R.style.AnimationBottomInOut); // 添加动画
                            datePickerDialog.show();
                        }
                    });
                } else {
                    vhSelTime.tvSelectTimeContent.setTextColor(Color.GRAY);
                }
                break;

            case ItemListDataClass.TYPE_TEXT_TEXT:
                vhv = getViewHolder(R.layout.common_item_text_text, TextTextViewHolder.class, itemInfo, convertView);
                final TextTextViewHolder vhTt = (TextTextViewHolder) vhv.viewHolder;
                setItemBackground(itemInfo, vhv.convertView);// 设置背景

                ImageUtil.setTextViewDrawableLeft(vhTt.tvItemNameTt, (int) itemInfo.value2, 25, 25, 10);
                ViewUtil.setViewText(vhTt.tvItemNameTt, itemInfo.name);
                ViewUtil.setViewText(vhTt.tvItemContentTt, itemInfo.value);
                if (itemInfo.isEdit == 1) { // 点击事件回调
                    vhv.convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (null != itemInfo.callback) {
                                IItemEventCallBack callback = (IItemEventCallBack) itemInfo.callback;
                                callback.handleEvent(itemInfo.name, itemInfo.value);
                            }
                        }
                    });
                }
                break;

            case ItemListDataClass.TYPE_BUTTON:
                vhv = getViewHolder(R.layout.common_item_button, ButtonViewHolder.class, itemInfo, convertView);
                final ButtonViewHolder vhBtn = (ButtonViewHolder) vhv.viewHolder;

                ViewUtil.setViewText(vhBtn.btnCommon, itemInfo.name);
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) vhBtn.btnCommon.getLayoutParams();
                lp.setMargins(lp.leftMargin, Integer.parseInt(itemInfo.value), lp.rightMargin, Integer.parseInt(
                        (String) itemInfo.value2));
                if ((int) itemInfo.value3 > 0) {
                    vhBtn.btnCommon.setBackgroundResource((int) itemInfo.value3);
                }
                if (itemInfo.isEdit == 1) { // 点击事件回调
                    vhBtn.btnCommon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (null != itemInfo.callback) {
                                IItemEventCallBack callback = (IItemEventCallBack) itemInfo.callback;
                                callback.handleEvent(itemInfo.name, "");
                            }
                        }
                    });
                } else {
                    vhBtn.btnCommon.setBackgroundResource(R.drawable.shape_btn_common);
                    vhBtn.btnCommon.setTextColor(Color.parseColor("#999999"));
                }
                break;

            case ItemListDataClass.TYPE_TEXT_EDIT_BUTTON:
                vhv = getViewHolder(R.layout.common_item_text_edit_btn, TextEditButtonViewHolder.class, itemInfo,
                        convertView);
                final TextEditButtonViewHolder vhTeb = (TextEditButtonViewHolder) vhv.viewHolder;
                setItemBackground(itemInfo, vhv.convertView);// 设置背景

                ViewUtil.setViewText(vhTeb.tvItemNameTeb, itemInfo.name);
                vhTeb.tvItemNameTeb.setVisibility(JudgeUtil.isEmpty(itemInfo.name) ? View.GONE : View.VISIBLE);
                ViewUtil.setViewText(vhTeb.etItemContentTeb, itemInfo.value);
                vhTeb.etItemContentTeb.setEnabled(itemInfo.isEdit / 10 == 1);
                if (itemInfo.isEdit / 10 == 1 && JudgeUtil.isNotEmpty(itemInfo.value2)) {
                    if ("USE_NAME".equals(itemInfo.value2)) {
                        vhTeb.etItemContentTeb.setHint("请输入" + itemInfo.name); // 设置Hint值
                    } else {
                        vhTeb.etItemContentTeb.setHint((String) itemInfo.value2); // 设置Hint值
                    }
                }
                addEditTextChangedListener(key, vhTeb.etItemContentTeb, itemInfo);
                if (itemInfo.isEdit % 10 == 1) { // 点击事件回调
                    if (null != itemInfo.value3) {
                        vhTeb.btnItemTeb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                IItemEventCallBack callback = (IItemEventCallBack) itemInfo.value3;
                                callback.handleEvent(view, "");
                            }
                        });
                    } else {
                        vhTeb.btnItemTeb.setVisibility(View.GONE);
                    }
                }
                break;

            case ItemListDataClass.TYPE_CUSTOM:
                vhv = getViewHolder(Integer.parseInt(itemInfo.value), (Class) itemInfo.value2, itemInfo, convertView);
                setItemBackground(itemInfo, vhv.convertView);// 设置背景

                if (itemInfo.callback != null) {
                    IItemCallBack callback = (IItemCallBack) itemInfo.callback;
                    callback.handleItem(vhv.convertView, vhv.viewHolder);
                }
                break;
        }
        return vhv != null ? vhv.convertView : convertView;
    }

    // 初始化数据
    private ViewHolderView getViewHolder(int resId, Class cls, ItemInfo itemInfo, View convertView) {
        Object viewHolder = null;
        if (null == convertView) {
            convertView = mInflater.inflate(resId, null);
            if (cls != null) {
                try {
                    viewHolder = cls.getConstructor(View.class).newInstance(convertView);
                    convertView.setTag(viewHolder);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            viewHolder = convertView.getTag();
        }
        convertView.setVisibility(itemInfo.visible);
        return new ViewHolderView(viewHolder, convertView);
    }

    // 设置背景
    private void setItemBackground(ItemInfo itemInfo, View convertView) {
        if (itemInfo.isMust == 1) {
            convertView.setBackgroundResource(R.drawable.bg_underline_notnull_05dip);
        } else if (itemInfo.isMust == 0) {
            convertView.setBackgroundResource(R.drawable.selector_underline_05dip);
        }
    }

    private void addEditTextChangedListener(final int key, EditText et, final ItemInfo itemInfo) {
        RxTextView.textChanges(et) // 抛砖引玉
                .compose(((BaseActivity) mContext).<CharSequence>bindToLifecycle())  // Rxlifecycle自动取消订阅，防止内存泄露
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        mILdc.getItemInfo(key).value = charSequence.toString();
                        if (null != itemInfo.callback) {
                            IItemEventCallBack callback = (IItemEventCallBack) itemInfo.callback;
                            callback.handleEvent(charSequence.toString(), "");
                        }
                    }
                });
    }

    /**
     * 使用 Map按key进行排序
     *
     * @param map
     * @return
     */
    private Map<Integer, ItemInfo> sortMapByKey(Map<Integer, ItemInfo> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<Integer, ItemInfo> sortedMap = new TreeMap<>(
                new Comparator<Integer>() {
                    @Override
                    public int compare(Integer a, Integer b) {
                        return a - b;
                    }
                });
        sortedMap.putAll(map);
        return sortedMap;
    }

    private class ViewHolderView {
        public Object viewHolder;
        public View convertView;

        public ViewHolderView(Object viewHolder, View convertView) {
            this.viewHolder = viewHolder;
            this.convertView = convertView;
        }
    }
}
