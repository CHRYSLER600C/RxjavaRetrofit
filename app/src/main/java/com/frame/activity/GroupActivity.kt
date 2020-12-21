package com.frame.activity

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentTransaction
import com.frame.R
import com.frame.fragment.*
import com.frame.utils.LU
import kotlinx.android.synthetic.main.activity_group.*

class GroupActivity : BaseActivity() {

    private var mCurrentFragment: BaseTitleFragment? = null           //当前选中的Fragment
    private val mTab1Fragment = Tab1Fragment()
    private val mTab2Fragment = Tab2Fragment()
    private val mTab3Fragment = Tab3Fragment()
    private val mTab4Fragment = Tab4Fragment()

    private var mExitTime: Long = 0

    companion object {
        const val TAB1 = "tab1"
        const val TAB2 = "tab2"
        const val TAB3 = "tab3"
        const val TAB4 = "tab4"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)
        initControl()
    }

    private fun initControl() {
        rgHomeTabs?.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbTab1 -> switchContent(mTab1Fragment)
                R.id.rbTab2 -> switchContent(mTab2Fragment)
                R.id.rbTab3 -> switchContent(mTab3Fragment)
                R.id.rbTab4 -> switchContent(mTab4Fragment)
            }
        }
        rgHomeTabs?.check(R.id.rbTab1)
    }

    fun setCurrentPage(pageName: String) {
        when (pageName) {
            TAB1 -> rgHomeTabs.check(R.id.rbTab1)
            TAB2 -> rgHomeTabs.check(R.id.rbTab2)
            TAB3 -> rgHomeTabs.check(R.id.rbTab3)
            TAB4 -> rgHomeTabs.check(R.id.rbTab4)
        }
    }

    fun onViewClicked(v: View?) {
        mCurrentFragment?.onViewClicked(v)
    }

    /**
     * 当fragment进行切换时，采用隐藏与显示的方法加载fragment以防止数据的重复加载
     *
     * @param toPage
     */
    private fun switchContent(toPage: BaseTitleFragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (mCurrentFragment !== toPage) {
            if (mCurrentFragment != null) {
                transaction.hide(mCurrentFragment!!)
            }
            if (!toPage.isAdded && null == supportFragmentManager.findFragmentByTag("TAG_$toPage")) {
                transaction.add(R.id.flContainer, toPage, "TAG_$toPage")
            }
            if (toPage.isHidden) {
                transaction.show(toPage)
            }
            mCurrentFragment = toPage
            if (!isFinishing && !isDestroyed) {
                transaction.commitAllowingStateLoss()
                supportFragmentManager.executePendingTransactions()
            }
        }
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() - mExitTime < 2000) {
            LU.logout()
            super.onBackPressed()
        } else {
            showShort("再点一次退出程序！")
            mExitTime = System.currentTimeMillis()
        }
    }

    override fun isActivityCanSlideBack() = false
}