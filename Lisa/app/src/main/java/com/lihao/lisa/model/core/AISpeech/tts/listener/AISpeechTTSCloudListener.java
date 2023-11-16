package com.lihao.lisa.model.core.AISpeech.tts.listener;


import android.os.Handler;

import com.aispeech.AIError;
import com.aispeech.common.AIConstant;
import com.aispeech.export.listeners.AITTSListener;
import com.lihao.lisa.model.core.base.BaseSynthesizerListener;
import com.lihao.lisa.util.MyLogger;

public class AISpeechTTSCloudListener extends BaseSynthesizerListener implements AITTSListener {
    private static final String TAG = AISpeechTTSCloudListener.class.getSimpleName();
    public AISpeechTTSCloudListener(Handler handler) {
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
    public void onReady(String utteranceId) {
        MyLogger.debug(TAG, "onReady: " + utteranceId);
        onBaseSpeakStart();
    }

    @Override
    public void onProgress(int currentTime, int totalTime, boolean isRefTextTTSFinished) {
         MyLogger.debug(TAG, "onProgress() currentTime:" + currentTime +
                                     "ms, totalTime:" + totalTime +
                                     "ms, Reliability:" + isRefTextTTSFinished);
         float percent = (currentTime / totalTime) * 100;

         onBaseSpeakProgress((int)percent,0,0);
    }

    @Override
    public void onCompletion(String utteranceId) {
        MyLogger.debug(TAG, "onCompletion() utteranceId: " + utteranceId);
        onBaseSpeakCompleted();
    }

    @Override
    public void onError(String utteranceId, AIError error) {
        MyLogger.error(TAG, "onError: " + utteranceId + "," + error.toString());
        onBaseSpeakError(error.toString());
    }

    @Override
    public void onTimestampReceived(byte[] var1, int var2){
        MyLogger.error(TAG, "onTimestampReceived()");
    }

    @Override
    public void onPhonemesDataArrived(String var1, String var2){
        MyLogger.error(TAG, "onPhonemesDataArrived()");
    }
}
