package com.example.weatherappmvp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.weatherappmvp.R;
import com.example.weatherappmvp.bean.DayWeatherBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FutureWeatherAdapter extends RecyclerView.Adapter<FutureWeatherAdapter.WeatherViewHolder> {
    private Context mContext;
    private List<DayWeatherBean> mWeatherBeans;

    public FutureWeatherAdapter(Context mContext, List<DayWeatherBean> weatherBeans) {
        this.mContext = mContext;
        this.mWeatherBeans = weatherBeans;
    }

    public void updateData(List<DayWeatherBean> mWeatherBeans) {
        this.mWeatherBeans = mWeatherBeans;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.wether_item_layout, parent, false);
        WeatherViewHolder viewHolder = new WeatherViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        DayWeatherBean dayWeather = mWeatherBeans.get(position);
        holder.day.setText(dateFormat(dayWeather.getFxDate()));
        holder.weather.setText(dayWeather.getTextDay());
        holder.temp.setText(dayWeather.getTempMin() + "° ~ " + dayWeather.getTempMax() + "°");
    }

    @Override
    public int getItemCount() {
        if (mWeatherBeans == null) {
            return 0;
        }
        return mWeatherBeans.size();
    }

    private String dateFormat(String dateStr) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-M-d");
        SimpleDateFormat newFormat = new SimpleDateFormat("M月d日");
        String formattedDate = "";
        try {
            Date date = originalFormat.parse(dateStr);
            formattedDate = newFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            return formattedDate;
        }
    }

    class WeatherViewHolder extends RecyclerView.ViewHolder {
        TextView day;
        TextView weather;
        TextView temp;

        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            day = itemView.findViewById(R.id.tv_day);
            weather = itemView.findViewById(R.id.tv_weather);
            temp = itemView.findViewById(R.id.tv_temp);
        }
    }
}
