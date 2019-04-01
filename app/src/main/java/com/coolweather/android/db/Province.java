package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

//
// Database Province
//
public class Province extends DataSupport {
    // inner variables
    private int id;
    private String provinceName;
    private int provinceCode;

    // getter and setter
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public String getProvinceName(){
        return provinceName;
    }
    public void setProvinceName(String provinceName){
        this.provinceName = provinceName;
    }
    public int getProvinceCode(){
        return provinceCode;
    }
    public void setProvinceCode(int provinceCode){
        this.provinceCode = provinceCode;
    }
}
