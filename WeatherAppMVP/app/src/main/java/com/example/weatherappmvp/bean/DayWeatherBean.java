package com.example.weatherappmvp.bean;

import com.google.gson.annotations.SerializedName;

public class DayWeatherBean {
    @SerializedName("fxDate")
    private String fxDate;

    @SerializedName("sunset")
    private String sunset;

    @SerializedName("tempMax")
    private String tempMax;

    @SerializedName("tempMin")
    private String tempMin;

    @SerializedName("textDay")
    private String textDay;

    @SerializedName("windScaleDay")
    private String windSpeedDay;

    @SerializedName("humidity")
    private String humidity;

    @SerializedName("pressure")
    private String pressure;

    @SerializedName("uvIndex")
    private String uvIndex;

    @SerializedName("sunrise")
    private String sunRise;

    public String getFxDate() {
        return fxDate;
    }

    public String getSunset() {
        return sunset;
    }

    public String getTempMax() {
        return tempMax;
    }

    public String getTempMin() {
        return tempMin;
    }

    public String getTextDay() {
        return textDay;
    }

    public String getWindSpeedDay() {
        return windSpeedDay;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getPressure() {
        return pressure;
    }

    public String getUvIndex() {
        return uvIndex;
    }

    public String getSunRise() {
        return sunRise;
    }

    @Override
    public String toString() {
        return "DayWeatherBean{" +
                "fxDate='" + fxDate + '\'' +
                ", sunset='" + sunset + '\'' +
                ", tempMax='" + tempMax + '\'' +
                ", tempMin='" + tempMin + '\'' +
                ", textDay='" + textDay + '\'' +
                ", windSpeedDay='" + windSpeedDay + '\'' +
                ", humidity='" + humidity + '\'' +
                ", pressure='" + pressure + '\'' +
                ", uvIndex='" + uvIndex + '\'' +
                ", sunRise='" + sunRise + '\'' +
                '}';
    }
}
