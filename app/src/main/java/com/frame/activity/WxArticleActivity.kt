package com.frame.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import com.blankj.utilcode.util.ObjectUtils
import com.frame.R
import com.frame.common.CommonData
import com.frame.dataclass.DataClass
import com.frame.fragment.BaseTitleFragment
import com.frame.fragment.WxArticleDetailFragment
import com.frame.observers.ProgressObserver
import com.frame.utils.CU
import com.frame.utils.JU
import kotlinx.android.synthetic.main.activity_tablayout_viewpager.*
import java.util.*

/**
 */
class WxArticleActivity : BaseTitleActivity() {

    private val mFragments: MutableList<BaseTitleFragment> = ArrayList()
    var mMap: HashMap<String, Any>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tablayout_viewpager)
        initControl()
    }

    private fun initControl() {
        mMap = CU.cast(intent.getSerializableExtra(CommonData.PARAM1))
        if (ObjectUtils.isNotEmpty(JU.s(mMap, "name"))) setTitleText(JU.s(mMap, "name")) else setTitleText("公众号")
    }

    override fun onResume() {
        super.onResume()
        if (ObjectUtils.isEmpty(mFragments)) {
            val list = JU.al<ArrayList<AbstractMap<String, Any>?>>(mMap, "children")
            if (ObjectUtils.isNotEmpty(list)) initTabLayoutAndViewPager(list) else getWxArticleTitleData()
        }
    }

    private fun initTabLayoutAndViewPager(list: ArrayList<AbstractMap<String, Any>?>) {
        mFragments.clear()
        for (map in list) {
            mFragments.add(WxArticleDetailFragment.getInstance(map!!["id"].toString(), map["name"].toString()))
        }
        vpCommon.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return mFragments[position]
            }

            override fun getCount(): Int {
                return mFragments.size
            }

            override fun getPageTitle(position: Int): CharSequence {
                return list[position]?.get("name").toString()
            }
        }
        slidingTabLayout.setViewPager(vpCommon)
    }

    private fun getWxArticleTitleData() {
        doCommonGet("wxarticle/chapters/json", null, object : ProgressObserver<DataClass>(this, true) {
            override fun onNext(dc: DataClass) {
                initTabLayoutAndViewPager(JU.al(dc.obj, "data"))
            }
        })
    }

}