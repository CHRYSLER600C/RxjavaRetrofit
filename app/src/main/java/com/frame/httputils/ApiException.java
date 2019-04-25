package com.frame.httputils;

/**
 * Created by dongxie on 16/3/10.
 */
public class ApiException extends RuntimeException {

    public static final int UNKNOWN_ERROR = 100;
    public static final int USER_NOT_EXIST = 101;
    public static final int WRONG_PASSWORD = 102;

    public ApiException(int resultCode) {
        this(getApiExceptionMessage(resultCode));
    }

    public ApiException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Code -> Message
     * @param code
     * @return message
     */
    private static String getApiExceptionMessage(int code){
        String message = "";
        switch (code) {
            case USER_NOT_EXIST:
                message = "该用户不存在";
                break;
            case WRONG_PASSWORD:
                message = "密码错误";
                break;
            default:
                message = "未知错误";

        }
        return message;
    }
}

