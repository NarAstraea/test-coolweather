package com.coolweather.android.ui.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.ExDrawerLayout;
import com.coolweather.android.R;
import com.coolweather.android.gson.AQI;
import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.ForecastWeather;
import com.coolweather.android.gson.Suggestion;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

//
// Weather Message Page Activity
// edited
//
public class WeatherActivity extends AppCompatActivity {

    // layout and db objects and so on
    public ExDrawerLayout drawerLayout;
    private Button menuButton;
    public SwipeRefreshLayout swipeRefreshLayout;
    private String mWeatherId;
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private Weather weather;
    private AQI aqi;
    private Suggestion suggestion;
    private List<String> responseTexts = new ArrayList<>(3);
    private ImageView bingPicImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //沉浸式状态栏
        if(Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            |View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        weatherLayout = (ScrollView)findViewById(R.id.weather_layout);
        titleCity = (TextView)findViewById(R.id.title_city);
        titleUpdateTime = (TextView)findViewById(R.id.title_update_time);
        degreeText = (TextView)findViewById(R.id.temperature_num);
        weatherInfoText = (TextView)findViewById(R.id.weather_text);
        forecastLayout = (LinearLayout)findViewById(R.id.forecast_layout);
        aqiText = (TextView)findViewById(R.id.aqi_aqi_text);
        pm25Text = (TextView)findViewById(R.id.aqi_pm_text);
        comfortText = (TextView)findViewById(R.id.suggestion_comfortable);
        carWashText = (TextView)findViewById(R.id.suggestion_washcars);
        sportText = (TextView)findViewById(R.id.suggestion_sports);
        bingPicImg = (ImageView)findViewById(R.id.bing_pic_img);
        drawerLayout = (ExDrawerLayout)findViewById(R.id.drawer_layout);
        menuButton = (Button)findViewById(R.id.change_menu_btn);

        //刷新当前城市天气
        //update city weather currently
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        //保存当前页面元素内容至缓存用以下次直接载入无需联网
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String updateString = prefs.getString("updateTime",null);
        String weatherString = prefs.getString("weather",null);
        String aqiString = prefs.getString("aqi",null);
        String suggestionString = prefs.getString("suggestion",null);
        if(updateString != null && weatherString != null && aqiString != null && suggestionString != null){
            Weather weather = Utility.handleWeatherResponse(weatherString);
            AQI aqi = Utility.handleAQIWeatherResponse(aqiString);
            Suggestion suggestion = Utility.handleSugWeatherResponse(suggestionString);
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather,aqi,suggestion);
        }else{
            mWeatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
            weatherLayout.setVisibility(View.VISIBLE);
        }

        //获取BING背景图
        String bingPic = prefs.getString("bing_pic",null);
        if(bingPic != null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else{
            loadBingPic();
        }

        //刷新城市天气信息
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                mWeatherId = prefs.getString("weatherId",null);
                requestWeather(mWeatherId);
            }
        });

        //切换城市菜单
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    //
    // 请求天气数据的方法主体
    //
    public void requestWeather(final String weatherId){
        //分别获取基本天气数据（温度 天气状况等）、AQI数据和生活建议数据
        final String weatherUrl = "https://free-api.heweather.net/s6/weather/now?location=" + weatherId + "&key=e6cd302b539c4482acff7b2b85994c73";
        final String weatherUrlForAQI = "https://free-api.heweather.net/s6/air/now?location=" + weatherId + "&key=e6cd302b539c4482acff7b2b85994c73";
        final String weatherUrlForSug = "https://free-api.heweather.net/s6/weather/lifestyle?location=" + weatherId + "&key=e6cd302b539c4482acff7b2b85994c73";
        Callbacks callbacks = new Callbacks();
        final Callback callback = callbacks;

        HttpUtil.sendOkHttpRequest(weatherUrl,callback);
        for(;responseTexts.size()<1;){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        HttpUtil.sendOkHttpRequest(weatherUrlForAQI,callback);
        for(;responseTexts.size()<2;){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        HttpUtil.sendOkHttpRequest(weatherUrlForSug,callback);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(;responseTexts.size()<3;){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        //将获取到的数据存入预先准备的String容器中
        weather = Utility.handleWeatherResponse(responseTexts.get(0));
        aqi = Utility.handleAQIWeatherResponse(responseTexts.get(1));
        suggestion = Utility.handleSugWeatherResponse(responseTexts.get(2));
        if(weather != null && aqi != null && suggestion != null && "ok".equals(weather.status)&&"ok".equals(aqi.status)&&"ok".equals(suggestion.status)){
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
            editor.putString("weather",responseTexts.get(0));
            editor.putString("aqi",responseTexts.get(1));
            editor.putString("suggestion",responseTexts.get(2));
            editor.putString("updateTime",suggestion.update.updateTime);
            editor.putString("weatherId",weather.basic.weatherId);
            editor.apply();
            //调用方法将数据刷新到页面上
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showWeatherInfo(weather,aqi,suggestion);
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(WeatherActivity.this,"request msg failed[2]",Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
        responseTexts.clear();
        loadBingPic();//每次刷新页面同时刷新背景
    }

    //刷新页面数据
    private void showWeatherInfo(Weather weather,AQI aqi,Suggestion suggestion){
        String cityName = weather.basic.cityName;
        String updateTime = weather.update.updateTime;
        String degree = weather.now.temperature;
        String weatherInfo = weather.now.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        aqiText.setText(aqi.city.aqi);
        pm25Text.setText(aqi.city.pm25);
        String echoText = "舒适度：" +suggestion.lifestyle.get(0).assess+"  "+ suggestion.lifestyle.get(0).message;
        comfortText.setText(echoText);
        echoText = "洗车：" + suggestion.lifestyle.get(6).assess +"  "+ suggestion.lifestyle.get(6).message;
        carWashText.setText(echoText);
        echoText = "运动：" + suggestion.lifestyle.get(3).assess +"  "+ suggestion.lifestyle.get(3).message;
        sportText.setText(echoText);
    }

    //封装好的callback接口实现
    private class Callbacks implements Callback{
        @Override
        public void onFailure(Call call, IOException e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(WeatherActivity.this,"request msg failed[0]",Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            final String responseText = response.body().string();
            responseTexts.add(responseText);
        }
    }

    //获取bing每日一图 并刷新到页面中
    private void loadBingPic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }
}
