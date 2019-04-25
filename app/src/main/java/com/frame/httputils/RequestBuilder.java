package com.frame.httputils;

import com.frame.common.CommonData;

import java.util.HashMap;
import java.util.Iterator;

public class RequestBuilder {

    /**
     * 构造请求的网址
     *
     * @param requestObject 请求参数
     * @return 构造完成的Url
     */
    public static String build(RequestObject requestObject, String url) {
        StringBuffer sb = new StringBuffer();

        if (requestObject.map != null) {
            Iterator<String> it = requestObject.map.keySet().iterator();
            while (it.hasNext()) {
                sb.append("&");
                String key = it.next();
                String value = "" + requestObject.map.get(key);
                sb.append(key);
                sb.append("=");
                sb.append(value);
            }
        }

        String params = sb.toString();
        if (null != params && params.startsWith("&")) {
            params = params.substring(1);
        }
        return url + requestObject.method + "?" + params;
    }

    public static String build(RequestObject requestObject) {
        return build(requestObject, CommonData.SEVER_URL);
    }

    public static class RequestObject {
        public String method;
        public HashMap<String, Object> map = new HashMap<>();
    }
}