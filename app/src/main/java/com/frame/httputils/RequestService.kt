package com.frame.httputils;

import com.frame.dataclass.DataClass;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by dongxie on 16/3/9.
 * <p>
 * 定义所有的接口参数，非common的自行定义
 */
public interface RequestService {


    // ============================== All GET Method Declaration ==============================

    // 通用GET请求
    @GET
    Observable<DataClass> commonGet(@Url String url);

    @GET
    Observable<DataClass> commonGet(@Url String url, @QueryMap Map<String, Object> map);

    // 通用GET请求，返回原始数据
    @GET
    Observable<String> commonGetRaw(@Url String url);

    @GET
    Observable<String> commonGetRaw(@Url String url, @QueryMap Map<String, Object> map);

    // ============================== All POST Method Declaration ==============================

    // 通用POST请求
    @POST
    Observable<DataClass> commonPost(@Url String url);

    @FormUrlEncoded
    @POST
    Observable<DataClass> commonPost(@Url String url, @FieldMap Map<String, Object> map);

    //添加Headers，以json字符串传送
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST
    Observable<DataClass> commonPostJson(@Url String url, @Body RequestBody strJson);

    // 通用POST请求，返回原始数据
    @POST
    Observable<String> commonPostRaw(@Url String url);

    @FormUrlEncoded
    @POST
    Observable<String> commonPostRaw(@Url String url, @FieldMap Map<String, Object> map);

    //添加Headers，以json字符串传送
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST
    Observable<String> commonPostJsonRaw(@Url String url, @Body RequestBody strJson);
}
