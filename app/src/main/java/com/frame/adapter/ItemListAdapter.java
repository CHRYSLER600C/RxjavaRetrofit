package com.frame.adapter;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ObjectUtils;
import com.frame.R;
import com.frame.activity.BaseActivity;
import com.frame.adapter.ItemListDataClass.IItemCallBack;
import com.frame.adapter.ItemListDataClass.IItemEventCallBack;
import com.frame.adapter.ItemListDataClass.ItemInfo;
import com.frame.dataclass.bean.NameValue;
import com.frame.dataclass.bean.PickerItem;
import com.frame.dataclass.bean.PickerValue;
import com.frame.observers.RecycleObserver;
import com.frame.utils.CommonUtil;
import com.frame.view.dialog.PickerDialog;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class ItemListAdapter {

    private BaseActivity mBaseActivity;
    private LinearLayout mLlParent;         // 父布局
    private ItemListDataClass mILdc;        // 布局数据
    private LayoutInflater mInflater;
    private Object[] mKeys;
    private CommonViewHolder holder = null;

    /**
     * ========================================== public function ==========================================
     *
     * @param mLlParent 父布局
     * @param ildc      布局数据
     */
    public ItemListAdapter(BaseActivity baseActivity, LinearLayout mLlParent, ItemListDataClass ildc) {
        this.mBaseActivity = baseActivity;
        this.mLlParent = mLlParent;
        this.mILdc = ildc;
        this.mInflater = LayoutInflater.from(mBaseActivity);
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
        mBaseActivity.add2Disposable(Observable.fromArray(mKeys)
                .map((Object o) -> getView((int) o, null))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new RecycleObserver<View>() {
                    @Override
                    public void onNext(View view) {
                        if (ObjectUtils.isNotEmpty(view)) mLlParent.addView(view);
                    }
                }));
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
        Collections.sort(keyList, (Integer integer1, Integer integer2) -> integer1 - integer2);
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
        final ItemInfo itemInfo = mILdc.getItemInfo(key);

        switch (itemInfo.type) {
            case ItemListDataClass.TYPE_TEXT:
                holder = getSVH(convertView, R.layout.common_item_line);
                holder.itemView.setVisibility(itemInfo.visible);

                if (ObjectUtils.isNotEmpty(itemInfo.name)) {
                    holder.setText(R.id.tvItemLine, itemInfo.name);
                }
                if (ObjectUtils.isNotEmpty(itemInfo.value)) { // 高度-字体大小 eg:(100-20)可只传第一个
                    String[] values = itemInfo.value.split("-");
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            Integer.parseInt(values[0]));
                    holder.setLayoutParams(R.id.tvItemLine, lp);
                    if (values.length > 1) {
                        holder.setTextSize(R.id.tvItemLine, Integer.parseInt(values[1]));
                    }
                }
                if (ObjectUtils.isNotEmpty(itemInfo.value2)) { // 字体颜色值-背景颜色值 eg:(#333333-#cccccc)可只传第一个
                    String[] values2 = ((String) itemInfo.value2).split("-");
                    holder.setTextColor(R.id.tvItemLine, Color.parseColor(values2[0]));
                    if (values2.length > 1) {
                        holder.setBackgroundColor(R.id.tvItemLine, Color.parseColor(values2[1]));
                    }
                }
                if ((Integer) itemInfo.value3 > 0) {
                    holder.setGravity(R.id.tvItemLine, (Integer) itemInfo.value3);
                }
                break;

            case ItemListDataClass.TYPE_TEXT_EDIT_TEXT:
                holder = getSVH(convertView, R.layout.common_item_text_edit_text);
                holder.itemView.setVisibility(itemInfo.visible);
                setItemBackground(itemInfo, holder.itemView);// 设置背景

                holder.setText(R.id.tvItemNameTet, itemInfo.name);
                holder.setText(R.id.etItemContentTet, itemInfo.value);
                holder.setEnabled(R.id.etItemContentTet, itemInfo.isEdit == 1);
                if (itemInfo.isEdit == 1 && ObjectUtils.isNotEmpty(itemInfo.value3)) {
                    if ("USE_NAME".equals(itemInfo.value3)) {
                        holder.setHint(R.id.etItemContentTet, "请输入" + itemInfo.name); // 设置Hint值
                    } else {
                        holder.setHint(R.id.etItemContentTet, (String) itemInfo.value3); // 设置Hint值
                    }
                }
                if (ObjectUtils.isNotEmpty(itemInfo.value2)) {
                    holder.setText(R.id.tvItemUnitTet, (String) itemInfo.value2); // 单位
                } else {
                    holder.setVisibility(R.id.tvItemUnitTet, View.GONE);
                }
                addEditTextChangedListener(key, holder.findViewById(R.id.etItemContentTet), itemInfo);
                break;

            case ItemListDataClass.TYPE_TEXT_SELECT:
                holder = getSVH(convertView, R.layout.common_item_select);
                holder.itemView.setVisibility(itemInfo.visible);
                setItemBackground(itemInfo, holder.itemView);// 设置背景

                holder.setText(R.id.tvSelectName, itemInfo.name);
                holder.setText(R.id.tvSelectUnit, itemInfo.value);
                PickerItem pickerItem = (PickerItem) itemInfo.value2;
                if (pickerItem != null) {
                    holder.setText(R.id.tvSelectContent1, pickerItem.nv1.name);
                    if (pickerItem.nv2 == null) {
                        holder.setVisibility(R.id.tvSelectContent2, View.GONE);
                    } else {
                        holder.setText(R.id.tvSelectContent2, pickerItem.nv2.name);
                    }
                    if (pickerItem.nv3 == null) {
                        holder.setVisibility(R.id.tvSelectContent3, View.GONE);
                    } else {
                        holder.setText(R.id.tvSelectContent3, pickerItem.nv3.name);
                    }
                }
                holder.findViewById(R.id.llSelectContent).setEnabled(itemInfo.isEdit == 1);

                final PickerValue pickerValue = (PickerValue) itemInfo.value3;
                final PickerDialog.Builder pickerBuilder = new PickerDialog.Builder(mBaseActivity);
                pickerBuilder.setBtnOk(pickerValue, "bottom", (NameValue nv1, NameValue nv2, NameValue nv3) -> {
                    itemInfo.value2 = new PickerItem(nv1, nv2, nv3);
                    holder.setText(R.id.tvSelectContent1, nv1.name);
                    holder.setText(R.id.tvSelectContent2, nv2.name);
                    holder.setText(R.id.tvSelectContent3, nv3.name);
                    if (null != itemInfo.callback) { // 选择后回调处理数据
                        IItemEventCallBack callback = (IItemEventCallBack) itemInfo.callback;
                        callback.handleEvent(itemInfo.value2, "");
                    }
                }).setPickedData((PickerItem) itemInfo.value2);
                if (itemInfo.isEdit == 1) {
                    holder.findViewById(R.id.llSelectContent).setOnClickListener((View view) -> {
                        pickerBuilder.setPickedData((PickerItem) itemInfo.value2);
                        if (pickerValue != null && pickerValue.list1.size() == 0) {
                            Toast.makeText(mBaseActivity, "暂无数据", Toast.LENGTH_SHORT).show();
                        } else {
                            CommonUtil.showAnimatDialog(pickerBuilder.create());
                        }
                    });
                } else {
                    holder.setTextColor(R.id.tvSelectContent1, Color.GRAY);
                    holder.setTextColor(R.id.tvSelectContent2, Color.GRAY);
                    holder.setTextColor(R.id.tvSelectContent3, Color.GRAY);
                }
                break;

            case ItemListDataClass.TYPE_TEXT_SELECT_TIME:
                holder = getSVH(convertView, R.layout.common_item_select_time);
                holder.itemView.setVisibility(itemInfo.visible);
                setItemBackground(itemInfo, holder.itemView);// 设置背景

                holder.setText(R.id.tvSelectTimeName, itemInfo.name);
                holder.setText(R.id.tvSelectTimeContent, itemInfo.value);
                holder.setEnabled(R.id.tvSelectTimeContent, itemInfo.isEdit == 1);
                holder.setHint(R.id.tvSelectTimeContent, "请选择");

                final TextView tv = holder.findViewById(R.id.tvSelectTimeContent);
                if (itemInfo.isEdit == 1) {
                    holder.setOnClickListener(R.id.tvSelectTimeContent, (View view) -> {
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

                        final DatePickerDialog datePickerDialog = new DatePickerDialog(mBaseActivity,
                                (DatePicker datePicker, int year, int month, int day) -> {
                                    itemInfo.value = String.format("%02d-%02d-%02d", year, ++month, day);
                                    tv.setText(itemInfo.value);
                                }, yearInt, monthInt, dayInt);

                        Window window = datePickerDialog.getWindow();
                        window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
                        window.setWindowAnimations(R.style.AnimationBottomInOut); // 添加动画
                        datePickerDialog.show();
                    });
                } else {
                    holder.setTextColor(R.id.tvSelectTimeContent, Color.GRAY);
                }
                break;

            case ItemListDataClass.TYPE_TEXT_TEXT:
                holder = getSVH(convertView, R.layout.common_item_text_text);
                holder.itemView.setVisibility(itemInfo.visible);
                setItemBackground(itemInfo, holder.itemView);// 设置背景

                holder.setTextViewDrawableLeft(R.id.tvItemNameTt, (int) itemInfo.value2, 25, 25, 10);
                holder.setText(R.id.tvItemNameTt, itemInfo.name);
                holder.setText(R.id.tvItemContentTt, itemInfo.value);
                if (itemInfo.isEdit == 1) { // 点击事件回调
                    holder.itemView.setOnClickListener((View view) -> {
                        if (null != itemInfo.callback) {
                            IItemEventCallBack callback = (IItemEventCallBack) itemInfo.callback;
                            callback.handleEvent(itemInfo.name, itemInfo.value);
                        }
                    });
                }
                break;

            case ItemListDataClass.TYPE_BUTTON:
                holder = getSVH(convertView, R.layout.common_item_button);
                holder.itemView.setVisibility(itemInfo.visible);

                holder.setText(R.id.btnCommon, itemInfo.name);
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.findViewById(R.id.btnCommon)
                        .getLayoutParams();
                lp.setMargins(lp.leftMargin, Integer.parseInt(itemInfo.value), lp.rightMargin, Integer.parseInt(
                        (String) itemInfo.value2));
                if ((int) itemInfo.value3 > 0) {
                    holder.setBackgroundResource(R.id.btnCommon, (int) itemInfo.value3);
                }
                if (itemInfo.isEdit == 1) { // 点击事件回调
                    holder.setOnClickListener(R.id.btnCommon, (View view) -> {
                        if (null != itemInfo.callback) {
                            IItemEventCallBack callback = (IItemEventCallBack) itemInfo.callback;
                            callback.handleEvent(itemInfo.name, "");
                        }
                    });
                } else {
                    holder.setBackgroundResource(R.id.btnCommon, R.drawable.shape_btn_common);
                    holder.setTextColor(R.id.btnCommon, Color.parseColor("#999999"));
                }
                break;

            case ItemListDataClass.TYPE_TEXT_EDIT_BUTTON:
                holder = getSVH(convertView, R.layout.common_item_text_edit_btn);
                holder.itemView.setVisibility(itemInfo.visible);
                setItemBackground(itemInfo, holder.itemView);// 设置背景

                holder.setText(R.id.tvItemNameTeb, itemInfo.name);
                holder.setVisibility(R.id.tvItemNameTeb, ObjectUtils.isEmpty(itemInfo.name) ? View.GONE : View.VISIBLE);
                holder.setText(R.id.etItemContentTeb, itemInfo.value);
                holder.setEnabled(R.id.etItemContentTeb, itemInfo.isEdit / 10 == 1);
                if (itemInfo.isEdit / 10 == 1 && ObjectUtils.isNotEmpty(itemInfo.value2)) {
                    if ("USE_NAME".equals(itemInfo.value2)) {
                        holder.setHint(R.id.etItemContentTeb, "请输入" + itemInfo.name); // 设置Hint值
                    } else {
                        holder.setHint(R.id.etItemContentTeb, (String) itemInfo.value2); // 设置Hint值
                    }
                }
                addEditTextChangedListener(key, holder.findViewById(R.id.etItemContentTeb), itemInfo);
                if (itemInfo.isEdit % 10 == 1) { // 点击事件回调
                    if (null != itemInfo.value3) {
                        holder.setOnClickListener(R.id.btnItemTeb, (View view) -> {
                            IItemEventCallBack callback = (IItemEventCallBack) itemInfo.value3;
                            callback.handleEvent(view, "");
                        });
                    } else {
                        holder.setVisibility(R.id.btnItemTeb, View.GONE);
                    }
                }
                break;

            case ItemListDataClass.TYPE_CUSTOM:
                holder = getSVH(convertView, Integer.parseInt(itemInfo.value));
                holder.itemView.setVisibility(itemInfo.visible);
                setItemBackground(itemInfo, holder.itemView);// 设置背景

                if (itemInfo.callback != null) {
                    IItemCallBack callback = (IItemCallBack) itemInfo.callback;
                    callback.handleItem(holder.itemView, holder);
                }
                break;
        }
        return holder != null ? holder.itemView : convertView;
    }

    private CommonViewHolder getSVH(View convertView, int resId) {
        if (convertView == null) {
            return CommonViewHolder.get(null, mInflater.inflate(resId, null));
        } else { // When convertView != null, parent must be an AbsListView.
            return CommonViewHolder.get(convertView, null);
        }
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
        mBaseActivity.add2Disposable(RxTextView.textChanges(et) // 抛砖引玉
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new RecycleObserver<CharSequence>() {
                    @Override
                    public void onNext(CharSequence charSequence) {
                        mILdc.getItemInfo(key).value = charSequence.toString();
                        if (null != itemInfo.callback) {
                            IItemEventCallBack callback = (IItemEventCallBack) itemInfo.callback;
                            callback.handleEvent(charSequence.toString(), "");
                        }
                    }
                }));
    }

    /**
     * 使用 Map按key进行排序
     */
    private Map<Integer, ItemInfo> sortMapByKey(Map<Integer, ItemInfo> map) {
        if (map == null || map.isEmpty()) return null;
        Map<Integer, ItemInfo> sortedMap = new TreeMap<>((Integer a, Integer b) -> a - b);
        sortedMap.putAll(map);
        return sortedMap;
    }
}
