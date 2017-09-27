package com.born.frame.view.pickerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.born.frame.R;
import com.born.frame.dataclass.bean.NameValue;
import com.born.frame.dataclass.bean.PickerItem;
import com.born.frame.dataclass.bean.PickerValue;

import java.util.ArrayList;
import java.util.Set;

/**
 * 城市Picker
 *
 * @author lnh
 */
public class PickerView extends LinearLayout {

    private static final int REFRESH_VIEW = 0x001;

    /**
     * 滑动控件
     */
    private PickerScroller pickerScroller1;
    private PickerScroller pickerScroller2;
    private PickerScroller pickerScroller3;
    /**
     * 选择监听
     */
    private OnPickerSelectingListener onSelectingListener;
    /**
     * Picker数据
     */
    private PickerValue mPickerValue;
    private ArrayList<NameValue> listNothing = new ArrayList<>();

    private int mIndex1 = -1;
    private int mIndex2 = -1;
    private Context mContext;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESH_VIEW:
                    if (onSelectingListener != null)
                        onSelectingListener.selected(true);
                    break;
                default:
                    break;
            }
        }
    };

    public PickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public PickerView(Context context) {
        super(context);
        this.mContext = context;
    }

    public void setPickerData(PickerItem pickerItem, PickerValue pickerValue) {
        if (pickerValue == null) {
            return;
        }
        mPickerValue = pickerValue;
        if (mPickerValue.list1 != null && mPickerValue.list1.size() > 0) {
            pickerScroller1.setData(mPickerValue.list1);
            int defaultId1 = 0; // 查找已选name的id
            if (pickerItem != null && pickerItem.nv1 != null) {
                defaultId1 = getSelectedId(pickerItem.nv1.name, mPickerValue.list1);
            }
            pickerScroller1.setDefault(defaultId1);
        } else {
            pickerScroller1.setVisibility(View.GONE);
        }

        if (mPickerValue.map2 != null && mPickerValue.map2.size() > 0) {
            setPicker2Data(pickerItem, mPickerValue, -1);
        } else {
            pickerScroller2.setVisibility(View.GONE);
        }

        if (mPickerValue.map3 != null && mPickerValue.map3.size() > 0) {
            setPicker3Data(pickerItem, mPickerValue, -1);
        } else {
            pickerScroller3.setVisibility(View.GONE);
        }
    }

    /**
     * 刷新第2个picker
     *
     * @param pickerItem  已选数据
     * @param pickerValue 数据结构
     * @param id          上一项选择的id
     */
    private void setPicker2Data(PickerItem pickerItem, PickerValue pickerValue, int id) {
        if (pickerScroller2.getVisibility() == View.GONE) {
            return;
        }

        String key;
        if (id >= 0) {
            key = pickerScroller1.getNameValueById(id).value;
        } else {
            key = pickerScroller1.getSelectedValue();
        }
        Set<String> keys = pickerValue.map2.keySet();
        if (pickerValue.isRelation) { // 强关联
            if (keys.contains(key)) {
                pickerScroller2.setData(pickerValue.map2.get(key));
            } else {
                pickerScroller2.setData(listNothing);
            }
        } else { // 无关联取第一项
            Object[] objs = keys.toArray();
            if (objs.length > 0) {
                pickerScroller2.setData(pickerValue.map2.get(objs[0]));
            } else {
                pickerScroller2.setData(listNothing);
            }
        }
        int defaultId2 = 0; // 查找已选name的id
        if (pickerItem != null && pickerItem.nv2 != null) {
            defaultId2 = getSelectedId(pickerItem.nv2.name, pickerScroller2.getData());
        }
        pickerScroller2.setDefault(defaultId2);
    }

    /**
     * 刷新第三个picker
     *
     * @param pickerItem  已选数据
     * @param pickerValue 数据结构
     * @param id          上一项选择的id
     */
    private void setPicker3Data(PickerItem pickerItem, PickerValue pickerValue, int id) {
        if (pickerScroller3.getVisibility() == View.GONE) {
            return;
        }

        String key;
        if (id >= 0) {
            key = pickerScroller2.getNameValueById(id).value;
        } else {
            key = pickerScroller2.getSelectedValue();
        }
        Set<String> keys = pickerValue.map3.keySet();
        if (pickerValue.isRelation) { // 强关联
            if (keys.contains(key)) {
                pickerScroller3.setData(pickerValue.map3.get(key));
            } else {
                pickerScroller3.setData(listNothing);
            }
        } else { // 无关联取第一项
            Object[] objs = keys.toArray();
            if (objs.length > 0) {
                pickerScroller3.setData(pickerValue.map3.get(objs[0]));
            } else {
                pickerScroller3.setData(listNothing);
            }
        }
        int defaultId3 = 0; // 查找已选name的id
        if (pickerItem != null && pickerItem.nv3 != null) {
            defaultId3 = getSelectedId(pickerItem.nv3.name, pickerScroller3.getData());
        }
        pickerScroller3.setDefault(defaultId3);
    }

    private int getSelectedId(String name, ArrayList<NameValue> list) {
        int id = 0;
        if (!TextUtils.isEmpty(name)) {
            for (int i = 0; i < list.size(); i++) {
                if (name.equals(list.get(i).name)) {
                    id = i;
                    break;
                }
            }
        }
        return id;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater.from(getContext()).inflate(R.layout.common_picker, this);

        pickerScroller1 = (PickerScroller) findViewById(R.id.pickerScroller1);
        pickerScroller2 = (PickerScroller) findViewById(R.id.pickerScroller2);
        pickerScroller3 = (PickerScroller) findViewById(R.id.pickerScroller3);

        pickerScroller1.setOnSelectListener(new PickerScroller.OnSelectListener() {
            @Override
            public void endSelect(int id, String text) {
                if (TextUtils.isEmpty(text)) {
                    return;
                }
                if (mIndex1 != id) {
                    setPicker2Data(null, mPickerValue, id);
                    setPicker3Data(null, mPickerValue, 0); // picker2的id默认0

                    int size1 = Integer.valueOf(pickerScroller1.getListSize());
                    if (id >= size1) {
                        pickerScroller1.setDefault(size1 - 1);
                    }
                }
                mIndex1 = id;
                mHandler.sendEmptyMessage(REFRESH_VIEW);
            }

            @Override
            public void selecting(int id, String text) {
            }
        });

        pickerScroller2.setOnSelectListener(new PickerScroller.OnSelectListener() {

            @Override
            public void endSelect(int id, String text) {
                if (TextUtils.isEmpty(text)) {
                    return;
                }
                if (mIndex2 != id) {
                    setPicker3Data(null, mPickerValue, id);

                    int size2 = Integer.valueOf(pickerScroller2.getListSize());
                    if (id >= size2) {
                        pickerScroller1.setDefault(size2 - 1);
                    }
                }
                mIndex2 = id;
                mHandler.sendEmptyMessage(REFRESH_VIEW);
            }

            @Override
            public void selecting(int id, String text) {

            }
        });
        pickerScroller3.setOnSelectListener(new PickerScroller.OnSelectListener() {

            @Override
            public void endSelect(int id, String text) {
                if (TextUtils.isEmpty(text)) {
                    return;
                }
                mHandler.sendEmptyMessage(REFRESH_VIEW);
            }

            @Override
            public void selecting(int id, String text) {
            }
        });
    }

    public NameValue getSelectedNameValue1() {
        return pickerScroller1.getSelectedNameValue();
    }

    public NameValue getSelectedNameValue2() {
        return pickerScroller2.getSelectedNameValue();
    }

    public NameValue getSelectedNameValue3() {
        return pickerScroller3.getSelectedNameValue();
    }

    public void setOnSelectingListener(OnPickerSelectingListener onSelectingListener) {
        this.onSelectingListener = onSelectingListener;
    }

    public interface OnPickerSelectingListener {
        public void selected(boolean selected);
    }
}
