package com.example.weatherappmvp.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.weatherappmvp.MainContract;
import com.example.weatherappmvp.bean.WeatherBean1;
import com.example.weatherappmvp.bean.WeatherBean2;
import com.example.weatherappmvp.model.DataModel;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainPresenter implements MainContract.IMainPresenter {
    private MainContract.IMainModel mModel;
    private MainContract.IMainView mView;
    private Handler mHandler;
    private List<String> cityNameList;
    private String dayWeather;
    private String nowWeather;

    public MainPresenter(MainContract.IMainView view, Context context) {
        this.mView = view;
        mModel = new DataModel(context);
        mHandler = new Handler(Looper.getMainLooper());
        cityNameList = new ArrayList<>();
    }

    @Override
    public void handleData(String cityName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("LocationTag2", cityName);
                Boolean flag = false;
                if (null != cityName && cityName.contains(",")) {
                    flag = true;
                }

                // 在后台线程中执行网络请求
                // 有网
                String locationId = "";
                String cityNameResult = "";
                if (isNetworkAvailable()) {
                    String[] data = mModel.requsetCityId(cityName);
                    locationId = data[0];
                    cityNameResult = data[1];
                    dayWeather = mModel.requestDayWeather(locationId);
                    nowWeather = mModel.requestNowWeather(locationId);
                    if (!flag) {
                        mModel.saveResponseData(cityNameResult, dayWeather, "dayWeather");
                        mModel.saveResponseData(cityNameResult, nowWeather, "nowWeather");
                    } else {
                        //定位城市
                        cityNameResult = "当前定位城市:" + cityNameResult;
                        mModel.saveResponseData(cityName, dayWeather, "dayWeather");
                        mModel.saveResponseData(cityName, nowWeather, "nowWeather");
                    }
                } else {
                    dayWeather = mModel.requestDayWeather(cityName);
                    nowWeather = mModel.requestNowWeather(cityName);
                    if (flag) {
                        cityNameResult = "当前定位城市:" + cityName;
                    } else {
                        cityNameResult = cityName;
                    }
                }

                Gson gson = new Gson();
                WeatherBean1 weatherBean1 = gson.fromJson(dayWeather, WeatherBean1.class);
                WeatherBean2 weatherBean2 = gson.fromJson(nowWeather, WeatherBean2.class);

                String finalCityNameResult = cityNameResult;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (weatherBean1 != null && weatherBean2 != null) {
                            mView.setTopData(weatherBean1, weatherBean2, finalCityNameResult);
                        } else {
                            Log.e("WEATHERTag", "weatherBean1 or weatherBean2 is null");
                        }
                    }
                });
            }
        }).start();
    }

    public void viewPagerInit(List<String> cityNameList, Context context) {
        this.cityNameList = cityNameList;
        mView.setViewPager(cityNameList);
    }

    public void onPageSelected(int position) {
        String cityName = cityNameList.get(position);
        Log.d("MyTag", position + cityName + "---" + cityNameList.size());
        Log.d("ViewPagerTag", "2" + cityNameList.toString());
        if (position == 0) {
            // 防止传入了定位城市名导致未识别为定位城市
            if (mModel.isNetworkAvailable()) {
                cityName = mView.getLatitudeAndLongitude();
            }
        }
        handleData(cityName);

    }


    public void saveCityList(List<String> cityList, Context context) {
        mModel.saveCityList(cityList, context);
    }


    public List<String> readCityList(Context context) {
        return mModel.readCityList(context);
    }

    public void setupNavigationView() {
        mView.setupNavigationView();
    }

    @Override
    public void showAddCityDialogFragment() {
        mView.showAddCityDialogFragment();
    }

    public void onNavigationItemSelected(String title) {
        if (title.equals("添加更多城市")) {
            mView.showAddCityDialogFragment();
        } else if (title.equals("当前城市")) {
            mView.updateViewPager(0);
//            if (isNetworkAvailable()) {
//                mView.initLocation();
//            } else {
//                handleData("108.90,34.15");
//            }
        } else {
//            handleData(title);
            // setCurrentItem切换貌似会自己调用onPagerSelected;
            if (cityNameList != null && cityNameList.contains(title)) {
                int cityIndex = cityNameList.indexOf(title);
                mView.updateViewPager(cityIndex);
            }
        }
    }

    @Override
    public void updateMenuSelection(int position) {
        mView.updateMenuSelection(position);
    }

    @Override
    public void refreshViewPager() {
        mView.refreshViewPager();
    }

    @Override
    public void initMenu(List<String> cityNameList) {
        mView.initMenu(cityNameList);
    }

    public List<String> requestSearchCity(String keyWords) {
        String reponse = mModel.requestSearchCity(keyWords);
        List<String> ret = parseCityData(reponse);
        return ret;
    }

    public static List<String> parseCityData(String jsonData) {
        List<String> resultList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            JSONArray locationArray = jsonObject.getJSONArray("location");

            for (int i = 0; i < locationArray.length(); i++) {
                JSONObject locationObject = locationArray.getJSONObject(i);

                String name = locationObject.getString("name");
                String adm1 = locationObject.getString("adm1");
                String adm2 = locationObject.getString("adm2");

                String combinedString = name + "-" + adm2 + "市-" + adm1;
                resultList.add(combinedString);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public boolean isNetworkAvailable() {
        return mModel.isNetworkAvailable();
    }
}
