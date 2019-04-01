package com.coolweather.android.gson;

//import com.google.gson.annotations.SerializedName;

//import java.util.List;

//
// Database Weather
// gson structure for saving internet get-request "Weather message data"
// this db class is a container for other db classes
//
public class Weather {
    // other database containers
    public String status;
    public Basic basic;
    public Now now;
    public Update update;
//    @SerializedName("daily_forecast")
//    public List<Forecast> forecastList;
}
