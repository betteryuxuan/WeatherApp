package com.example.weatherappmvp.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WeatherBean2 implements Serializable {
    @SerializedName("now")
    public NowWeatherBean nowWeatherBean;

    public NowWeatherBean getNowWeatherBean() {
        return nowWeatherBean;
    }

    public void setNowWeatherBean(NowWeatherBean nowWeatherBean) {
        this.nowWeatherBean = nowWeatherBean;
    }

    @Override
    public String toString() {
        return "WeatherBean2{" +
                "nowWeatherBean=" + nowWeatherBean +
                '}';
    }
}
