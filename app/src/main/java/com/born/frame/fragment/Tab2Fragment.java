package com.born.frame.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.born.frame.R;
import com.born.frame.activity.GroupActivity;

/**
 * TAB2
 */
public class Tab2Fragment extends BaseTitleFragment implements View.OnClickListener {

    private GroupActivity mParent;

    @Override
    protected View setContentView(Bundle savedInstanceState) {
        mParent = (GroupActivity) getActivity();
        return View.inflate(mParent, R.layout.fragment_tab2, null);
    }

    @Override
    protected void initControl() {
        setTitle("货源");
        setLeftVisible(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
            }
        }
    }

}
