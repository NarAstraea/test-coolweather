package com.coolweather.android.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

//
// 利用okhttp开源库进行http请求
//
public class HttpUtil {
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
