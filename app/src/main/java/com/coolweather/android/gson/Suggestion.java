package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

//
// Database Suggestion
// gson structure for saving internet get-request "life suggestion data"
//
public class Suggestion {
    //data container
    public Basic basic;
    public List<LifeStyle> lifestyle;
    public String status;
    public Update update;
    public class LifeStyle{
        @SerializedName("txt")
        public String message;
        @SerializedName("brf")
        public String assess;
    }
}
