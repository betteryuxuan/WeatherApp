package com.example.weatherappmvp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.weatherappmvp.MainContract;
import com.example.weatherappmvp.R;
import com.example.weatherappmvp.adapter.FutureWeatherAdapter;
import com.example.weatherappmvp.bean.DayWeatherBean;
import com.example.weatherappmvp.bean.NowWeatherBean;
import com.example.weatherappmvp.bean.WeatherBean1;
import com.example.weatherappmvp.bean.WeatherBean2;
import com.example.weatherappmvp.databinding.FragmentBottomBinding;
import com.example.weatherappmvp.model.DataModel;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BottomFragment extends Fragment {

    private String cityName;
    private FragmentBottomBinding binding;
    private FutureWeatherAdapter futureWeatherAdapter;
    private MainContract.IMainModel mModel;
    private WeatherBean1 weatherBean1;
    private WeatherBean2 weatherBean2;
    private Handler mHandler;
    private DayWeatherBean dayWeather;
    private NowWeatherBean nowWeatherBean;
    private String dayWeatherJson;
    private String nowWeatherJson;

    public static BottomFragment newInstance(String cityName) {
        BottomFragment fragment = new BottomFragment();
        fragment.cityName = cityName;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new DataModel(requireContext());
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBottomBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        // 初始化 RecyclerView 和 Adapter
        futureWeatherAdapter = new FutureWeatherAdapter(getContext(), new ArrayList<>());
        binding.rvFutureWeather.setAdapter(futureWeatherAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.rvFutureWeather.setLayoutManager(layoutManager);

        // 滑动冲突
        binding.rvFutureWeather.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (swipeRefreshLayout != null && recyclerView != null && recyclerView.getChildCount() > 0) {
                    View firstChild = recyclerView.getChildAt(0);
                    int firstChildPosition = (firstChild == null) ? 0 : firstChild.getTop();
                    swipeRefreshLayout.setEnabled(firstChildPosition >= 0);
                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 在后台线程中执行网络请求
                Log.d("AAATag", "2" + cityName);
                String locationId = "";
                String cityNameResult = "";
                if (mModel.isNetworkAvailable()) {
                    String[] data = mModel.requsetCityId(cityName);
                    locationId = data[0];
                    cityNameResult = data[1];
                    dayWeatherJson = mModel.requestDayWeather(locationId);
                    nowWeatherJson = mModel.requestNowWeather(locationId);
//                    mModel.saveResponseData(cityName, dayWeatherJson, "dayWeather");
//                    mModel.saveResponseData(cityName, nowWeatherJson, "nowWeather");
                } else {
                    dayWeatherJson = mModel.requestDayWeather(cityName);
                    nowWeatherJson = mModel.requestNowWeather(cityName);
                }

                Gson gson = new Gson();
                weatherBean1 = gson.fromJson(dayWeatherJson, WeatherBean1.class);
                weatherBean2 = gson.fromJson(nowWeatherJson, WeatherBean2.class);
                Log.d("FragmentTag", weatherBean1.toString() + weatherBean2.toString());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateFragmentUI(weatherBean1, weatherBean2);
                    }
                });
            }
        }).start();

        // 点击事件，显示BottomSheet
        binding.cardHumidity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BottomSheetTag", dayWeather.getHumidity());
                WeatherBottomSheet bottomSheet = new WeatherBottomSheet(dayWeather.getHumidity(), 1, weatherBean1);
                bottomSheet.show(getParentFragmentManager(), "WeatherBottomSheet");
            }
        });

        binding.cardSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BottomSheetTag", dayWeather.getWindSpeedDay());
                WeatherBottomSheet bottomSheet = new WeatherBottomSheet(dayWeather.getWindSpeedDay().split("-")[1], 2, weatherBean1);
                bottomSheet.show(getParentFragmentManager(), "WeatherBottomSheet");
            }
        });
        binding.cardTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BottomSheetTag", nowWeatherBean.getBodyTemp());
                WeatherBottomSheet bottomSheet = new WeatherBottomSheet(nowWeatherBean.getBodyTemp(), 3, weatherBean1);
                bottomSheet.show(getParentFragmentManager(), "WeatherBottomSheet");
            }
        });
        binding.cardUv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BottomSheetTag", dayWeather.getUvIndex());
                WeatherBottomSheet bottomSheet = new WeatherBottomSheet(dayWeather.getUvIndex(), 4, weatherBean1);
                bottomSheet.show(getParentFragmentManager(), "WeatherBottomSheet");
            }
        });
        binding.cardSun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int flag = chooseSun();
                if (flag == 1) {
                    // 表示传递的是日出
                    WeatherBottomSheet bottomSheet = new WeatherBottomSheet(dayWeather.getSunRise(), 5, weatherBean1);
                    bottomSheet.show(getParentFragmentManager(), "WeatherBottomSheet");
                } else if (flag == 0) {
                    // 表示传递的是日落
                    WeatherBottomSheet bottomSheet = new WeatherBottomSheet(dayWeather.getSunset(), 6, weatherBean1);
                    bottomSheet.show(getParentFragmentManager(), "WeatherBottomSheet");
                }
            }
        });
        binding.cardPressure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BottomSheetTag", dayWeather.getPressure());
                WeatherBottomSheet bottomSheet = new WeatherBottomSheet(dayWeather.getPressure(), 7, weatherBean1);
                bottomSheet.show(getParentFragmentManager(), "WeatherBottomSheet");
            }
        });


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    private void updateFragmentUI(WeatherBean1 weatherBean1, WeatherBean2 weatherBean2) {
//        ((MainActivity) getActivity()).setWetherBean(weatherBean1, weatherBean2, cityName);
        updateWeather(weatherBean1);
        updateNowWeather(weatherBean2);
    }

    // 更新实时天气UI
    private void updateNowWeather(WeatherBean2 weatherBean2) {
        if (weatherBean2 == null) {
            Log.d("BottomFragment", "WeatherBean2 is null");
            return;
        }
        nowWeatherBean = weatherBean2.getNowWeatherBean();
        if (nowWeatherBean == null) {
            Log.d("BottomFragment", "NowWeatherBean is null");
            return;
        }
        Log.d("BottomFragment", "BodyTemp: " + nowWeatherBean.getBodyTemp());

        binding.tvBodytemp.setText(nowWeatherBean.getBodyTemp() + "°");
    }


    // 更新今日近日UI与未来天气
    private void updateWeather(WeatherBean1 weatherBean) {
        if (weatherBean == null) return;
        dayWeather = weatherBean.getList().get(0);
        if (dayWeather == null) return;

        // 更新天气信息
        binding.tvHumidity.setText(dayWeather.getHumidity() + "%");
        binding.tvWindSpeed.setText(dayWeather.getWindSpeedDay().split("-")[1] + "级");
        binding.tvPressure.setText(dayWeather.getPressure());

        String sunRiseTime = dayWeather.getSunRise();
        String sunSetTime = dayWeather.getSunset();
        int flag = chooseSun();

        if (flag == 0) {
            binding.tvSunRiseSet.setText("日落");
            binding.tvRiseSet.setText(sunSetTime);
        } else if (flag == 1) {
            binding.tvSunRiseSet.setText("日出");
            binding.tvRiseSet.setText(sunRiseTime);
        }


        // 更新 UV Index 显示
        int uvIndex = Integer.parseInt(dayWeather.getUvIndex());
        if (uvIndex >= 0 && uvIndex <= 2) {
            binding.tvUvray.setText("弱");
        } else if (uvIndex <= 4) {
            binding.tvUvray.setText("较弱");
        } else if (uvIndex <= 6) {
            binding.tvUvray.setText("较强");
        } else if (uvIndex <= 9) {
            binding.tvUvray.setText("强");
        } else {
            binding.tvUvray.setText("极强");
        }

        // 更新 Adapter 的数据
        List<DayWeatherBean> dayWeatherBeans = weatherBean.getList().subList(0, 7);
        futureWeatherAdapter.updateData(dayWeatherBeans);
    }

    private int chooseSun() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        try {
            Calendar calendar = Calendar.getInstance();
            Date currentTime = calendar.getTime();

            Calendar tenAMCalendar = Calendar.getInstance();
            tenAMCalendar.set(Calendar.HOUR_OF_DAY, 10);
            tenAMCalendar.set(Calendar.MINUTE, 0);
            Date tenAM = tenAMCalendar.getTime();

            Calendar tenPMCalendar = Calendar.getInstance();
            tenPMCalendar.set(Calendar.HOUR_OF_DAY, 22);
            tenPMCalendar.set(Calendar.MINUTE, 0);
            Date tenPM = tenPMCalendar.getTime();

            if (currentTime.after(tenAM) && currentTime.before(tenPM)) {
                // 日落
                return 0;
            } else {
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
