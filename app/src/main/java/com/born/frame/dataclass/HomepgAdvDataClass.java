package com.born.frame.dataclass;

import com.google.gson.annotations.Expose;

import java.util.List;

public class HomepgAdvDataClass extends DataClass {

    @Expose
    public List<HomepgAdvInfo> imgInfo;// 首页广告位


    public static class HomepgAdvInfo {
        @Expose
        public String linkUrl;
        @Expose
        public String imgUrl;
    }
}