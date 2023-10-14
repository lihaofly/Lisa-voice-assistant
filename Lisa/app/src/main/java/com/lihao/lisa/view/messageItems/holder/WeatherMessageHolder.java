package com.lihao.lisa.view.messageItems.holder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.lihao.lisa.R;
import com.lihao.lisa.util.BaseMessage;
import com.lihao.lisa.util.WeatherMessage;

public class WeatherMessageHolder extends RecyclerView.ViewHolder {
    TextView mMessageText, mMessageTitle, mMessageSummary;
    TextView mMessageWindDir,mMessageWindlevel, mMessageUR, mMessageHumi, mMessagePressure, mMessagePreci;
    LottieAnimationView mWeatherIcon;

    public WeatherMessageHolder(View itemView) {
        super(itemView);
        mWeatherIcon = (LottieAnimationView) itemView.findViewById(R.id.weather_icon);
        mMessageText = (TextView) itemView.findViewById(R.id.text_gchat_message_europe_cup);
        mMessageTitle = (TextView) itemView.findViewById(R.id.weather_title);
        mMessageSummary = (TextView) itemView.findViewById(R.id.weather_summary);

        mMessageWindDir = (TextView) itemView.findViewById(R.id.weather_win_dir);
        mMessageWindlevel= (TextView) itemView.findViewById(R.id.weather_win_level);
        mMessageUR = (TextView) itemView.findViewById(R.id.weather_ultraviolet_ray);
        mMessageHumi = (TextView) itemView.findViewById(R.id.weather_humidity_level);
        mMessagePressure = (TextView) itemView.findViewById(R.id.weather_pressure_level);
        mMessagePreci = (TextView) itemView.findViewById(R.id.weather_precipitation_level);

    }

    public void bind(BaseMessage message) {
        WeatherMessage wMsg = (WeatherMessage)message;
        mMessageText.setText(message.getShowMessage());
        mMessageSummary.setText(message.getShowMessage());

        mMessageWindDir.setText(wMsg.getWindDir());
        mMessageWindlevel.setText(wMsg.getWindSpeed());

        String str = String.format("%s° %s", wMsg.getTemp(),wMsg.getText());
        mMessageTitle.setText(str);

        String humi = String.format("%s", wMsg.getHumidity());
        mMessageHumi.setText(humi);

        String pres = String.format("%shPa", wMsg.getPressure());
        mMessagePressure.setText(pres);

        String preCi = String.format("%smm", wMsg.getPrecip());
        mMessagePreci.setText(preCi);

        updateWeatherIcon(wMsg);

    }

    private void updateWeatherIcon(WeatherMessage message)
    {
        mWeatherIcon.setAnimation("Weather/weather-partly-cloudy.json");
    }



}