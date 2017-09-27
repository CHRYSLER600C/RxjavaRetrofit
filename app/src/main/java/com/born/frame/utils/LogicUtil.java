package com.born.frame.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.born.frame.activity.BaseActivity;
import com.born.frame.activity.LoginActivity;
import com.born.frame.common.CommonData;
import com.born.frame.dataclass.DataClass;

import static com.born.frame.utils.JudgeUtil.isNotEmpty;

/**
 * App相关逻辑处理的工具类
 */
public class LogicUtil {

    /**
     * 退出登录，清空数据
     */
    public static void logout() {
//        OkHttpUtil.getInstance().cleanCookie();
    }

    /**
     * 跳转登录界面，当是activity时，就用activity的跳转来跳转，
     * 那样的话返回的时候就会调用activity中的onActivityResult方法。
     *
     * @param object
     * @param isSuccess
     * @param responseObj
     * @return
     */
    public static <T> boolean handleResponse(Object object, boolean isSuccess, T responseObj) {
        boolean needContinue = false;
        if (isSuccess) {
            DataClass response = (DataClass) responseObj;
            if (CommonData.RESULT_UNLOGIN.equals(response.code)) {
                loginIntent(object);
            } else if (CommonData.RESULT_FAILED.equals(response.code)) {
                showToast(object, responseMsg(response));
            } else {
                needContinue = true;
            }
        } else {
            showToast(object, CommonData.NETWORK_ERROR_MSG);
        }
        return needContinue;
    }

    /**
     * 获取网络请求返回的msg
     *
     * @param data
     * @return
     */
    public static String responseMsg(DataClass data) {
        String result = CommonData.NETWORK_ERROR_MSG;
        if (isNotEmpty(data) && isNotEmpty(data.message)) {
            result = data.message;
        }
        return result;
    }

    /**
     * 跳转登录界面
     *
     * @param object Activity or Fragment
     */
    public static void loginIntent(Object object) {
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

    private static void showToast(Object object, String msg) {
        if (object instanceof Activity) {
            ((BaseActivity) object).showToast(msg);
        } else if (object instanceof Fragment) {
            Fragment fragment = (Fragment) object;
            ((BaseActivity) fragment.getActivity()).showToast(msg);
        }
    }
}
