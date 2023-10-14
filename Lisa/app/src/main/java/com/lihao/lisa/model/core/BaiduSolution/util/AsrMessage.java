package com.baidu.aip.asrwakeup3.core.util;

/**
 * Created by Lihao on 2021/6/23.
 */

public class AsrMessage {
    private int mAsrStatus;
    private String mAsrStatusStr;
    private String mAsrResult;
    private String mHint;
    private long mTimeStamp;

    public AsrMessage(int asrStatus, String asrStatusStr, String asrResult,String hint, long timeStamp){
        mAsrStatus = asrStatus;
        mAsrStatusStr = asrStatusStr;
        mAsrResult = asrResult;
        mHint = hint;
        mTimeStamp = timeStamp;

    }

    public void setAsrStatus(int mAsrStatus) {
        this.mAsrStatus = mAsrStatus;
    }

    public void setAsrStatusStr(String mAsrStatusStr) {
        this.mAsrStatusStr = mAsrStatusStr;
    }

    public void setAsrResult(String mAsrResult) {
        this.mAsrResult = mAsrResult;
    }

    public void setHint(String mHint) {
        this.mHint = mHint;
    }

    public void setTimeStamp(long mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }

    public int getAsrStatus() {
        return mAsrStatus;
    }

    public String getAsrStatusStr() {
        return mAsrStatusStr;
    }

    public String getAsrResult() {
        return mAsrResult;
    }

    public String getHint() {
        return mHint;
    }

    public long getTimeStamp() {
        return mTimeStamp;
    }

    @Override
    public String toString() {
        return "AsrMessage{" +
                "mAsrStatus=" + mAsrStatus +
                ", mAsrStatusStr='" + mAsrStatusStr + '\'' +
                ", mAsrResult='" + mAsrResult + '\'' +
                ", mHint='" + mHint + '\'' +
                ", mTimeStamp=" + mTimeStamp +
                '}';
    }
}


