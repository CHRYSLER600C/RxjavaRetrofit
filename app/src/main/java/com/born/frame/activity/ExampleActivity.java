package com.born.frame.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.born.frame.R;
import com.born.frame.adapter.ItemListAdapter;
import com.born.frame.adapter.ItemListDataClass;
import com.born.frame.adapter.ItemListDataClass.IItemCallBack;
import com.born.frame.adapter.ItemListDataClass.IItemEventCallBack;
import com.born.frame.dataclass.bean.NameValue;
import com.born.frame.dataclass.bean.PickerItem;
import com.born.frame.dataclass.bean.PickerValue;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by dongxie on 2017/3/22.
 */

public class ExampleActivity extends BaseTitleActivity {

    public static final int KEY_TET_SCHOOL = 10;
    public static final int KEY_TET_GRADE = 20;
    public static final int KEY_TEB_VERIFY_CODE = 22;
    public static final int KEY_LINE = 30;
    public static final int KEY_CUSTOM = 40;
    public static final int KEY_TEXT_SELECT_TIME = 50;
    public static final int KEY_TEXT_SELECT = 60;
    public static final int KEY_IMAGE_TEXT_TEXT = 70;
    public static final int KEY_LINE2 = 75;
    public static final int KEY_BUTTON = 80;
    public static final int KEY_TET_CLASS = 100;

    @Bind(R.id.llSvContainer)
    LinearLayout mLlSvContainer;

    private ItemListDataClass mILdc = new ItemListDataClass();
    private ItemListAdapter mILAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_sv_ll);
        initControls();
    }

    private void initControls() {
        setLeftBackClick();
        setTitle("EXAMPLE");

        addItemListDataClass();
    }

    private void addItemListDataClass() {
        mILdc.addCustomItem(KEY_CUSTOM, R.layout.activity_base_title, TitleViewHolder.class, new IItemCallBack() {

            @Override
            public void handleItem(View convertView, Object holder) {
                TitleViewHolder h = (TitleViewHolder) holder;
                h.mIvLeft.setVisibility(View.GONE);
                h.mTvTitle.setText("我是自定义View");
                h.mTvTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showToast("我是自定义View");
                    }
                });
            }
        });
        mILdc.addText(KEY_LINE, "我是分割线", "120-20", "#ff0000-#ccffcc", Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        mILdc.addTextEditText(KEY_TET_SCHOOL, "学校", "重庆三中", "", "请填写学校", 0, 0);
        mILdc.addTextEditText(KEY_TET_GRADE, "年级", "5", "年级", "请填写年级", 1, 1);
        mILdc.addTextSelectTime(KEY_TEXT_SELECT_TIME, "入学时间", "", 1, 1);

        ArrayList<NameValue> list1 = new ArrayList<>();
        list1.add(new NameValue("黑龙江", "3585"));
        list1.add(new NameValue("宁夏", "3742"));
        list1.add(new NameValue("山西", "3802"));
        ArrayList<NameValue> list2 = new ArrayList<>();
        list2.add(new NameValue("哈尔滨", "3586"));
        list2.add(new NameValue("北安", "3587"));
        list2.add(new NameValue("大庆", "3588"));
        ArrayList<NameValue> list22 = new ArrayList<>();
        list22.add(new NameValue("宁夏1", "3586"));
        list22.add(new NameValue("宁夏2", "3587"));
        ArrayList<NameValue> list3 = new ArrayList<>();
        list3.add(new NameValue("哈尔滨", "4464"));
        list3.add(new NameValue("松北", "4465"));
        list3.add(new NameValue("道里", "4466"));
        ArrayList<NameValue> list4 = new ArrayList<>();
        list4.add(new NameValue("萨尔图", "4580"));
        list4.add(new NameValue("龙凤", "4581"));
        PickerValue pickerValue = new PickerValue();
        pickerValue.isRelation = true;
        pickerValue.list1 = list1;
        pickerValue.map2 = new HashMap<>();
        pickerValue.map2.put("3585", list2);
        pickerValue.map2.put("3742", list22);
        pickerValue.map3 = new HashMap<>();
        pickerValue.map3.put("3586", list3);
        pickerValue.map3.put("3588", list4);
        mILdc.addTextSelect(KEY_TEXT_SELECT, "居住地址", new PickerItem(new NameValue("重庆", ""), new NameValue("渝北", ""),
                new NameValue("金星", "")), "栋", pickerValue, 1, 1);
        mILdc.addTextText(KEY_IMAGE_TEXT_TEXT, "授信管理", "已授信", R.drawable.ic_registration_mobile, 1, new
                IItemEventCallBack() {
                    @Override
                    public void handleEvent(Object value1, Object value2) {
                        showToast("你要去哪");
                    }
                });
        mILdc.addText(KEY_LINE2, " ", "80", "#00000000-#00000000", -1);
        mILdc.addButton(KEY_BUTTON, "保存", "20", "30", -1, 1, new IItemEventCallBack() {
            @Override
            public void handleEvent(Object value1, Object value2) {
                mILdc.addTextEditText(KEY_TET_CLASS, "班级", "三班", "", "请填写班级", 1, 0);
                mILAdapter.addItemView(KEY_TET_CLASS); //新增

                mILAdapter.removeItemView(KEY_CUSTOM); //删除

                mILdc.getItemInfo(KEY_LINE).name = "View.GONE";
                mILAdapter.refreshItemView(KEY_LINE); // 刷新
            }
        });
        mILdc.addTextEditButton(KEY_TEB_VERIFY_CODE, "手机号码", "", "请输入手机号码", 11, 0, new IItemEventCallBack() {
            @Override
            public void handleEvent(Object value1, Object value2) {
                showToast("再点一下");
                mILAdapter.setItemViewVisible(KEY_TEB_VERIFY_CODE, View.GONE);
            }
        });

        mILAdapter = new ItemListAdapter(mLlSvContainer, mILdc);
        mILAdapter.refreshAllItemView();
    }


    public static class TitleViewHolder {
        @Bind(R.id.ivLeft)
        ImageView mIvLeft;
        @Bind(R.id.tvLeft)
        TextView mTvLeft;
        @Bind(R.id.tvTitle)
        TextView mTvTitle;
        @Bind(R.id.ivRight)
        ImageView mIvRight;
        @Bind(R.id.tvRight)
        TextView mTvRight;

        public TitleViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
