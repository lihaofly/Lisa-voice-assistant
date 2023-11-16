package com.lihao.lisa.model.core.AISpeech.tts.listener;


import android.os.Handler;

import com.aispeech.AIError;
import com.aispeech.common.AIConstant;
import com.aispeech.export.listeners.AILocalTTSListener;
import com.aispeech.export.listeners.AITTSListener;
import com.lihao.lisa.model.core.base.BaseSynthesizerListener;
import com.lihao.lisa.util.MyLogger;

public class AISpeechTTSLocalListener extends BaseSynthesizerListener implements AILocalTTSListener {
    private static final String TAG = AISpeechTTSLocalListener.class.getSimpleName();
    public AISpeechTTSLocalListener(Handler handler) {
        super(handler);
        mHandler = handler;
    }

    @Override
    public void onInit(int status) {
        MyLogger.debug(TAG, "onInit()");
        if (status == AIConstant.OPT_SUCCESS) {
            MyLogger.debug(TAG, "Initialize success!");
        } else {
            MyLogger.error(TAG, "Initialize failed!");
            onBaseSpeakError(Integer.toString(status));
        }
    }

    @Override
    public void onSynthesizeStart(String s) {
        MyLogger.debug(TAG, "onSynthesizeStart() s: " + s);
    }

    @Override
    public void onSynthesizeDataArrived(String s, byte[] bytes) {
        MyLogger.debug(TAG, "onSynthesizeDataArrived() s: " + s);
    }

    @Override
    public void onSynthesizeFinish(String s) {
        MyLogger.debug(TAG, "onSynthesizeFinish() s: " + s);
    }

    @Override
    public void onError(String utteranceId, AIError error) {
        MyLogger.error(TAG, "onError: " + utteranceId + "," + error.toString());
        onBaseSpeakError(error.toString());
    }
    @Override
    public void onSpeechStart(String var1){
        MyLogger.debug(TAG, "onSpeechStart()" );
        onBaseSpeakStart();
    }

    @Override
    public void onSpeechProgress(int var1, int var2, boolean var3){
        MyLogger.debug(TAG, "onSpeechProgress() var1:" + var1 +
                " var2:" + var2 +
                " var3:" + var3);
//        float percent = (currentTime / totalTime) * 100;

        onBaseSpeakProgress((int)100,0,0);
    }

    @Override
    public void onSpeechFinish(String var1){
        MyLogger.debug(TAG, "onSpeechFinish() var1: " + var1);
        onBaseSpeakCompleted();
    }
    @Override
    public void onTimestampReceived(byte[] var1, int var2){

    }
}
