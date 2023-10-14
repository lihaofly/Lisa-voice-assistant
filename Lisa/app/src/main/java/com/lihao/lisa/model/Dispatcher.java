package com.lihao.lisa.model;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.baidu.aip.asrwakeup3.core.util.AsrMessage;
import com.lihao.lisa.model.features.InformationSearch.InformationSearch;
import com.lihao.lisa.model.features.VehicleBridge.VehicleBridge;
import com.lihao.lisa.model.features.weather.Weather;
import com.lihao.lisa.util.ASRMessage;
import com.lihao.lisa.util.BaseMessage;
import com.lihao.lisa.util.ExecuteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Dispatcher {
    private static Dispatcher mInstance = null;
    private static final String TAG = "Lisa-Dispatcher";

    private Context mContext;
    private Handler mHandler;

    private VehicleBridge mVehicleBridge = null;
    private Weather mWeather  = null;
    private InformationSearch mInformationSearch = null;

    private Dispatcher(Context context, Handler handler){
        mContext = context;
        mHandler = handler;
        initVehicleBridge();
        initWeather();
        informationSearch();
    }

    public static Dispatcher getInstance(Context context, Handler handler){
        if( mInstance == null ){
            synchronized (Dispatcher.class){
                if(mInstance == null){
                    mInstance = new Dispatcher(context, handler);
                }
            }
        }
        return mInstance;
    }

    private boolean initVehicleBridge(){
        mVehicleBridge = VehicleBridge.getInstance();
        mVehicleBridge.Connect("B8:9F:09:F4:01:B2");
        return true;
    }

    private boolean initWeather(){
        mWeather = Weather.getInstance();
        mWeather.InitWeather(mContext, mHandler);
        return true;
    }

    private boolean informationSearch(){
        mInformationSearch = InformationSearch.getInstance(mHandler);
        return true;
    }

    public boolean DispatchMsg(ASRMessage msg)
    {
        try {
            Log.d(TAG, "DispatchMsg: " + msg.getAsrResult());

            JSONObject jsonObject = new JSONObject(msg.getAsrResult());
            String rawText = jsonObject.getString("raw_text");
            Log.d(TAG, "DispatchMsg: " + msg.getAsrResult());
            JSONArray results = jsonObject.getJSONArray("results");
            if( results.length() >0 ){
                String domain = results.getJSONObject(0).getString("domain");
                String intent = results.getJSONObject(0).getString("intent");
                Log.d(TAG, "DispatchMsg: domain: " + domain + " Intent: " + intent);

                if(domain.equals("instruction")){
                    mVehicleBridge.SendMessage(msg.getAsrResult());
                }else if(domain.equals("weather")){
                    mWeather.QuearyWeather("成都");
                }else if(domain.equals("joke")){
                    mInformationSearch.SearchJoke();
                }else if(domain.equals("search")){
                    mInformationSearch.SearchEuropeCup();
                }
                else if(domain.equals("robot")){
                    Message message = Message.obtain();
                    message.what = BaseMessage.EXCUTE_MESSAGE;
                    message.obj = new ExecuteMessage(BaseMessage.RESULT_SUCCESS, "大家好，欢迎大家来参加今天的比赛。我的名字叫丽莎，我还很小，还有很多需要学习的，希望大家喜欢我。");
                    mHandler.sendMessage(message);
                }
                else{
                    SendNotSupportMessage();
                    Log.w(TAG, "DispatchMsg: Not Supporting features");
                }
            }else if(rawText.contains("名字")){
                Message message = Message.obtain();
                message.what = BaseMessage.EXCUTE_MESSAGE;
                message.obj = new ExecuteMessage(BaseMessage.RESULT_FAILED, "我的名字叫丽莎，不好意思！我和秀静同学同名。但，我真的没法解Audio的bug。我还很小，还有很多需要学习的。");
                mHandler.sendMessage(message);
            }else if(rawText.contains("介绍")){
                Message message = Message.obtain();
                message.what = BaseMessage.EXCUTE_MESSAGE;
                message.obj = new ExecuteMessage(BaseMessage.RESULT_SUCCESS, "大家好，欢迎大家来参加今天的比赛。我的名字叫丽莎，我还很小，还有很多需要学习的，希望大家喜欢我。");
                mHandler.sendMessage(message);
            }else if(rawText.contains("拉票") || rawText.contains("拉个票")){
                Message message = Message.obtain();
                message.what = BaseMessage.EXCUTE_MESSAGE;
                message.obj = new ExecuteMessage(BaseMessage.RESULT_SUCCESS, "好呀！先说好，得奖了，我们五五分哟！那我开始啦：各位小哥哥小姐姐们，丽莎在此卖个萌啦，动动您的手指，为我投上一票吧。");
                mHandler.sendMessage(message);
            }
            else{
                SendNotSupportMessage();
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return true;
    }
    
    public void Cancel(){
        Log.d(TAG, "Cancel: Operation");
        //TODO: Add interrupt operation
    }

    private void SendNotSupportMessage(){
        Message message = Message.obtain();
        message.what = BaseMessage.EXCUTE_MESSAGE;
        message.obj = new ExecuteMessage(BaseMessage.RESULT_FAILED, "");
        mHandler.sendMessage(message);
    }


}
