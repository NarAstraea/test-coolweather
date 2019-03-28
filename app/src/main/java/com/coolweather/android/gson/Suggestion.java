package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Suggestion {
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
