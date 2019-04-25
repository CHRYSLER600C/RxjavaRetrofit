package com.frame.httputils;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

interface ICookieStore {

    void add(HttpUrl uri, List<Cookie> cookie);

    List<Cookie> get(HttpUrl uri);

    List<Cookie> getCookies();

    boolean remove(HttpUrl uri, Cookie cookie);

    boolean removeAll();


}
