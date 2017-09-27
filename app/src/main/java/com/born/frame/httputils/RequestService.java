package com.born.frame.httputils;

import com.born.frame.dataclass.DataClass;
import com.born.frame.dataclass.HomepgAdvDataClass;
import com.born.frame.dataclass.UpdateInfoDataClass;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by dongxie on 16/3/9.
 *
 * 定义所有的接口参数
 */
public interface RequestService {


    // ============================== All GET Method Declaration ==============================

    // 返回类型为DataClass的通用GET请求，methodName传方法名
    @GET("{methodName}.htm")
    Observable<DataClass> commonGet(@Path("methodName") String methodName);
    @GET("{methodName}.htm")
    Observable<DataClass> commonGet(@Path("methodName") String methodName, @QueryMap Map<String, Object> map);



    @GET("appUpdateInfo.htm") // 软件更新
    Observable<UpdateInfoDataClass> getUpdateInfo(@QueryMap Map<String, Object> map);

    @GET("getIndexImg.htm") // 首页Banner图
    Observable<HomepgAdvDataClass> getBannerImages();




    // ============================== All POST Method Declaration ==============================

    // 返回类型为DataClass的通用POST请求，methodName传方法名
    @FormUrlEncoded
    @POST("{methodName}.htm")
    Observable<DataClass> commonPost(@Path("methodName") String methodName);
    @FormUrlEncoded
    @POST("{methodName}.htm")
    Observable<DataClass> commonPost(@Path("methodName") String methodName, @FieldMap Map<String, Object> map);

}
