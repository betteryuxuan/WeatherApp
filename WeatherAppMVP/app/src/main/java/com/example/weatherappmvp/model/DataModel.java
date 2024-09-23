package com.example.weatherappmvp.model;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.weatherappmvp.MainContract;
import com.example.weatherappmvp.bean.WeatherBean1;
import com.example.weatherappmvp.bean.WeatherBean2;
import com.example.weatherappmvp.database.WeatherDatabaseHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DataModel implements MainContract.IMainModel {
    private static final String URL_WEATHER_NOW = "https://api.qweather.com/v7/weather/now?";
    private static final String URL_WEATHER_DAY = "https://api.qweather.com/v7/weather/7d?";
    private static final String URL_CITY_DAY = "https://geoapi.qweather.com/v2/city/lookup?";
    private static final String API_KEY = "8d10337174fe4491894596b13e2155bb";
    private Context context;
    private WeatherDatabaseHelper dbHelper;

    public DataModel(Context context) {
        this.context = context.getApplicationContext();
        this.dbHelper = new WeatherDatabaseHelper(context);
    }

    public String doGet(String urlStr) {
        String responseData = "";
        try {
            Cache cache = new Cache(new File(context.getCacheDir(), "http_cache"), 10 * 1024 * 1024); // 10MB缓存
            OkHttpClient client = new OkHttpClient.Builder()
                    .cache(cache)
                    .build();
            Request request = new Request.Builder()
                    .url(urlStr)
                    .cacheControl(new CacheControl.Builder()
                            .maxStale(1, TimeUnit.HOURS)
                            .build())
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                responseData = response.body().string();
            } else {
                response = client.newCall(request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build()).execute();
                if (response.isSuccessful()) {
                    responseData = response.body().string();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseData;
    }

    @Override
    public String requestDayWeather(String name) {
        if (!isNetworkAvailable()) {
            String dayWeather = getWeatherFromDatabase(name, "dayWeather");
            if (dayWeather != null) {
                Log.d("LiteTag", "读取到了" + name + "本地7天天气数据");
                return dayWeather;
            } else {
                if (name.equals("")) {
                    Log.e("LiteTag", name + "null");
                }
                Log.e("LiteTag", "没有找到" + name + "本地7天天气数据");
                return "";
            }
        }

        String[] cityId = requsetCityId(name);
        String urlDayStr = URL_WEATHER_DAY + "location=" + cityId[0] + "&key=" + API_KEY;
        String dayWeather = doGet(urlDayStr);

        return dayWeather;
    }

    @Override
    public String requestNowWeather(String name) {
        if (!isNetworkAvailable()) {
            String nowWeather = getWeatherFromDatabase(name, "nowWeather");
            if (nowWeather != null) {
                Log.d("LiteTag", "读取到了" + name + "本地实时天气数据");
                return nowWeather;
            } else {
                Log.e("LiteTag", "没有找到" + name + "本地实时天气数据");
                return "";
            }
        }

        String[] cityId = requsetCityId(name);
        String urlNowStr = URL_WEATHER_NOW + "location=" + cityId[0] + "&key=" + API_KEY;
        String nowWeather = doGet(urlNowStr);

        Log.d("WEATHERTag", name + " 实时天气数据：" + nowWeather);
        return nowWeather;
    }


    @Override
    public String[] requsetCityId(String name) {
        String url = URL_CITY_DAY + "location=" + name + "&key=" + API_KEY + "&range=cn";
        String response = doGet(url);
        Log.d("WEATHERTag", "idUrlResponse: " + response);

        try {
            JSONObject jsonResponse = new JSONObject(response);
            if (jsonResponse.getString("code").equals("200")) {
                JSONArray locationArray = jsonResponse.getJSONArray("location");
                if (locationArray.length() > 0) {
                    JSONObject firstLocation = locationArray.getJSONObject(0);
                    String locationId = firstLocation.getString("id");
                    String cityNameResult = firstLocation.getString("name");

                    return new String[]{locationId, cityNameResult};
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String requestSearchCity(String keywords) {
        String url = URL_CITY_DAY + "location=" + keywords + "&key=" + API_KEY + "&range=cn";
        String response = doGet(url);
        Log.d("CityFragmentTag", "searchCityResponse: " + response);
        return response;
    }

    public void saveResponseData(String cityName, String responseData, String weatherType) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(WeatherDatabaseHelper.COLUMN_CITY_NAME, cityName);
        values.put(WeatherDatabaseHelper.COLUMN_RESPONSE_DATA, responseData);
        values.put(WeatherDatabaseHelper.COLUMN_WEATHER_TYPE, weatherType);
        Log.d("LiteTag", "保存: " + cityName + " , " + responseData);

        db.insertWithOnConflict(WeatherDatabaseHelper.TABLE_WEATHER, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }


    public void saveCityList(List<String> cityNameList, Context context) {
        cityNameList.remove(0);

        Set<String> uniqueCityNameSet = new LinkedHashSet<>(cityNameList);
        List<String> uniqueCityNameList = new ArrayList<>(uniqueCityNameSet);

        SharedPreferences sharedPreferences = context.getSharedPreferences("WeatherAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String cityListJson = gson.toJson(uniqueCityNameList);

        Log.d("MenuTag2", "保存: " + cityListJson);

        editor.putString("city_list", cityListJson);
        editor.apply();
    }


    public List<String> readCityList(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("WeatherAppPrefs", MODE_PRIVATE);
        String cityListJson = sharedPreferences.getString("city_list", null);

        if (cityListJson == null || cityListJson.equals("[]")) {
            Log.d("MenuTag2", "城市列表为空");
            return null;
        }

        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {
        }.getType();
        List<String> cityNameList = gson.fromJson(cityListJson, type);

        Set<String> uniqueCityNameSet = new LinkedHashSet<>(cityNameList);
        List<String> uniqueCityNameList = new ArrayList<>(uniqueCityNameSet);

        Log.d("MenuTag2", "读取： " + uniqueCityNameList);

        return uniqueCityNameList;
    }


    public String getWeatherFromDatabase(String cityName, String weatherType) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String weatherData = null;

        Cursor cursor = db.query(WeatherDatabaseHelper.TABLE_WEATHER,
                new String[]{WeatherDatabaseHelper.COLUMN_RESPONSE_DATA},
                WeatherDatabaseHelper.COLUMN_CITY_NAME + "=? AND " + WeatherDatabaseHelper.COLUMN_WEATHER_TYPE + "=?",
                new String[]{cityName, weatherType},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(WeatherDatabaseHelper.COLUMN_RESPONSE_DATA);

            if (columnIndex != -1) {
                weatherData = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        db.close();
        return weatherData;
    }


    @Override
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (!(activeNetworkInfo != null && activeNetworkInfo.isConnected())) {
            Log.d("LiteTag", "网断了");
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
