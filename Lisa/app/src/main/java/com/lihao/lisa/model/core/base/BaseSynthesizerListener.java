package com.lihao.lisa.model.core.base;

import static com.lihao.lisa.util.BaseMessage.RESULT_SUCCESS;
import static com.lihao.lisa.util.TTSMessage.TTS_STATUS_INPUT_TEXT_FINISHED;
import static com.lihao.lisa.util.TTSMessage.TTS_STATUS_INPUT_TEXT_SELECTION;


import android.os.Handler;
import android.os.Message;

import com.lihao.lisa.util.BaseMessage;
import com.lihao.lisa.util.MyLogger;
import com.lihao.lisa.util.TTSMessage;


/**
 * Abstract base behavior of Synthesizer
 */
public class BaseSynthesizerListener {
    private static final String TAG = BaseSynthesizerListener.class.getSimpleName();
    protected int mProgress;
    protected Handler mHandler;
    protected String mError;

    public BaseSynthesizerListener(Handler mHandler) {
        this.mHandler = mHandler;
    }

    /**
     * Voice synthesizer start to speak
     */
    protected void onBaseSpeakStart() {
        MyLogger.debug(TAG, "onBaseSpeakStart" );
    }

    /**
     * Voice synthesizer speak process
     * @param percent Percent of finished prompt
     * @param beginPos //TBD
     * @param endPos //TBD
     */
    protected void onBaseSpeakProgress(int percent, int beginPos, int endPos) {
        MyLogger.debug(TAG, "onBaseSpeakProgress: percent: " + percent +
                " beginPos: " + beginPos +
                " endPos: " + endPos);
        mProgress = percent;
        TTSMessage ttsMsg = new TTSMessage(RESULT_SUCCESS, TTS_STATUS_INPUT_TEXT_SELECTION);
        ttsMsg.setmProgress(mProgress);
        sendMessage(ttsMsg, BaseMessage.TTS_MESSAGE);
    }

    /**
     * Voice synthesizer speak completed
     */
    protected void onBaseSpeakCompleted() {
        MyLogger.debug(TAG, "onBaseSpeakCompleted" );
        TTSMessage ttsMsg = new TTSMessage(RESULT_SUCCESS, TTS_STATUS_INPUT_TEXT_FINISHED);
        ttsMsg.setmProgress(mProgress);
        sendMessage(ttsMsg, BaseMessage.TTS_MESSAGE);
    }

    /**
     * Voice synthesizer paused
     */
    protected void onBaseSpeakPaused() {
        MyLogger.debug(TAG, "onBaseSpeakPaused" );
    }

    /**
     * Voice synthesizer resumed from pause
     */
    protected void onBaseSpeakResumed() {
        MyLogger.debug(TAG, "onBaseSpeakResumed" );
    }

    /**
     * Voice synthesizer error happened
     */
    protected void onBaseSpeakError(String errorCode) {
        MyLogger.error(TAG, "onBaseSpeakError: " + errorCode);
        mError = errorCode;
    }

    /**
     * Update the status of synthesizer to handler of main thread
     * @param obj TTS message which contain the status
     * @param action Define the message action
     */
    protected void sendMessage(TTSMessage obj, int action) {
        if (mHandler != null) {
            Message msg = Message.obtain();
            msg.what = action;
            msg.obj = obj;
            mHandler.sendMessage(msg);
        }
    }

}
