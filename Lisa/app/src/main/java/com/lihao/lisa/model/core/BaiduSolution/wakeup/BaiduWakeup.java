package com.lihao.lisa.model.core.BaiduSolution.wakeup;

import java.util.Map;
import java.util.TreeMap;
import org.json.JSONObject;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.baidu.aip.asrwakeup3.core.inputstream.InFileStream;
import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.google.gson.Gson;
import com.lihao.lisa.util.BaseMessage;
import com.lihao.lisa.util.MyLogger;

public class BaiduWakeup implements EventListener {
    private static final String TAG = BaiduWakeup.class.getSimpleName();
    private EventManager mWakeup = null;
    private Context mContext;
    private Handler mHandler;
    private static BaiduWakeup mInstance = null;

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
        mWakeup = EventManagerFactory.create(mContext, "wp");
        mWakeup.registerListener(this);
        return true;
    }

    public void StartWakeup() {
        MyLogger.debug(TAG, "StartWakeup()");
        Map<String, Object> params = new TreeMap<String, Object>();
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        params.put(SpeechConstant.WP_WORDS_FILE, "assets:///wakeup_lisa_hello.bin");
        InFileStream.setContext(mContext);
        String json = null;
        json = new JSONObject(params).toString();
        mWakeup.send(SpeechConstant.WAKEUP_START, json, null, 0, 0);
        MyLogger.debug(TAG, "startWakeup: Input Data: " + json);
    }

    public void StopWakeup() {
        mWakeup.send(SpeechConstant.WAKEUP_STOP, null, null, 0, 0); //
    }

    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        MyLogger.debug(TAG, "onEvent: name: " + name);
        Gson gson = new Gson();
        if (params != null) {
            WakeupBean wakeupBean = gson.fromJson(params, WakeupBean.class);
            MyLogger.debug(TAG, "onEvent: wakeupBean: " + wakeupBean.toString());
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
