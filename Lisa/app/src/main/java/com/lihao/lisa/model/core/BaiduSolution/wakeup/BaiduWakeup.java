package com.lihao.lisa.model.core.baidu.wakeup;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.baidu.aip.asrwakeup3.core.inputstream.InFileStream;
import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.google.gson.Gson;
import com.lihao.lisa.model.features.InformationSearch.EuropeCupBean;
import com.lihao.lisa.util.BaseMessage;

import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;

public class BaiduWakeup implements EventListener {
    private EventManager mWakeup = null;
    private Context mContext;
    private Handler mHandler;
    private static BaiduWakeup mInstance = null;
    private static final String TAG = "BaiduWakeup";

    private BaiduWakeup(Context mContext, Handler handler) {
        this.mContext = mContext;
        this.mHandler = handler;
    }

    public static BaiduWakeup getInstance(Context context, Handler handler) {
        if (mInstance == null) {
            synchronized (BaiduWakeup.class) {
                if (mInstance == null) {
                    mInstance = new BaiduWakeup(context, handler);
                }
            }
        }
        return mInstance;
    }

    public boolean InitWakeup() {
        // 基于SDK唤醒词集成1.1 初始化EventManager
        mWakeup = EventManagerFactory.create(mContext, "wp");
        // 基于SDK唤醒词集成1.3 注册输出事件
        mWakeup.registerListener(this); //  EventListener 中 onEvent方法
        return true;
    }

    public void StartWakeup() {
        Log.d(TAG, "startWakeup");
        Map<String, Object> params = new TreeMap<String, Object>();
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        params.put(SpeechConstant.WP_WORDS_FILE, "assets:///wakeup_lisa_hello.bin");
        // "assets:///WakeUp.bin" 表示WakeUp.bin文件定义在assets目录下
        InFileStream.setContext(mContext);
        String json = null; // 这里可以替换成你需要测试的json
        json = new JSONObject(params).toString();
        mWakeup.send(SpeechConstant.WAKEUP_START, json, null, 0, 0);
        Log.d(TAG, "startWakeup: Input Data: " + json);
    }

    public void StopWakeup() {
        mWakeup.send(SpeechConstant.WAKEUP_STOP, null, null, 0, 0); //
    }

    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        Log.d(TAG, "onEvent: name: " + name);
        Gson gson = new Gson();
        if (params != null) {
            WakeupBean wakeupBean = gson.fromJson(params, WakeupBean.class);
            Log.d(TAG, "onEvent: wakeupBean: " + wakeupBean.toString());
            if (wakeupBean.getErrorCode() == 0) {
                Message msg = Message.obtain();
                msg.what = BaseMessage.WAKEUP_MESSAGE;
                msg.obj = wakeupBean;
                mHandler.sendMessage(msg);
                StopWakeup();
            }
        }
    }
}
