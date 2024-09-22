package com.example.weatherappmvp.bean;

import com.google.gson.annotations.SerializedName;

public class NowWeatherBean {
    @SerializedName("temp")
    private String nowTemp;

    @SerializedName("feelsLike")
    private String bodyTemp;

    @SerializedName("windScale")
    private String windScale;

    public String getNowTemp() {
        return nowTemp;
    }

    public void setNowTemp(String nowTemp) {
        this.nowTemp = nowTemp;
    }

    public String getBodyTemp() {
        return bodyTemp;
    }

    public void setBodyTemp(String bodyTemp) {
        this.bodyTemp = bodyTemp;
    }

    public String getWindScale() {
        return windScale;
    }

    public void setWindScale(String windScale) {
        this.windScale = windScale;
    }

    @Override
    public String toString() {
        return "NowWeatherBean{" +
                "nowTemp='" + nowTemp + '\'' +
                ", bodyTemp='" + bodyTemp + '\'' +
                ", windScale='" + windScale + '\'' +
                '}';
    }
}
