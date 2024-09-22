package com.example.weatherappmvp;

import android.content.Context;

import com.example.weatherappmvp.bean.WeatherBean1;
import com.example.weatherappmvp.bean.WeatherBean2;

import java.util.List;

// 契约接口，内含m,v,p三层接口方法
public interface MainContract {
    interface IMainModel {
        // 网络请求
        WeatherBean1 requestDayWeather(String name);

        WeatherBean2 requestNowWeather(String name);

        String[] requsetCityId(String name);

        void saveCityList(List<String> cityNameList, Context context);

        List<String> readCityList(Context context);

        String requestSearchCity(String keywords);

    }

    interface IMainView {
        // 顶部三个控件内容
        void setTopData(WeatherBean1 weatherBean1, WeatherBean2 weatherBean2, String naem);

        // viewPager的fragment内容,初始化
        void setViewPager(List<String> cityNameList);


        void updateViewPager(int cityIndex);

        void updateMenuSelection(int position);

        void showAddCityDialogFragment();

        void refreshViewPager();

        void initMenu(List<String> cityNameList);

        void setupNavigationView();

        void initLocation();

        String getLatitudeAndLongitude();
    }

    interface IMainPresenter {
        void handleData(String name);

        // viewPager城市的切换
        void onPageSelected(int position);

        void saveCityList(List<String> cityNameList, Context context);

        List<String> readCityList(Context context);

        void updateMenuSelection(int position);

        void refreshViewPager();

        void initMenu(List<String> cityNameList);

        void setupNavigationView();

        void showAddCityDialogFragment();

        List<String> requestSearchCity(String keyWords);
    }
}
