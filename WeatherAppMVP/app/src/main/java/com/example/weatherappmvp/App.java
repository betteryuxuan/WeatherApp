package com.example.weatherappmvp;
import android.app.Application;
import android.content.Context;

import com.amap.api.location.AMapLocationClient;

public class App  extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Context mContext = this;
        // 定位隐私政策同意
        AMapLocationClient.updatePrivacyShow(mContext,true,true);
        AMapLocationClient.updatePrivacyAgree(mContext,true);

    }
}
