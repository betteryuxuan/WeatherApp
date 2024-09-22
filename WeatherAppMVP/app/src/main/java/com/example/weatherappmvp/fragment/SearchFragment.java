package com.example.weatherappmvp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherappmvp.R;
import com.example.weatherappmvp.adapter.CityAdapter;
import com.example.weatherappmvp.model.DataModel;
import com.example.weatherappmvp.presenter.MainPresenter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends BottomSheetDialogFragment {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private List<String> cityList;
    private List<String> filteredCityList;
    private MainPresenter mainPresenter;
    private CityAdapter cityAdapter;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                List<String> cities = (List<String>) msg.obj;
                filteredCityList.clear();
                filteredCityList.addAll(cities);
                Log.d("CityFragmentTag", "filteredCityList1: " + filteredCityList);
                cityAdapter.updateCityList(cities);
            }
        }
    };

    public SearchFragment(MainPresenter mainPresenter) {
        this.mainPresenter = mainPresenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cityList = new ArrayList<>();
        filteredCityList = new ArrayList<>(cityList);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = view.findViewById(R.id.searchView);
        recyclerView = view.findViewById(R.id.recyclerView);

        searchView.setIconifiedByDefault(false);

        cityAdapter = new CityAdapter(filteredCityList, new CityAdapter.OnCityClickListener() {
            @Override
            public void onCityClick(String cityName) {
                mainPresenter.handleData(cityName);
                dismiss();
            }
        });
        recyclerView.setAdapter(cityAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterCityList(newText);
                return true;
            }
        });

        return view;
    }

    private void filterCityList(String query) {
        if (TextUtils.isEmpty(query)) {
            filteredCityList.clear();
            filteredCityList.addAll(cityList);
            cityAdapter.updateCityList(filteredCityList);
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<String> retCityList = mainPresenter.requestSearchCity(query);
                    Message message = mHandler.obtainMessage(1, retCityList);
                    mHandler.sendMessage(message);
                }
            }).start();
        }
    }
}
