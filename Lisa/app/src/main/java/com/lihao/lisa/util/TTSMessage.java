package com.lihao.lisa.util;

import android.util.Log;

import com.lihao.lisa.view.util.User;

public class TTSMessage extends AssistantMessage{
    public static final int TTS_STATUS_INPUT_TEXT_SELECTION = 5;
    public static final int TTS_STATUS_INPUT_TEXT_FINISHED = 6;
    public static final int TTS_STATUS_SYNTHES_TEXT_SELECTION = 2;
    public static final int TTS_STATUS_SYNTHES_TEXT_FINISHED = 3;

    private static final String TAG = "TTSMessage";
    private String mPlayedTTSString;
    private int mProgress;
    private boolean mIsFinished;
    private int mTTSStatus;

    public TTSMessage(int mMessageResult, int ttsStatus) {
        super(mMessageResult);
        this.mMessageType = TTS_MESSAGE;
        mTTSStatus = ttsStatus;
    }

    public TTSMessage(int mMessageResult, String mShowMesssage, int ttsStatus) {
        super(mMessageResult, mShowMesssage);
        this.mMessageType = TTS_MESSAGE;
        mTTSStatus = ttsStatus;
    }

    public TTSMessage(int mMessageResult, String mShowMesssage, User mUser, int ttsStatus) {
        super(mMessageResult, mShowMesssage, mUser);
        this.mMessageType = TTS_MESSAGE;
        mTTSStatus = ttsStatus;
    }

    public String getmPlayedTTSString() {
        if(mProgress <= mShowMessage.length()){
            mPlayedTTSString = mShowMessage.substring(0,mProgress);
        }else{
            mPlayedTTSString = mShowMessage;
        }
        return mPlayedTTSString;
    }

    public int getmProgress() {
        return mProgress;
    }

    public void setmProgress(int mProgress) {
        this.mProgress = mProgress;
    }

    public int getTTSStatus() {
        return mTTSStatus;
    }

    public void setTTSStatus(int mTTSStatus) {
        this.mTTSStatus = mTTSStatus;
    }

    public boolean getIsIsFinished() {
        if(mTTSStatus == TTS_STATUS_INPUT_TEXT_FINISHED){
            mIsFinished = true;
        }
        return mIsFinished;
    }

    public void setIsFinished(boolean mIsFinished) {
        this.mIsFinished = mIsFinished;
    }
}
