package com.born.frame.common;

import android.os.Environment;

public class CommonData {

    public static final boolean ISRELEASE_URL = true; //false; //
    public static final boolean DEBUG = !ISRELEASE_URL; //true; //

    /**
     * 服务器地址配置
     */
    public static final String BASE_URL_FORMAL = "https://www.yiqiyiqi.cn/app/"; //正式环境
    public static final String BASE_URL_TEST = "http://192.168.45.92:28370/app/"; //测试环境
    public static final String SEVER_URL = ISRELEASE_URL ? BASE_URL_FORMAL : BASE_URL_TEST;

    /**
     * SD卡文件存储路径
     **/
    public static final String PACKAGE_NAME = "com.born.frame";
    public static final String BASE_DIR_SD = Environment.getExternalStorageDirectory().getPath()
            + "/Android/data/" + PACKAGE_NAME;
    public static final String IMAMGE_DIR_SD = BASE_DIR_SD + "/cache/image";
    public static final String LOGGER_DIR_SD = BASE_DIR_SD + "/cache/log";
    public static final String APK_DIR_SD = BASE_DIR_SD + "/cache/apk";
    /**
     * 内存文件存储路径
     **/
    public static final String BASE_DIR_RAM = "/data/data/" + PACKAGE_NAME;
    public static final String IMAMGE_DIR_RAM = BASE_DIR_RAM + "/cache/image";
    public static final String APK_DIR_RAM = BASE_DIR_RAM + "/cache/apk";

    /**
     * 照片选择
     */
    public static final int PHOTO_NONE = 0x1600;
    public static final int PHOTO_CAMERA = 0x1601;  // 拍照
    public static final int PHOTO_GALLERY = 0x1602; // 图库
    public static final int PHOTO_RESULT = 0x1603;  // 结果
    public static final String IMAGE_UNSPECIFIED = "image/*";

    /**
     * code值
     */
    public static final String RESULT_SUCCESS = "1"; // 请求成功
    public static final String RESULT_FAILED = "0";    // 请求失败
    public static final String RESULT_UNLOGIN = "-1";// 登录失效

    public static final String AESKEY = "0123456789abcdef"; // SYS_PARAM_APP_PWD_KEY
    public static final String NETWORK_ERROR_MSG = "获取数据异常，请稍后重试";

    public static final int REQUEST_CODE_LOGIN = 1000; // 跳转登录
    public static boolean IS_LOGIN = false;
}
