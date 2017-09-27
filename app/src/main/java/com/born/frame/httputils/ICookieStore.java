package com.born.frame.httputils;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

public interface ICookieStore
{

    public void add(HttpUrl uri, List<Cookie> cookie);

    public List<Cookie> get(HttpUrl uri);

    public List<Cookie> getCookies();

    public boolean remove(HttpUrl uri, Cookie cookie);

    public boolean removeAll();


}
