package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

//
// Database Update
// gson structure for saving internet get-request "Update time data"
//
public class Update {
    // data container
    @SerializedName("loc")
    public String updateTime;
}
