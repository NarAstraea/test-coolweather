package com.coolweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.coolweather.android.ui.activity.WeatherActivity;

//
//主要对缓存数据进行判断和直接读取
//
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getString("updateTime",null)!= null && prefs.getString("suggestion",null)!= null){
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
