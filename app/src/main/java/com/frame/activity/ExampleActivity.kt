package com.frame.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.frame.R;
import com.frame.adapter.CommVHolder;
import com.frame.adapter.ItemListAdapter;
import com.frame.adapter.ItemListDataClass;
import com.frame.common.CommonData;
import com.frame.dataclass.DataClass;
import com.frame.dataclass.bean.NameValue;
import com.frame.dataclass.bean.PickerItem;
import com.frame.dataclass.bean.PickerValue;
import com.frame.httputils.HttpUtil2;
import com.frame.httputils.OkHttpUtil2;
import com.frame.httputils.RequestBuilder;
import com.frame.observers.ProgressObserver;
import com.frame.utils.CU;
import com.frame.utils.JU;
import com.frame.view.dialog.CustomPopView;
import com.frame.view.dialog.PhotoDialog;
import com.google.gson.internal.LinkedTreeMap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;

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

    @BindView(R.id.llSvContainer)
    LinearLayout mLlSvContainer;

    private ItemListDataClass mILdc = new ItemListDataClass();
    private ItemListAdapter mILAdapter;
    private PhotoDialog mPhotoDialog;

    CustomPopView mPvExample;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_sv_ll);
        initControls();
    }

    private void initControls() {
        setTitleText("EXAMPLE");

        addItemListDataClass();
        if (mPvExample == null) {
            mPvExample = new CustomPopView.Builder(mBActivity, R.layout.common_popview_example, mCallBackPopView)
                    .setOutsideTouchDismiss(false)
                    .seBackDismiss(true)
                    .create();
            mPvExample.setAnimationStyle(R.style.AnimationBottomInOut);
        }
    }

    private void addItemListDataClass() {
        mILdc.addCustomItem(KEY_CUSTOM, R.layout.common_item_text_text, (View convertView, CommVHolder h) -> {
            h.setText(R.id.tvItemNameTt, "自定义View");
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
        mILdc.addTextText(KEY_IMAGE_TEXT_TEXT, "图片管理", "查看", R.drawable.ic_default, 1, (value1, value2) -> {
            ArrayList<String> imgList = new ArrayList<>();
            imgList.add("https://pics5.baidu.com/feed/0d338744ebf81a4c9f3f23ea1e4cdb5c252da675" +
                    ".jpeg?token=79091d07753b5b65acb005b78f73bfcb&s=86BC7C84C6CB1747788D50960300D080");
            imgList.add("https://pics6.baidu.com/feed/c2fdfc039245d6887d1325116ca4c61bd31b24da" +
                    ".jpeg?token=b2c1c0e64c38691d7ed12b72a2f65309&s=A32A68A5565311D2183BA0810300308B");

            Intent intent = new Intent(mBActivity, BigPicActivity.class);
            intent.putExtra("index", 0);
            intent.putExtra("picUrls", imgList);
            intent.putExtra("picIds", "");
            ActivityUtils.startActivity(intent);
        });
        mILdc.addText(KEY_LINE2, " ", "80", "#00000000-#00000000", -1);
        mILdc.addButton(KEY_BUTTON, "保存", "20", "30", -1, 1, (value1, value2) -> {
            mILdc.addTextEditText(KEY_TET_CLASS, "班级", "三班", "", "请填写班级", 1, 0);
            mILAdapter.addItemView(KEY_TET_CLASS); //新增

            mILAdapter.removeItemView(KEY_CUSTOM); //删除

            mILdc.getItemInfo(KEY_LINE).name = "View.GONE";
            mILAdapter.refreshItemView(KEY_LINE); // 刷新
            doCommonGet("https://api.xiniuzc.com/data/city", null, new ProgressObserver<DataClass>
                    (mBActivity, true) {
                @Override
                public void onNext(DataClass dc) {
                    ArrayList<LinkedTreeMap<String, Object>> list = JU.al(dc.object);
                    ArrayList<NameValue> list11 = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        list11.add(new NameValue(JU.s(list.get(i), "name"), JU.s(list.get(i), "id")));
                    }
                    CU.showSinglePickerDialog(mBActivity, mTitleBar.getTitleText(), new PickerValue(list11));
                }
            });
        });
        mILdc.addTextEditButton(KEY_TEB_VERIFY_CODE, "手机号码", "", "请输入手机号码", 11, 0,
                (value1, value2) -> mPvExample.showOrDismiss(mLlSvContainer));

        mILAdapter = new ItemListAdapter(mBActivity, mLlSvContainer, mILdc);
        mILAdapter.refreshAllItemView();
    }

    private void showPhotoPickDialog(String filepath) {
        if (mPhotoDialog == null) {
            mPhotoDialog = new PhotoDialog(mBActivity);
        }
        if (mPhotoDialog.isShowing()) {
            mPhotoDialog.dismiss();
        }
        mPhotoDialog.setCaptureFile(filepath);
        mPhotoDialog.show();
    }

    private void uploadImg(final String path) {
        showProgressDialog();
        final String filePath = CU.bitmap2File(path, CU.FILE_PATH, CU.randomFileName(".jpg"));
        RequestBuilder.RequestObject request = new RequestBuilder.RequestObject();
        request.method = "imagesUpload";
        HashMap<String, Object> params = new HashMap<>();
        params.put("file", new File(filePath));
        params.put("use", "download");
        uploadFile(request, params, DataClass.class, new OkHttpUtil2.IRequestFileCallback() {

            @Override
            public <T> void ObjResponse(Boolean isSuccess, T responseObj, IOException ioException) {
                dismissProgressDialog();
                HttpUtil2.handleResponse(mBActivity, isSuccess, responseObj);
            }

            @Override
            public void ProgressResponse(long progress, long total) {
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CommonData.PHOTO_CAMERA:
                    uploadImg(mPhotoDialog.getCaptureFilePath());
                    break;
                case CommonData.PHOTO_GALLERY:
                    String sdPath = CU.getImagePathFromGallery(intent);
                    if (!ObjectUtils.isEmpty(sdPath)) {
                        uploadImg(sdPath);
                    }
                    break;
            }
        }
    }

    CustomPopView.ICallBackPopView mCallBackPopView = new CustomPopView.ICallBackPopView() {
        @Override
        public void handlePopView(CustomPopView popView, CommVHolder h, Object adapter) {
            h.setOnClickListener(R.id.ivCloseDetail, (View v) -> mPvExample.dismiss());
        }
    };
}
