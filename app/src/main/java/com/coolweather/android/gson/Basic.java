package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

//
// Database Basic
// gson structure for saving internet get-request "Basic data"
//
public class Basic {
    // data container
    @SerializedName("location")
    public String cityName;

    @SerializedName("cid")
    public String weatherId;

}
