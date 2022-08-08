package com.frame.httputils

import com.frame.dataclass.DataClass
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * Created by dongxie on 16/3/9.
 *
 *
 * 定义所有的接口参数，非common的可自行定义
 */
interface RequestService {
    /**
     ============================== All GET Method Declaration ==============================
     */
    // 通用GET请求
    @GET
    fun commonGet(@Url url: String): Observable<DataClass>?

    @GET
    fun commonGet(@Url url: String, @QueryMap map: Map<String, Any>?): Observable<DataClass>?

    // 通用GET请求，返回原始数据
    @GET
    fun commonGetRaw(@Url url: String): Observable<String?>?

    @GET
    fun commonGetRaw(@Url url: String, @QueryMap map: Map<String, Any>?): Observable<String?>?

    /**
     ============================== All POST Method Declaration ==============================
     */
    // 通用POST请求
    @POST
    fun commonPost(@Url url: String): Observable<DataClass>?

    @FormUrlEncoded
    @POST
    fun commonPost(@Url url: String, @FieldMap map: Map<String, Any>?): Observable<DataClass>?

    //添加Headers，以json字符串传送
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST
    fun commonPostJson(@Url url: String, @Body strJson: RequestBody?): Observable<DataClass>?

    // 通用POST请求，返回原始数据
    @POST
    fun commonPostRaw(@Url url: String): Observable<String?>?

    @FormUrlEncoded
    @POST
    fun commonPostRaw(@Url url: String, @FieldMap map: Map<String, Any>?): Observable<String?>?

    //添加Headers，以json字符串传送
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST
    fun commonPostJsonRaw(@Url url: String, @Body strJson: RequestBody?): Observable<String?>?

    // 上传单个文件
    @Multipart
    @POST
    fun uploadFile(@Url url: String, @Part file: MultipartBody.Part?): Observable<DataClass>?
}