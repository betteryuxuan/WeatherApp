package com.example.weatherappmvp.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherappmvp.R;

import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder> {

    private List<String> cityList;

    private OnCityClickListener listener;

    public CityAdapter(List<String> cityList, OnCityClickListener listener) {
        this.cityList = cityList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_item_layout, parent, false);
        return new CityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CityViewHolder holder, int position) {
        String cityName = cityList.get(position);
        holder.cityName.setText(cityName);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(holder.itemView.getContext(), "点击了城市：" + cityName, Toast.LENGTH_SHORT).show();
                String city = cityName.split("-")[0];
                listener.onCityClick(city);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    public void updateCityList(List<String> newCityList) {
        cityList.clear();
        cityList.addAll(newCityList);
        Log.d("CityFragmentTag", "filteredCityList2: " + newCityList);
        notifyDataSetChanged();
    }

    static class CityViewHolder extends RecyclerView.ViewHolder {
        TextView cityName;
        ImageView imageView;

        public CityViewHolder(@NonNull View itemView) {
            super(itemView);
            this.cityName = itemView.findViewById(R.id.tv_cityname);
            this.imageView = itemView.findViewById(R.id.imageView);
        }
    }

    public interface OnCityClickListener {
        void onCityClick(String cityName);
    }

}
