package com.example.weatherappmvp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.weatherappmvp.R;
import com.example.weatherappmvp.bean.DayWeatherBean;
import com.example.weatherappmvp.bean.WeatherBean1;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WeatherBottomSheet extends BottomSheetDialogFragment {

    private String arg1;
    private int flag = 0;
    private WeatherBean1 weatherBean1;
    private DayWeatherBean dayWeather;

    public WeatherBottomSheet(String arg1, int flag, WeatherBean1 weatherBean1) {
        this.arg1 = arg1;
        this.flag = flag;
        this.weatherBean1 = weatherBean1;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (weatherBean1 != null) {
            dayWeather = weatherBean1.getList().get(0);
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        holder.weather.setText(dayWeather.getTextDay());
//        holder.temp.setText(dayWeather.getTempMin() + "° ~ " + dayWeather.getTempMax() + "°");
        View view = inflater.inflate(R.layout.fragment_weather_bottom_sheet, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tvTemp = view.findViewById(R.id.tv_basic_temp);
        TextView tvFlag = view.findViewById(R.id.tv_flag);
        TextView tvBasicInfo = view.findViewById(R.id.tv_info);
        if (dayWeather != null) {
            tvTemp.setText(dayWeather.getTextDay() + "    " + dayWeather.getTempMin() + "° ~ " + dayWeather.getTempMax() + "°");
        }
        Log.e("BottomSheetTag", dayWeather.toString());
        Log.e("BottomSheetTag", String.valueOf(flag));


        switch (flag) {
            case 1:
                int humidity = 0;
                try {
                    humidity = Integer.parseInt(arg1);
                    Log.d("BottomSheetTag", String.valueOf(humidity));
                } catch (NumberFormatException e) {
                    Log.d("BottomSheetTag", "" + arg1);
                }

                if (humidity <= 30) {
                    tvFlag.setText("湿度过低");
                    tvBasicInfo.setText("皮肤保湿：使用保湿霜或润肤露来防止皮肤干燥。\n室内加湿：考虑使用加湿器来增加室内湿度，特别是使用空调或暖气的室内。\n多喝水：保持身体水分，防止脱水。");
                } else if (humidity <= 50) {
                    tvFlag.setText("湿度较低");
                    tvBasicInfo.setText("皮肤保湿：使用保湿霜或润肤露来防止皮肤干燥。\n增加室内湿度：在室内放置一些水盆或湿布，让水分自然蒸发。\n室内植物：这个湿度水平适合大多数室内植物的生长。\n通风：适当的通风可以帮助维持室内湿度在健康水平。");
                } else if (humidity <= 60) {
                    tvFlag.setText("湿度适中");
                    tvBasicInfo.setText(String.format("防霉：高湿度可能导致霉菌和细菌生长，定期检查家中是否有霉菌迹象。\n减少加湿：如果室内湿度过高，减少或关闭加湿器。\n通风：增加通风，比如打开窗户或使用排气扇，以减少室内湿度。\n电器设备：确保电器设备远离潮湿区域，防止短路。"));
                } else if (humidity <= 70) {
                    tvFlag.setText("湿度较高");
                    tvBasicInfo.setText("健康问题：高湿度可能导致呼吸道问题，特别是对于有哮喘或其他呼吸问题的人。\n衣物干燥：确保衣物彻底干燥，避免潮湿衣物长时间放置。\n食物保存：注意食物保存，高湿度可能导致食物更快变质。\n使用除湿器：考虑使用除湿器来降低室内湿度。");
                } else {
                    tvFlag.setText("湿度过高");
                    tvBasicInfo.setText("使用除湿器：考虑使用除湿器来降低室内湿度，特别是在潮湿的地下室或封闭空间。\n通风换气：在室外湿度较低时，适当开窗通风，帮助减少室内湿气。\n检查霉菌：定期检查墙壁、浴室、厨房等容易潮湿的地方是否有霉菌生长，并及时清理。");
                }
                break;
            case 2:
                int windLevel = Integer.parseInt(arg1);

                Log.d("BottomSheetTag", "风速" + String.valueOf(windLevel));
                if (windLevel == 0) {
                    tvFlag.setText("无风");
                    tvBasicInfo.setText("风平浪静：没有风，适合所有户外活动。\n空气流动：室内空气流动可能减缓，可考虑使用风扇或空调促进空气流通。");
                } else if (windLevel <= 2) {
                    tvFlag.setText("微风");
                    tvBasicInfo.setText("风速轻微：适合户外活动和旅行。\n衣物选择：由于风力较小，建议穿着轻便的衣物。\n户外活动：可以进行风筝、帆船等需要微风的活动。");
                } else if (windLevel <= 4) {
                    tvFlag.setText("和风");
                    tvBasicInfo.setText("风速温和：适合户外活动，但需注意可能的尘埃和落叶。\n户外活动：适合进行自行车、徒步等户外活动。\n注意防火：风力适中，需要注意户外用火安全。");
                } else if (windLevel <= 7) {
                    tvFlag.setText("强风");
                    tvBasicInfo.setText("风速较强：户外活动时需注意安全。\n户外活动：避免在高楼大厦或广告牌附近停留。\n注意安全：关好门窗，注意高空坠物。");
                } else if (windLevel <= 10) {
                    tvFlag.setText("大风");
                    tvBasicInfo.setText("风速猛烈：户外活动需谨慎，避免水上活动。\n交通安全：注意行车安全，强风可能影响驾驶。\n加固物品：注意加固可能被风吹动的物品，如阳台上的花盆。");
                } else {
                    tvFlag.setText("暴风");
                    tvBasicInfo.setText("极端天气：避免外出，确保个人安全。\n紧急准备：准备应急物资，如手电筒、食物和水。\n注意安全：留意官方的天气预报和安全指示。");
                }
                break;
            case 3:
                int perceivedTemp = Integer.parseInt(arg1);

                if (perceivedTemp <= 10) {
                    tvFlag.setText("寒冷");
                    tvBasicInfo.setText("保暖：请穿着保暖衣物，如羽绒服、围巾、帽子和手套。\n户外活动：尽量减少户外活动时间，避免长时间暴露在寒冷环境中。\n健康监测：注意体温过低和冻伤的风险，特别是对于老年人和儿童。");
                } else if (perceivedTemp <= 20) {
                    tvFlag.setText("凉爽");
                    tvBasicInfo.setText("舒适：这是一个凉爽舒适的温度范围。\n户外活动：适合进行各种户外活动，如徒步、骑行等。\n饮食建议：适量增加温热食物的摄入，以保持体温。");
                } else if (perceivedTemp <= 30) {
                    tvFlag.setText("温暖");
                    tvBasicInfo.setText("防晒：外出时请涂抹防晒霜，佩戴太阳镜和帽子。\n补水：请确保充足的水分摄入，以防脱水。\n室内通风：适当开窗通风，保持室内空气新鲜。");
                } else if (perceivedTemp > 30) {
                    tvFlag.setText("炎热");
                    tvBasicInfo.setText("防暑：避免在一天中最热的时候进行户外活动。\n补水：频繁饮水，以防中暑。\n室内降温：使用空调或风扇，保持室内温度适宜。");
                }
                break;
            case 4:
                int uvIndex = Integer.parseInt(arg1);

                if (uvIndex <= 2) {
                    tvFlag.setText("弱");
                    tvBasicInfo.setText("紫外线辐射低：可以安全地享受户外活动，但仍建议涂抹防晒霜。\n户外活动：适合进行长时间的户外活动，如徒步、游泳等。\n防晒建议：涂抹 SPF 15 或以上的防晒霜，即使在紫外线辐射低时也应保护皮肤。");
                } else if (uvIndex <= 4) {
                    tvFlag.setText("较弱");
                    tvBasicInfo.setText("紫外线辐射中等：建议采取一定的防护措施。\n户外活动：在紫外线辐射高峰时段（通常是上午10点至下午4点）尽量避免直接暴露在阳光下。\n防晒建议：涂抹 SPF 30 或以上的防晒霜，戴上帽子和太阳镜。");
                } else if (uvIndex <= 6) {
                    tvFlag.setText("较强");
                    tvBasicInfo.setText("紫外线辐射高：需要采取额外的防护措施。\n户外活动：尽量避免在紫外线辐射高峰时段进行户外活动。\n防晒建议：涂抹 SPF 50 或以上的防晒霜，穿长袖衣物和长裤，戴宽边帽和太阳镜。");
                } else if (uvIndex <= 9) {
                    tvFlag.setText("强");
                    tvBasicInfo.setText("紫外线辐射非常高：应尽量避免在户外活动。\n户外活动：如果必须外出，请尽量寻找阴凉处，并频繁重新涂抹防晒霜。\n防晒建议：涂抹 SPF 50+ 或以上，PA++++ 等级的防晒霜，穿戴防护衣物。");
                } else {
                    tvFlag.setText("极强");
                    tvBasicInfo.setText("紫外线辐射极端：应避免在户外活动，特别是在紫外线辐射高峰时段。\n健康警告：长时间暴露可能导致严重的皮肤灼伤和眼睛损伤。\n防晒建议：如果必须外出，请穿戴防护衣物，佩戴帽子、太阳镜，并使用防晒霜。");
                }
                break;
            case 5:
                // 日出前
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                Date sunriseTime = null;
                try {
                    sunriseTime = dateFormat.parse(arg1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                long[] timeToSunrise = calculateTimeToEvent(sunriseTime);
                if (timeToSunrise[0] == 0) {
                    tvFlag.setText("距离日出还有\n" + timeToSunrise[1] + " 分");
                } else {
                    tvFlag.setText("距离日出还有\n" + timeToSunrise[0] + " 时 " + timeToSunrise[1] + " 分");
                }
                tvBasicInfo.setText("天已经黑了，请注意安全。");
                tvBasicInfo.setHeight(120);
                break;
            case 6:
                // 日落前
                SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm");
                Date sunsetTime = null;
                try {
                    sunsetTime = dateFormat2.parse(arg1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                long[] timeToSunset = calculateTimeToEvent(sunsetTime);
                if (timeToSunset[0] == 0) {
                    tvFlag.setText("距离日落还有\n" + timeToSunset[1] + " 分");
                } else {
                    tvFlag.setText("距离日落还有\n" + timeToSunset[0] + " 时 " + timeToSunset[1] + " 分");
                }
                tvBasicInfo.setText("白天时间，适合户外活动。");
                tvFlag.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
                break;
            case 7:
                int pressure = Integer.parseInt(arg1);

                if (pressure < 980) {
                    tvFlag.setText("气压低");
                    tvBasicInfo.setText("气压较低：可能会导致天气不稳定，出现降雨或风暴。\n出行建议：出行时请关注天气预报，适当准备雨具。\n健康建议：低气压可能导致某些人感到头痛或疲劳，保持充足的水分和休息。");
                } else if (pressure < 1010) {
                    tvFlag.setText("气压正常");
                    tvBasicInfo.setText("气压正常：天气较为稳定，适合户外活动。\n出行建议：可以放心进行户外活动，享受阳光。\n健康建议：保持良好的作息和饮食，增强体质。");
                } else if (pressure < 1030) {
                    tvFlag.setText("气压高");
                    tvBasicInfo.setText("气压较高：天气晴朗，适合户外活动。\n出行建议：适合进行各种户外活动，如远足、骑行等。\n健康建议：高气压可能导致部分人感到不适，适当调整作息。");
                } else {
                    tvFlag.setText("气压极高");
                    tvBasicInfo.setText("气压极高：天气干燥，可能导致一些人感到不适。\n出行建议：适合进行户外活动，但要注意补水。\n健康建议：保持室内通风，避免长时间待在密闭空间。");
                }

        }
    }

    private long[] calculateTimeToEvent(Date eventTime) {
        Calendar now = Calendar.getInstance();

        Calendar event = Calendar.getInstance();
        event.set(Calendar.HOUR_OF_DAY, eventTime.getHours());
        event.set(Calendar.MINUTE, eventTime.getMinutes());
        event.set(Calendar.SECOND, 0);

        if (now.after(event)) {
            event.add(Calendar.DATE, 1);
        }

        long timeDifference = event.getTimeInMillis() - now.getTimeInMillis();
        long hours = (timeDifference / (1000 * 60 * 60)) % 24;
        long minutes = (timeDifference / (1000 * 60)) % 60;

        return new long[]{hours, minutes};
    }
}
