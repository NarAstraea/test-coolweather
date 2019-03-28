package com.coolweather.android.ui.activity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        weatherString = null;
        if(weatherString != null){
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        }else{
            String weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
            weatherLayout.setVisibility(View.VISIBLE);
        }
    }

    public void requestWeather(final String weatherId){
        String weatherUrl = "https://free-api.heweather.net/s6/weather/now?location=" + weatherId + "&key=e6cd302b539c4482acff7b2b85994c73";
        String weatherUrlForAQI = "https://free-api.heweather.net/s6/air/now?location=" + weatherId + "&key=e6cd302b539c4482acff7b2b85994c73";
        String weatherUrlForSug = "https://free-api.heweather.net/s6/weather/lifestyle?location=" + weatherId + "&key=e6cd302b539c4482acff7b2b85994c73";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"request msg failed[0]",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("updateTime",weather.update.updateTime);
                            editor.apply();
                            showWeatherInfo(weather);
                        }else{
                            Toast.makeText(WeatherActivity.this,"request msg failed[1]",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        HttpUtil.sendOkHttpRequest(weatherUrlForAQI, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"request msg failed[0]",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final AQI aqi = Utility.handleAQIWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(aqi != null && "ok".equals(aqi.status)){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("updateTime",aqi.update.loc);
                            editor.apply();
                            showAQIWeatherInfo(aqi);
                        }else{
                            Toast.makeText(WeatherActivity.this,"request msg failed[1]",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        HttpUtil.sendOkHttpRequest(weatherUrlForSug, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"request msg failed[0]",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Suggestion suggestion = Utility.handleSugWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(suggestion != null && "ok".equals(suggestion.status)){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("updateTime",suggestion.update.updateTime);
                            editor.apply();
                            showSugWeatherInfo(suggestion);
                        }else{
                            Toast.makeText(WeatherActivity.this,"request msg failed[1]",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    private void showWeatherInfo(Weather weather){
        String cityName = weather.basic.cityName;
        String updateTime = weather.update.updateTime;
        String degree = weather.now.temperature;
        String weatherInfo = weather.now.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);

    }

    private void showAQIWeatherInfo(AQI aqi){
        aqiText.setText(aqi.city.aqi);
        pm25Text.setText(aqi.city.pm25);
    }
    private void showSugWeatherInfo(Suggestion suggestion){
        String echoText = "舒适度：" +suggestion.lifestyle.get(0).assess+"  "+ suggestion.lifestyle.get(0).message;
        comfortText.setText(echoText);
        echoText = "洗车：" + suggestion.lifestyle.get(6).assess +"  "+ suggestion.lifestyle.get(6).message;
        carWashText.setText(echoText);
        echoText = "运动：" + suggestion.lifestyle.get(3).assess +"  "+ suggestion.lifestyle.get(3).message;
        sportText.setText(echoText);
    }
}
