package com.lihao.lisa.model.core.iFlytek.tts.listener;

import android.os.Bundle;
import android.os.Handler;

import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.SpeechEvent;
import com.lihao.lisa.util.MyLogger;
import com.lihao.lisa.model.core.base.BaseSynthesizerListener;

/**
 * @ClassName iFlytekTTSListener
 * @Author lihao.fly@163.com
 * @Data 2023/10/23
 * @Description Register to SDK listen the status of Speech Synthesizer
 */

public class iFlytekTTSListener extends BaseSynthesizerListener implements SynthesizerListener {
    private static final String TAG = iFlytekTTSListener.class.getSimpleName();

    public iFlytekTTSListener(Handler handler) {
        super(handler);
        mHandler = handler;
    }

    @Override
    public void onSpeakBegin() {
        MyLogger.debug(TAG, "onSpeakBegin" );
        onBaseSpeakStart();
    }

    @Override
    public void onSpeakProgress(int percent, int beginPos, int endPos) {
        MyLogger.debug(TAG, "onSpeakProgress: percent: " + percent +
                " beginPos: " + beginPos +
                " endPos: " + endPos);
        onBaseSpeakProgress(percent,beginPos,endPos);
    }

    @Override
    public void onCompleted(SpeechError error) {
        MyLogger.debug(TAG, "onCompleted: is any error: " + error);
        if(error != null){
            onBaseSpeakError(error.toString());
        }
        onBaseSpeakCompleted();
    }

    @Override
    public void onSpeakPaused() {
        MyLogger.debug(TAG, "onSpeakPaused" );
        onBaseSpeakPaused();
    }

    @Override
    public void onSpeakResumed() {
        MyLogger.debug(TAG, "onSpeakResumed" );
        onBaseSpeakResumed();
    }

    @Override
    public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
        MyLogger.debug(TAG, "onBufferProgress: percent: " + percent +
                " beginPos: " + beginPos +
                " endPos: " + endPos +
                " info: " + info);
    }

    @Override
    public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        MyLogger.debug(TAG, "onEvent: eventType: " + eventType +
                " arg1: " + arg1 +
                " arg2: " + arg2);

            //The following code is used to obtain the session ID with the cloud.
            //When a business error occurs, the session ID is provided to technical support personnel.
            //It can be used to query the session log and locate the cause of the error.
            //If using local capabilities, the session id is null
            if (SpeechEvent.EVENT_SESSION_ID == eventType) {
                String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
                MyLogger.debug(TAG, "onEvent: session id =" + sid);
            }

            //当设置SpeechConstant.TTS_DATA_NOTIFY为1时，抛出buf数据
            if (SpeechEvent.EVENT_TTS_BUFFER == eventType) {
                byte[] buf = obj.getByteArray(SpeechEvent.KEY_EVENT_TTS_BUFFER);
                MyLogger.error(TAG, "onEvent: bufLengthis:" + buf.length);
            }
    }
}
