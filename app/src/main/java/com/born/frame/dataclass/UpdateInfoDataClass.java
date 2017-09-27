package com.born.frame.dataclass;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class UpdateInfoDataClass extends DataClass {

    @Expose
    public UpdateInfo updateInfo;

    public static class UpdateInfo implements Serializable {
        private static final long serialVersionUID = 3L;

        @Expose
        public int forceUpdateCode;       // 强制升级的版本
        @Expose
        public int optionalUpdateCode;    // 可选升级版本
        @Expose
        public String updateInfo;         // 更新的提示信息
        @Expose
        public String updateUrl;          // 更新的url
    }
}
