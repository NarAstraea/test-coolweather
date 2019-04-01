package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

//
// Database AQI
// gson structure for saving internet get-request "AQI data"
//
public class AQI {
    // data containers
    public Basic basic;
    public String status;
    @SerializedName("air_now_city")
    public AQICity city;
    public class AQICity{
        public String aqi;
        public String pm25;
    }
    @SerializedName("update")
    public Update update;
    public class Update{
        public String loc;
    }
}
