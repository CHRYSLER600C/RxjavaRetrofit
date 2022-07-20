package com.frame.dataclass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by dongxie on 2016/11/21.
 */

public class DataClass implements Serializable {

    private static final long serialVersionUID = 1L;


    @Expose
    @SerializedName("json")
    public Object obj; // 所有数据都用这个接收

}
