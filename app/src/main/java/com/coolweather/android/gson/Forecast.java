package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

//
// Database Forecast
// !!not using!!
// for old version server requests
//
public class Forecast {
    //data container
    @SerializedName("date")
    public String updateTimeForForecast;

    @SerializedName("cond_txt_d")
    public String weather;

    @SerializedName("tmp_max")
    public String tempMax;

    @SerializedName("tmp_min")
    public String tempMin;
}
