package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

//
// Database Now
// gson structure for saving internet get-request "Now weather data"
//
public class Now {
    // data container
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond_txt")
    public String info;
}
