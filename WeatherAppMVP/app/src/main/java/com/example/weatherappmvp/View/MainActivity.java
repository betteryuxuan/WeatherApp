package com.example.weatherappmvp.View;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.weatherappmvp.R;
import com.example.weatherappmvp.databinding.ActivityMainBinding;
import com.example.weatherappmvp.MainContract;
import com.example.weatherappmvp.adapter.MyFragmentVPAdapter;
import com.example.weatherappmvp.bean.DayWeatherBean;
import com.example.weatherappmvp.bean.NowWeatherBean;
import com.example.weatherappmvp.bean.WeatherBean1;
import com.example.weatherappmvp.bean.WeatherBean2;
import com.example.weatherappmvp.fragment.SearchFragment;
import com.example.weatherappmvp.presenter.MainPresenter;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements MainContract.IMainView {
    private MyFragmentVPAdapter fragmentVPAdapter;
    private ActivityMainBinding binding;
    private MainPresenter mPresenter;
    private List<String> cityNameList;

    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    private String currentLatitudeAndLongitude;
    //声明定位回调监听器,在监听器的回调方法里处理定位结果
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {

                if (aMapLocation.getErrorCode() == 0) {
                    aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    aMapLocation.getLatitude();//获取纬度
                    aMapLocation.getLongitude();//获取经度
                    aMapLocation.getProvince();//省信息
                    aMapLocation.getCity();//城市信息
                    aMapLocation.getDistrict();//城区信息
                    //获取定位时间
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(aMapLocation.getTime());
                    String formatDate = df.format(date);

                    Log.d("LocationTag", "定位成功：" + aMapLocation.getLatitude() + "," + aMapLocation.getLongitude() + "," + aMapLocation.getDistrict());


                    String[] splitLatitude = String.valueOf(aMapLocation.getLatitude()).split("\\.");
                    String[] splitLongitude = String.valueOf(aMapLocation.getLongitude()).split("\\.");
                    currentLatitudeAndLongitude = splitLongitude[0] + "." + splitLongitude[1].substring(0, 2) + "," + splitLatitude[0] + "." + splitLatitude[1].substring(0, 2);


                    Log.d("LocationTag", currentLatitudeAndLongitude);

                    mPresenter.handleData(currentLatitudeAndLongitude);
                    binding.viewPager.setCurrentItem(0);

                    Log.d("MainActivityTag", "1" + cityNameList.toString());
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("LocationTag", "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 检查权限并获取当前位置
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return;
        } else {
            Log.d("LocationTag", "已获取权限");
        }


        mPresenter = new MainPresenter(this, this);

        List<String> cityNameRet = mPresenter.readCityList(this);
        if (null != cityNameRet) {
            cityNameList = cityNameRet;
        } else {
            Log.d("MainActivityTag", "onCreate中的readCityList啥也没读到");
            cityNameList = new ArrayList<>();
            cityNameList.add("西安");
            cityNameList.add("重庆");
            cityNameList.add("武汉");
            cityNameList.add("北京");
        }

        if (mPresenter.isNetworkAvailable()) {
            initLocation();
        } else {
            Toast.makeText(MainActivity.this, "网络未连接", Toast.LENGTH_SHORT).show();
            mPresenter.handleData("108.90,34.15");
            binding.viewPager.setCurrentItem(0);
        }

        // 定位是异步，这里没把定位到的城市添加到cityNameList里
        // 用读取的菜单初始化
        mPresenter.initMenu(cityNameList);

        Log.d("MainActivityTag", "2" + cityNameList.toString());
        mPresenter.viewPagerInit(cityNameList, this);
        mPresenter.updateMenuSelection(0);

        // 添加城市按钮
        binding.imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.showAddCityDialogFragment();
            }
        });

        // ViewPager 的页面切换逻辑
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Log.d("MyTag", position + "---" + cityNameList.size());
                Log.d("ViewPagerTag", "1" + cityNameList.toString());
                mPresenter.onPageSelected(position);
                mPresenter.updateMenuSelection(position);

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // 如果 ViewPager 正在滑动，则禁用下拉刷新
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    binding.swipeRefreshLayout.setEnabled(false);
                } else {
                    // 滑动结束后重新启用下拉刷新
                    binding.swipeRefreshLayout.setEnabled(true);
                }
            }
        });
        binding.viewPager.setOffscreenPageLimit(6);


        mPresenter.setupNavigationView();

        // 设置侧边栏的点击与长按事件
        setNavigationItemSelectedListener();
        setNavigationItemLongListener();

        // 下拉刷新
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(MainActivity.this, "已刷新", Toast.LENGTH_SHORT).show();
//                mPresenter.handleData(binding.edit.toString());
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        Log.d("ListTag", "onStop" + cityNameList.toString());
        mPresenter.saveCityList(cityNameList, this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mPresenter.isNetworkAvailable()) {
            initLocation();
        } else {
            mPresenter.handleData("108.90,34.15");
        }
        Log.d("ListTag", "onRestart" + cityNameList.toString());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initLocation();
            } else {
                Toast.makeText(this, "需要定位权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void initLocation() {
        try {
            mLocationClient = new AMapLocationClient(getApplicationContext());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();

        // 设置高精度模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        if (null != mLocationClient) {
            mLocationClient.setLocationOption(mLocationOption);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }

        //获取一次定位结果：
        mLocationOption.setOnceLocation(true);
        //获取最近3s内精度最高的一次定位结果：
        mLocationOption.setOnceLocationLatest(true);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(30000);
        //启动定位
        mLocationClient.startLocation();
        Log.d("LocationTag", "111");
    }

    @Override
    public void setupNavigationView() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);

        // 这个对象用于同步 DrawerLayout 和 Toolbar 的状态
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, // 当前 Activity
                drawerLayout, // DrawerLayout 对象
                toolbar, // Toolbar 对象
                R.string.open, // Drawer 打开的描述
                R.string.close // Drawer 关闭的描述
        );
        toggle.syncState();
        // 监听器用于处理 Drawer 的打开和关闭事件
        drawerLayout.addDrawerListener(toggle);
    }

    @Override
    public void setTopData(WeatherBean1 weatherBean1, WeatherBean2 weatherBean2, String name) {
        Log.d("UITag", "已调用");
        updateCityName(name);

        // 更新今日天气UI
        if (weatherBean1 != null) {
            DayWeatherBean dayWeather = weatherBean1.getList().get(0);
            if (dayWeather != null) {
                String data = dayWeather.getTextDay() + "  最高 " + dayWeather.getTempMax() + "° 最低 " + dayWeather.getTempMin() + "°";
                Log.d("UITag", data);
                Random random = new Random();
                int randomNum = random.nextInt(2);
                if (data.contains("多云")) {
                    if (randomNum == 0) {
                        binding.main.setBackgroundResource(R.drawable.yun_background_1);
                    } else {
                        binding.main.setBackgroundResource(R.drawable.yun_background_2);
                    }
                } else if (data.contains("晴")) {
                    if (randomNum == 0) {
                        binding.main.setBackgroundResource(R.drawable.qing_background_1);
                    } else {
                        binding.main.setBackgroundResource(R.drawable.qing_background_2);
                    }
                } else if (data.contains("阴")) {
                    if (randomNum == 0) {
                        binding.main.setBackgroundResource(R.drawable.yin_background_1);
                    } else {
                        binding.main.setBackgroundResource(R.drawable.yin_background_2);
                    }
                } else if (data.contains("雨")) {
                    if (randomNum == 0) {
                        binding.main.setBackgroundResource(R.drawable.rain_background_1);
                    } else {
                        binding.main.setBackgroundResource(R.drawable.rain_background_2);
                    }
                }
                binding.tvBasicInformation.setText(data);
            }
        }

        // 更新实时天气UI
        if (weatherBean2 != null) {
            NowWeatherBean nowWeatherBean = weatherBean2.getNowWeatherBean();
            if (nowWeatherBean != null) {
                binding.tvTemperature.setText(nowWeatherBean.getNowTemp() + "°");
            }
        }
    }

    @Override
    public void setViewPager(List<String> cityNameList) {
        fragmentVPAdapter = new MyFragmentVPAdapter(getSupportFragmentManager(), cityNameList);
        binding.viewPager.setAdapter(fragmentVPAdapter);
        binding.indicator.setViewPager(binding.viewPager);
        // 适配器与指示器绑定，监听数据变化
        fragmentVPAdapter.registerDataSetObserver(binding.indicator.getDataSetObserver());
    }

    private void setNavigationItemSelectedListener() {
        binding.navView.setNavigationItemSelectedListener(mItem -> {
            String title = mItem.getTitle().toString();
            mPresenter.onNavigationItemSelected(title);
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    public void setNavigationItemLongListener() {
        Menu menu = binding.navView.getMenu();

        binding.navView.post(() -> {
            for (int i = 1; i < menu.size() - 1; i++) {
                MenuItem menuItem = menu.getItem(i);
                String curCityName = menuItem.getTitle().toString();

                // 获取 MenuItem 的 View
                View itemView = binding.navView.findViewById(menuItem.getItemId());

//                Log.d("MenuTag2", curCityName + "---" + menuItem.getItemId());
                // 如果找到对应的 View，为其设置长按事件
                if (itemView != null) {
                    itemView.setOnLongClickListener(v -> {
//                        Toast.makeText(this, "长按菜单项: " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                        showDelateCityDialog(curCityName);
                        return true;
                    });
                }
            }
        });
    }

    @Override
    public void initMenu(List<String> cityNameList) {
        Menu menu = binding.navView.getMenu();
        menu.clear();
        // 更新菜单
        menu.add(Menu.NONE, R.id.nav_location_city, Menu.NONE, "当前城市");

        for (String cityName : cityNameList) {
            menu.add(Menu.NONE, Menu.FIRST + menu.size(), Menu.NONE, cityName);

        }
        menu.add(Menu.NONE, R.id.nav_add_city, Menu.NONE, "添加更多城市");
    }

    @Override
    public void showAddCityDialogFragment() {
        Log.d("UIFragmentTag", "showAddCityDialog");
        SearchFragment searchFragment = new SearchFragment(mPresenter);
        searchFragment.show(getSupportFragmentManager(), "SearchFragment");

//        // 之前的选中项
//        MenuItem checkedItem = binding.navView.getCheckedItem();
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        builder.setTitle("添加城市");
//
//        final EditText input = new EditText(MainActivity.this);
//        builder.setView(input);
//        builder.setCancelable(false);
//
//        builder.setPositiveButton("确定", (dialog, which) -> {
//            String cityName = input.getText().toString();
//            if (cityName.isEmpty()) {
//                Toast.makeText(MainActivity.this, "请输入城市名", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            mPresenter.handleData(cityName);
//        });
//        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (checkedItem != null) {
//                    binding.navView.setCheckedItem(checkedItem.getItemId());
//                }
//            }
//        });
//
//        builder.show();
    }

    public void AddCityMenu(String cityName) {
        cityNameList.add(1, cityName);
        mPresenter.refreshViewPager();

        Menu menu = binding.navView.getMenu();
        menu.clear();

        menu.add(Menu.NONE, R.id.nav_location_city, Menu.NONE, "当前城市");

        for (int i = 1; i < cityNameList.size(); i++) {
            menu.add(Menu.NONE, Menu.FIRST + i, Menu.NONE, cityNameList.get(i));  // 使用城市名称添加菜单项
        }
        menu.add(Menu.NONE, R.id.nav_add_city, Menu.NONE, "添加更多城市");

        binding.viewPager.setCurrentItem(1);
        //重新设置长按点击事件，否则名字不匹配
        setNavigationItemLongListener();
    }

    public void showDelateCityDialog(String cityName) {
        // 之前的选中项
        MenuItem checkedItem = binding.navView.getCheckedItem();

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("确定删除" + cityName + "吗");
        builder.setCancelable(false);
        builder.setIcon(R.drawable.icon);


        builder.setPositiveButton("确定", (dialog, which) -> {
            deleteCityMenu(cityName);
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (checkedItem != null) {
                    binding.navView.setCheckedItem(checkedItem.getItemId());
                }
            }
        });

        builder.show();
    }

    public void deleteCityMenu(String cityName) {
        cityNameList.remove(cityName);

        Menu menu = binding.navView.getMenu();
        menu.clear();

        menu.add(Menu.NONE, R.id.nav_location_city, Menu.NONE, "当前城市");

        for (int i = 1; i < cityNameList.size(); i++) {
            menu.add(Menu.NONE, Menu.FIRST + i, Menu.NONE, cityNameList.get(i));
        }
        menu.add(Menu.NONE, R.id.nav_add_city, Menu.NONE, "添加更多城市");

        // 更新 ViewPager 和 Indicator
        mPresenter.refreshViewPager();

        //重新设置长按点击事件，否则名字不匹配
        setNavigationItemLongListener();
    }

    @Override
    public void updateMenuSelection(int position) {
        if (position == 0) {
            Menu menu = binding.navView.getMenu();
            for (int i = 0; i < menu.size(); i++) {
                MenuItem item = menu.getItem(i);
                if (i == 0) {
                    item.setChecked(true);
                } else {
                    item.setChecked(false);
                }
            }
            return;
        }
        String cityName = cityNameList.get(position);
        Menu menu = binding.navView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getTitle().equals(cityName)) {
                item.setChecked(true);
            } else {
                item.setChecked(false);
            }
        }
    }

    @Override
    public void updateViewPager(int cityIndex) {
        binding.viewPager.setCurrentItem(cityIndex, true);
    }

    public void updateCityName(String cityName) {
        if (cityName.contains("当前定位城市:")) {
            String loctionName = cityName.split(":")[1];
            binding.edit.setText(loctionName);
            if (loctionName.equals("108.90,34.15")) {
                binding.edit.setText("长安");
            }
            if (!cityNameList.contains(loctionName)) {

                cityNameList.add(0, loctionName);
                mPresenter.refreshViewPager();
                Log.d("LocationTag2", cityNameList.toString());
            }

            return;
        } else {
            binding.edit.setText(cityName);
        }
        Menu menu = binding.navView.getMenu();
        // 如果没有这个城市就加到侧栏里
        for (int i = 0; i < menu.size(); i++) {
            if (menu.getItem(i).getTitle().equals(cityName)) {
                binding.viewPager.setCurrentItem(i, false);
//                Toast.makeText(MainActivity.this, "该城市已添加", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        AddCityMenu(cityName);
    }

    public void refreshViewPager() {
        Log.d("MainActivityTag", "3ViewPager" + cityNameList.toString());
        if (fragmentVPAdapter != null) {
            fragmentVPAdapter = null;
        }
        fragmentVPAdapter = new MyFragmentVPAdapter(getSupportFragmentManager(), cityNameList);
        binding.viewPager.setAdapter(fragmentVPAdapter);
        binding.indicator.setViewPager(binding.viewPager);
    }

    public String getLatitudeAndLongitude() {
        return currentLatitudeAndLongitude;
    }
}