package com.frame.common;

import com.frame.BuildConfig;
import com.frame.application.App;

public class CommonData {

    private static final boolean IS_RELEASE = !BuildConfig.IS_DEBUG; // false; //true; //
    public static final boolean DEBUG = !IS_RELEASE; //true; //

    /**
     * 服务器地址配置
     */
    private static final String BASE_URL_RELEASE = "http://wanandroid.com/"; //正式环境
    private static final String BASE_URL_TEST = "http://wanandroid.com/"; //测试环境
    public static final String SEVER_URL = IS_RELEASE ? BASE_URL_RELEASE : BASE_URL_TEST;

    /**
     * SD卡文件存储路径
     * getExternalCacheDir():/storage/sdcard/Android/data/应用包名/cache
     **/
    public static final String BASE_DIR_SD = App.getInstance().getExternalCacheDir() != null
            ? App.getInstance().getExternalCacheDir().getPath()
            : App.getInstance().getCacheDir().getPath();
    public static final String IMAGE_DIR_SD = BASE_DIR_SD + "/image/";
    public static final String FILE_DIR_SD = BASE_DIR_SD + "/file/";


    public static final int PHOTO_CAMERA = 0x1601;  // 拍照
    public static final int PHOTO_GALLERY = 0x1602; // 图库
    public static final int PHOTO_CROP = 0x1603;    // 图片裁剪
    public static final String IMAGE_UNSPECIFIED = "image/*";

    public static final int RESULT_SUCCESS = 0;    // 请求成功
    public static final int RESULT_UNLOGIN = -1001;// 登录失效
    public static final String CODE = "errorCode";
    public static final String MESSAGE = "errorMsg";
    public static final String NETWORK_ERROR_MSG = "获取数据异常，请稍后重试";

    public static final int REQUEST_CODE_LOGIN = 1000; // 跳转登录
    public static boolean IS_LOGIN = false;
    public static final String DB_NAME = "wan_android.db";  //数据库名

    /**
     * Intent params
     */
    public static final String PARAM1 = "param1";
    public static final String PARAM2 = "param2";

    /**
     * Other
     */
    public static final String TAG = "TEST";    // 测试用
}
