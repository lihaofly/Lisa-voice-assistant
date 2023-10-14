package com.lihao.lisa.model.features.weather;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.lihao.lisa.util.BaseMessage;
import com.lihao.lisa.util.WeatherMessage;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.base.Range;
import com.qweather.sdk.bean.base.Unit;
import com.qweather.sdk.bean.geo.GeoBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.HeConfig;
import com.qweather.sdk.view.QWeather;

import java.util.List;

import static com.qweather.sdk.bean.base.Lang.ZH_HANS;

public class Weather {
    private static final String TAG = "Lisa: Weather";
    private static Weather mInstance = null;
    private Context mContext;
    private Handler mHandler;

    private Weather(){
    }

    public static Weather getInstance(){
        if( mInstance == null ){
            synchronized (Weather.class){
                if( mInstance == null ) {
                    mInstance = new Weather();
                }
            }
        }
        return mInstance;
    }

    public boolean InitWeather(Context context, Handler handler){
        mContext = context;
        mHandler = handler;
        HeConfig.init("HE2106201849121095", "b396259bd5994c538777a045ee2955a7");
        HeConfig.switchToDevService();
        return true;
    }

    private void QuearyGeo(String cityName){
        QWeather.getGeoCityLookup(mContext, cityName, Range.CN, 10, ZH_HANS, new QWeather.OnResultGeoListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, "onError: Error happened"+ throwable.getMessage().toString());
            }

            @Override
            public void onSuccess(GeoBean geoBean) {
                List<GeoBean.LocationBean> locationbean = geoBean.getLocationBean();
                for( int i=0; i < locationbean.size(); i++){
                    GeoBean.LocationBean item = locationbean.get(i);
                    String cityId = item.getId();
                    Log.d(TAG, "onSuccess: cityname: "+item.getName());
                    Log.d(TAG, "onSuccess: cityId: "+cityId);
                }

            }
        });
    }

    public void QuearyWeather(String cityName){

        QWeather.getGeoCityLookup(mContext, cityName, Range.CN, 10, ZH_HANS, new QWeather.OnResultGeoListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, "onError: Error happened: " + throwable.getMessage().toString());
            }

            @Override
            public void onSuccess(GeoBean geoBean) {
                List<GeoBean.LocationBean> locationbean = geoBean.getLocationBean();

                //Temp code for only show one city
                for( int i=0; i < locationbean.size() && i < 1; i++){
                    GeoBean.LocationBean item = locationbean.get(i);
                    String cityId = item.getId();
                    Log.d(TAG, "onSuccess: cityname: "+item.getName());
                    Log.d(TAG, "onSuccess: cityId: "+cityId);

                    QWeather.getWeatherNow(mContext, cityId, ZH_HANS, Unit.METRIC, new QWeather.OnResultWeatherNowListener() {
                        @Override
                        public void onError(Throwable e) {
                            Log.i(TAG, "getWeather onError: " + e);
                        }

                        @Override
                        public void onSuccess(WeatherNowBean weatherBean) {
                            Log.i(TAG, "getWeather onSuccess: " + new Gson().toJson(weatherBean));

                            //Temp code, Need to be refactor
                            Message msg = Message.obtain();
                            msg.what = BaseMessage.WEATHER_MESSAGE;
                            msg.obj = createWeaterMessage(weatherBean);
                            mHandler.sendMessage(msg);

                            //mHandler.sendEmptyMessage()
                            //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
                            if (Code.OK == weatherBean.getCode()) {
                                WeatherNowBean.NowBaseBean now = weatherBean.getNow();
                            } else {
                                //在此查看返回数据失败的原因
                                Code code = weatherBean.getCode();
                                Log.i(TAG, "failed code: " + code);
                            }
                        }
                    });
                }

            }
        });

    }

    private BaseMessage createWeaterMessage(WeatherNowBean weatherBean){
        WeatherMessage weatherMsg;
        if(weatherBean.getCode() == Code.OK) {
            weatherMsg = new WeatherMessage(BaseMessage.RESULT_SUCCESS);
        }else{
            weatherMsg = new WeatherMessage(BaseMessage.RESULT_FAILED);
        }
        weatherMsg.setFxLink(weatherBean.getBasic().getFxLink());
        weatherMsg.setUpdateTime(weatherBean.getBasic().getUpdateTime());
        weatherMsg.setCode(weatherBean.getCode().getTxt());
        weatherMsg.setCloud(weatherBean.getNow().getCloud());
        weatherMsg.setDew(weatherBean.getNow().getDew());
        weatherMsg.setFeelsLike(weatherBean.getNow().getFeelsLike());
        weatherMsg.setHumidity(weatherBean.getNow().getHumidity());
        weatherMsg.setIcon(weatherBean.getNow().getIcon());
        weatherMsg.setObsTime(weatherBean.getNow().getObsTime());
        weatherMsg.setPrecip(weatherBean.getNow().getPrecip());
        weatherMsg.setPressure(weatherBean.getNow().getPressure());
        weatherMsg.setTemp(weatherBean.getNow().getTemp());
        weatherMsg.setText(weatherBean.getNow().getText());
        weatherMsg.setVis(weatherBean.getNow().getVis());
        weatherMsg.setWind360(weatherBean.getNow().getWind360());
        weatherMsg.setWindDir(weatherBean.getNow().getWindDir());
        weatherMsg.setWindScale(weatherBean.getNow().getWindScale());
        weatherMsg.setWindSpeed(weatherBean.getNow().getWindSpeed());
        weatherMsg.setLicenseList(weatherBean.getRefer().getLicenseList().toString());
        weatherMsg.setSourcesList(weatherBean.getRefer().getSourcesList().toString());

        return weatherMsg;
    }


}
