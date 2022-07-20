package com.frame.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.frame.R;

/**
 * TAB3
 */
public class Tab3Fragment extends BaseTitleFragment implements View.OnClickListener {


    @Override
    protected View setContentView(Bundle savedInstanceState) {
        return View.inflate(mBActivity, R.layout.fragment_tab3, null);
    }

    @Override
    protected void initControl() {
        setTitleText("车源");
        setLeftBarHide();
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
