package com.example.weatherappmvp.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class WeatherBean1 implements Serializable {
    @SerializedName("daily")
    private List<DayWeatherBean> daily;


    public List<DayWeatherBean> getList() {
        return daily;
    }

    public void setList(List<DayWeatherBean> daily) {
        this.daily = daily;
    }

    @Override
    public String toString() {
        return daily.get(0).toString();
    }
}
