package com.frame.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.frame.R;
import com.frame.core.dao.HistoryData;
import com.frame.core.db.DbHelperImpl;
import com.frame.dataclass.DataClass;
import com.frame.observers.ProgressObserver;
import com.frame.observers.RecycleObserver;
import com.frame.utils.CommonUtil;
import com.frame.utils.JU;
import com.frame.view.dialog.CommonDialog;
import com.google.gson.internal.LinkedTreeMap;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 */
public class SearchActivity extends BaseTitleActivity {

    @BindView(R.id.tflSearch)
    TagFlowLayout mTflSearch;
    @BindView(R.id.etSearch)
    EditText mEtSearch;
    @BindView(R.id.tvSearch)
    TextView mTvSearch;
    @BindView(R.id.tvNullHint)
    TextView mTvNullHint;
    @BindView(R.id.tflSearchHistory)
    TagFlowLayout mTflSearchHistory;

    private DbHelperImpl mDbHelperImpl = new DbHelperImpl();
    private List<LinkedTreeMap<String, Object>> mList;
    private List<HistoryData> mHistoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initControl();
    }

    protected void initControl() {
        setTitleBarHide();
        add2Disposable(RxTextView.textChanges(mEtSearch)
                .map((CharSequence charSequence) -> String.valueOf(charSequence))
                .subscribeWith(new RecycleObserver<CharSequence>() {
                    @Override
                    public void onNext(CharSequence s) {
                        mTvSearch.setEnabled(ObjectUtils.isNotEmpty(s));
                        mTvSearch.setBackgroundResource(ObjectUtils.isNotEmpty(s) ? R.drawable.selector_lightred : R
                                .drawable.shape_gray);
                    }
                }));
        mEtSearch.setOnKeyListener((View v, int keyCode, KeyEvent event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) startSearch();
            return false;
        });
        setSearchHistoryAdapter(mHistoryList);
        refreshHistoryData(null, false);
        mEtSearch.postDelayed(() -> KeyboardUtils.showSoftInput(mEtSearch), 100); //弹出输入法
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ObjectUtils.isEmpty(mList)) getNetData();
    }

    @OnClick({R.id.llGoBack, R.id.ivClearAll, R.id.tvSearch})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llGoBack:
                finish();
                break;
            case R.id.ivClearAll:
                CommonDialog.Builder builder = new CommonDialog.Builder(mContext, CommonDialog.DialogType.TYPE_SIMPLE)
                        .setTitle("提示")
                        .setMessage("确定清空搜索历史吗？")
                        .setOkBtn("确定", (View v, String value) -> refreshHistoryData(null, true))
                        .setCancelBtn("取消", null);
                builder.create().show();
                break;
            case R.id.tvSearch:
                startSearch();
                break;
        }
    }

    private void startSearch() {
        String key = mEtSearch.getText().toString();
        refreshHistoryData(mDbHelperImpl.addHistoryData(key), false);
        ActivityUtils.startActivity(new Intent(mContext, SearchListActivity.class)
                .putExtra("key", key));
    }

    private void refreshHistoryData(List<HistoryData> list, boolean isClear) {
        mHistoryList.clear();
        if (isClear) {
            mDbHelperImpl.clearHistoryData();
        } else {
            mHistoryList.addAll(ObjectUtils.isEmpty(list) ? mDbHelperImpl.loadAllHistoryData() : list);
            Collections.reverse(mHistoryList);
        }
        mTflSearchHistory.getAdapter().notifyDataChanged();
        mTvNullHint.setVisibility(mHistoryList.size() > 0 ? View.GONE : View.VISIBLE);
    }

    public void setSearchHistoryAdapter(List<HistoryData> list) {
        mTflSearchHistory.setAdapter(new TagAdapter<HistoryData>(list) {
            @Override
            public View getView(FlowLayout parent, int position, HistoryData item) {
                TextView tv = (TextView) LayoutInflater.from(mContext).inflate(R.layout.flow_layout_tv,
                        parent, false);
                tv.setPadding(ConvertUtils.dp2px(12), ConvertUtils.dp2px(6), ConvertUtils.dp2px(12),
                        ConvertUtils.dp2px(6));
                tv.setText(item.getData());
                tv.setTextColor(CommonUtil.randomColor());
                return tv;
            }
        });
        mTflSearchHistory.setOnTagClickListener((view, position1, parent1) -> {
            ActivityUtils.startActivity(new Intent(mContext, SearchListActivity.class)
                    .putExtra("key", list.get(position1).getData()));
            return true;
        });
    }

    private void getNetData() {
        BaseActivity.doCommonGetImpl("hotkey/json", null, new ProgressObserver<DataClass>(this, true) {
            @Override
            public void onNext(DataClass dc) {
                mList = JU.al(dc.object, "data");
                mTflSearch.setAdapter(new TagAdapter<LinkedTreeMap<String, Object>>(mList) {
                    @Override
                    public View getView(FlowLayout parent, int position, LinkedTreeMap<String, Object> map) {
                        TextView tv = (TextView) LayoutInflater.from(mContext).inflate(R.layout.flow_layout_tv,
                                parent, false);
                        tv.setText(JU.s(map, "name"));
                        tv.setBackgroundColor(CommonUtil.randomTagColor());
                        tv.setTextColor(getResources().getColor(R.color.white));
                        return tv;
                    }
                });
                mTflSearch.setOnTagClickListener((view, position1, parent1) -> {
                    ActivityUtils.startActivity(new Intent(mContext, SearchListActivity.class)
                            .putExtra("key", JU.s(mList.get(position1), "name")));
                    return true;
                });
            }
        });
    }

}
