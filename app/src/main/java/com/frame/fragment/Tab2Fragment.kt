package com.frame.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.frame.R

/**
 * TAB2
 */
class Tab2Fragment : BaseTitleFragment() {
    override fun setContentView(savedInstanceState: Bundle?): View {
        return View.inflate(mBActivity, R.layout.fragment_tab2, null)
    }

    override fun initControl() {
        setTitleText("货源")
        setLeftBarHide()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
            }
        }
    }
}