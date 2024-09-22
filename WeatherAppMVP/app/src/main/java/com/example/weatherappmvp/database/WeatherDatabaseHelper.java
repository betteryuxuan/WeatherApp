package com.example.weatherappmvp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WeatherDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "weather.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_WEATHER = "weather_data";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CITY_NAME = "city_name";
    public static final String COLUMN_RESPONSE_DATA = "response_data";
    public static final String COLUMN_WEATHER_TYPE = "weather_type"; // 新增字段

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_WEATHER + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CITY_NAME + " TEXT, " +
                    COLUMN_WEATHER_TYPE + " TEXT, " +  // 天气类型字段
                    COLUMN_RESPONSE_DATA + " TEXT" +
                    ");";

    public WeatherDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_WEATHER + " ADD COLUMN " + COLUMN_WEATHER_TYPE + " TEXT");
        }
    }
}
