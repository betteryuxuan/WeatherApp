package com.example.weatherappmvp.adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.weatherappmvp.fragment.BottomFragment;

import java.util.List;

public class MyFragmentVPAdapter extends FragmentStatePagerAdapter {
    List<String> cityNameList;

    public MyFragmentVPAdapter(@NonNull FragmentManager fm, List<String> cityNameList) {
        super(fm);
        this.cityNameList = cityNameList;
        Log.d("ViewPagerTag", cityNameList.toString());
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Log.d("AAATag", "1" + cityNameList.get(position));

        return BottomFragment.newInstance(cityNameList.get(position));
    }

    @Override
    public int getCount() {
        return cityNameList == null ? 0 : cityNameList.size();
    }

    public void updateCityList(List<String> newCityList) {
        this.cityNameList = newCityList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        // notifyDataSetChanged() 页面不刷新问题的方法
        return POSITION_NONE;
    }

}
