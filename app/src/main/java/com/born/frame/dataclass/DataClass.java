package com.born.frame.dataclass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dongxie on 2016/11/21.
 */

public class DataClass {

    @Expose
    @SerializedName("code")
    public String code; // "1" : SUCCESS, "0" : FAIL

    @Expose
    @SerializedName("message")
    public String message;

}
