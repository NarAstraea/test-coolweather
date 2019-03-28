package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

public class AQI {
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
