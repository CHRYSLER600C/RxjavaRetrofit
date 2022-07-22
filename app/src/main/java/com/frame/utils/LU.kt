package com.frame.utils

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ObjectUtils
import com.frame.R
import com.frame.activity.*
import com.frame.common.CommonData
import com.frame.dataclass.bean.Template
import com.frame.httputils.ImageLoaderUtil
import com.frame.httputils.OkHttpUtil
import com.frame.httputils.OkHttpUtil2
import com.google.gson.internal.LinkedTreeMap
import com.youth.banner.Banner
import com.youth.banner.BannerConfig
import com.youth.banner.Transformer
import com.youth.banner.loader.ImageLoader

/**
 * 逻辑处理类
 */

object LU {

    fun gotoActivityScale(view: View?, intent: Intent) {
        if (!Build.MANUFACTURER.contains("samsung") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && view != null) {
            val options = ActivityOptions.makeScaleUpAnimation(view, view.width / 2, view.height / 2, 0, 0)
            ActivityUtils.startActivity(intent, options.toBundle())
        } else {
            ActivityUtils.startActivity(intent)
        }
    }

    fun gotoActivityAnim(view: View?, intent: Intent) {
        val activity = view?.context as? Activity ?: ActivityUtils.getTopActivity()
        if (!Build.MANUFACTURER.contains("samsung") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val options = ActivityOptions.makeSceneTransitionAnimation(activity, view, activity.getString(R.string.share_view))
            ActivityUtils.startActivity(intent, options.toBundle())
        } else {
            ActivityUtils.startActivity(intent)
        }
    }

    fun gotoActivityAnim(view: View?, clz: Class<out Activity>) {
        val activity = view?.context as? Activity ?: ActivityUtils.getTopActivity()
        gotoActivityAnim(view, Intent(activity, clz))
    }

    fun getListTab4(): MutableList<Template> {
        val list: MutableList<Template> = ArrayList()
        list.add(Template(R.drawable.ic_default, "Example", "", ExampleActivity::class.java))
        return list
    }

    fun getBlockList(): MutableList<Template> {
        val list: MutableList<Template> = ArrayList()
        list.run {
            add(Template(R.drawable.wan_icon_1, "知识体系", "", KnowledgeHierarchyActivity::class.java))
            add(Template(R.drawable.wan_icon_2, "公众号", "", WxArticleActivity::class.java))
            add(Template(R.drawable.wan_icon_3, "导航", "", NavigationActivity::class.java))
            add(Template(R.drawable.wan_icon_4, "项目", "", ProjectActivity::class.java))
        }
        return list
    }

    /**
     * 退出登录，清空数据
     */
    fun logout() {
        OkHttpUtil.getInstance().cleanCookie()
        OkHttpUtil2.getInstance().cleanCookie()
    }

    /**
     * 跳转登录界面
     *
     * @param any Activity or Fragment
     */
    fun gotoLogin(any: Any?) {
        var activity = any as? Activity
        if (activity == null) activity = (any as? Fragment)?.activity
        activity?.run {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivityForResult(intent, CommonData.REQUEST_CODE_LOGIN)
        }
    }

    class BannerImageLoader : ImageLoader() {
        override fun displayImage(context: Context, o: Any, iv: ImageView) {
            ImageLoaderUtil.loadImage(context, o as String, iv, R.drawable.ic_default, 0)
        }
    }
    //Banner of Tab1
    fun initBanner(context: Context?, banner: Banner, list: ArrayList<LinkedTreeMap<String, Any>>) {
        if (ObjectUtils.isEmpty(list)) return
        val titleList: MutableList<String> = ArrayList()
        val imageList: MutableList<String?> = ArrayList()
        val webUrlList: MutableList<String> = ArrayList()
        for (map in list) {
            titleList.add(JU.s(map, "title"))
            imageList.add(JU.s(map, "imagePath"))
            webUrlList.add(JU.s(map, "url"))
        }
        //设置banner样式
        banner.setBannerStyle(BannerConfig.NUM_INDICATOR_TITLE)
        //设置图片加载器
        banner.setImageLoader(BannerImageLoader())
        //设置图片集合
        banner.setImages(imageList)
        //设置banner动画效果
        banner.setBannerAnimation(Transformer.DepthPage)
        //设置标题集合（当banner样式有显示title时）
        banner.setBannerTitles(titleList)
        //设置自动轮播，默认为true
        banner.isAutoPlay(true)
        //设置轮播时间
        banner.setDelayTime(5000)
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER)
        banner.setOnBannerListener { i: Int ->
            if (i < webUrlList.size && ObjectUtils.isNotEmpty(webUrlList[i])) {
                ActivityUtils.startActivity(Intent(context, WebViewActivity::class.java)
                    .putExtra(WebViewActivity.TYPE, WebViewActivity.TYPE_LOAD_URL)
                    .putExtra("title", titleList[i])
                    .putExtra("url", webUrlList[i]))
            }
        }
        banner.start() //banner设置方法全部调用完毕时最后调用
    }


    fun getEtTrim(et: EditText?): String {
        return et?.text?.toString()?.trim() ?: ""
    }

}