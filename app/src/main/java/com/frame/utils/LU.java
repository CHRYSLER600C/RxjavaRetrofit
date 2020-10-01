package com.frame.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.frame.R;
import com.frame.activity.ExampleActivity;
import com.frame.activity.KnowledgeHierarchyActivity;
import com.frame.activity.LoginActivity;
import com.frame.activity.NavigationActivity;
import com.frame.activity.ProjectActivity;
import com.frame.activity.WebViewActivity;
import com.frame.activity.WxArticleActivity;
import com.frame.common.CommonData;
import com.frame.dataclass.bean.Template;
import com.frame.httputils.OkHttpUtil;
import com.frame.httputils.OkHttpUtil2;
import com.frame.other.BannerImageLoader;
import com.google.gson.internal.LinkedTreeMap;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;

/**
 * LogicUtil：App业务逻辑处理的工具类
 */
public class LU {

    public static List<Template> getListTab4() {
        List<Template> list = new ArrayList<>();
        list.add(new Template(R.drawable.ic_default, "Example", "", ExampleActivity.class));
        return list;
    }

    public static List<Template> getBlockList() {
        List<Template> list = new ArrayList<>();
        list.add(new Template(R.drawable.wan_icon_1, "知识体系", "", KnowledgeHierarchyActivity.class));
        list.add(new Template(R.drawable.wan_icon_2, "公众号", "", WxArticleActivity.class));
        list.add(new Template(R.drawable.wan_icon_3, "导航", "", NavigationActivity.class));
        list.add(new Template(R.drawable.wan_icon_4, "项目", "", ProjectActivity.class));
        return list;
    }

    /**
     * 退出登录，清空数据
     */
    public static void logout() {
        OkHttpUtil.getInstance().cleanCookie();
        OkHttpUtil2.getInstance().cleanCookie();
    }

    /**
     * 跳转登录界面
     *
     * @param object Activity or Fragment
     */
    public static void gotoLogin(Object object) {
        if (object instanceof Activity) {
            Intent login = new Intent((Activity) object, LoginActivity.class);
            login.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            ((Activity) object).startActivityForResult(login, CommonData.REQUEST_CODE_LOGIN);
        } else if (object instanceof Fragment) {
            Fragment fragment = (Fragment) object;
            Intent login = new Intent(fragment.getActivity(), LoginActivity.class);
            login.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            fragment.startActivityForResult(login, CommonData.REQUEST_CODE_LOGIN);
        }
    }

    //Banner of Tab1
    public static void initBanner(Context context, Banner banner,
                                  ArrayList<LinkedTreeMap<String, Object>> list) {
        if(ObjectUtils.isEmpty(list)) return;

        final List<String> titleList = new ArrayList<>();
        List<String> imageList = new ArrayList<>();
        final List<String> webUrlList = new ArrayList<>();
        for (LinkedTreeMap<String, Object> map : list) {
            titleList.add(JU.s(map, "title"));
            imageList.add(JU.s(map, "imagePath"));
            webUrlList.add(JU.s(map, "url"));
        }
        //设置banner样式
        banner.setBannerStyle(BannerConfig.NUM_INDICATOR_TITLE);
        //设置图片加载器
        banner.setImageLoader(new BannerImageLoader());
        //设置图片集合
        banner.setImages(imageList);
        //设置banner动画效果
        banner.setBannerAnimation(Transformer.DepthPage);
        //设置标题集合（当banner样式有显示title时）
        banner.setBannerTitles(titleList);
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(5000);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);

        banner.setOnBannerListener(i -> {
            if (i < webUrlList.size() && ObjectUtils.isNotEmpty(webUrlList.get(i))) {
                ActivityUtils.startActivity(new Intent(context, WebViewActivity.class)
                        .putExtra(WebViewActivity.TYPE, WebViewActivity.TYPE_LOAD_URL)
                        .putExtra("title", titleList.get(i))
                        .putExtra("url", webUrlList.get(i)));
            }
        });
        banner.start();  //banner设置方法全部调用完毕时最后调用
    }

}
