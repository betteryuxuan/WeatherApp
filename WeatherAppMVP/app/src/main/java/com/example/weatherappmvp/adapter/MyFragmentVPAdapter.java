package com.example.weatherappmvp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


import com.example.weatherappmvp.fragment.BottomFragment;

import java.util.List;

public class MyFragmentVPAdapter extends FragmentPagerAdapter {
    List<String> cityNameList;

    public MyFragmentVPAdapter(@NonNull FragmentManager fm, List<String> cityNameList) {
        super(fm);
        this.cityNameList = cityNameList;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
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
    public int getItemPosition(@NonNull Object object) {
        if (cityNameList.contains(object)) {
            return cityNameList.indexOf(object);
        } else {
            return POSITION_NONE;
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}
