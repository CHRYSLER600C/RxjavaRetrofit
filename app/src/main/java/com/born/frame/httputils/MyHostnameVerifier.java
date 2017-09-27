package com.born.frame.httputils;

import javax.net.ssl.SSLSession;

/**
 * Created by min on 2016/12/15.
 */

public class MyHostnameVerifier implements javax.net.ssl.HostnameVerifier {

    @Override
    public boolean verify(String hostname, SSLSession session) {
        return true;
    }
}
