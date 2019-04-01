package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

//
// Database City
//
public class City extends DataSupport {
    //inner variables
    private int id;
    private String cityName;
    private int cityCode;
    private int provinceId;

    // getter and setter
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public String getCityName(){
        return cityName;
    }
    public void setCityName(String cityName){
        this.cityName = cityName;
    }
    public int getcityCode(){
        return cityCode;
    }
    public void setCityCode(int cityCode){
        this.cityCode = cityCode;
    }
    public int getprovinceId(){
        return provinceId;
    }
    public void setProvinceId(int provinceId){
        this.provinceId = provinceId;
    }
}
