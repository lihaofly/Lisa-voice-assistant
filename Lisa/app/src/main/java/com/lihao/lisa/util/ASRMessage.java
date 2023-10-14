package com.lihao.lisa.util;

import android.util.Log;

import com.baidu.aip.asrwakeup3.core.util.AsrMessage;
import com.lihao.lisa.view.util.User;

import org.json.JSONException;
import org.json.JSONObject;

public class ASRMessage extends UserMessage{

    //Define ASR status
    public static final int ASR_STATUS_NONE = 2;
    public static final int ASR_STATUS_READY = 3;
    public static final int ASR_STATUS_SPEAKING = 4;
    public static final int ASR_STATUS_RECOGNITION = 5;
    public static final int ASR_STATUS_FINISHED = 6;
    public static final int ASR_STATUS_LONG_SPEECH_FINISHED = 7;
    public static final int ASR_STATUS_NLU_FINISHED = 8;
    public static final int ASR_STATUS_ALL_FINISHED = 9;
    public static final int ASR_STATUS_STOPPED = 10;

    private int mAsrStatus;
    private String mAsrStatusStr;
    private String mAsrResult;
    private String mHint;

    public ASRMessage(int mMessageResult) {
        super(mMessageResult);
        this.mMessageType = ASR_MESSAGE;
    }

    public ASRMessage(int mMessageResult, String mShowMesssage) {
        super(mMessageResult, mShowMesssage);
        this.mMessageType = ASR_MESSAGE;
    }

    public ASRMessage(int mMessageResult, String mShowMesssage, User mUser) {
        super(mMessageResult, mShowMesssage, mUser);
        this.mMessageType = ASR_MESSAGE;
    }

    public ASRMessage(AsrMessage msg) {
        super(RESULT_SUCCESS);
        this.mMessageType = ASR_MESSAGE;
        this.mAsrStatus = msg.getAsrStatus();
        this.mAsrStatusStr = msg.getAsrStatusStr();
        this.mAsrResult = msg.getAsrResult();
        this.mHint = msg.getHint();

        //Set the mShowMessage
        if( this.mAsrResult != null ) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(this.mAsrResult);
                mShowMessage = jsonObject.getString("best_result");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public int getAsrStatus() {
        return mAsrStatus;
    }

    public void setAsrStatus(int mAsrStatus) {
        this.mAsrStatus = mAsrStatus;
    }

    public String getAsrStatusStr() {
        return mAsrStatusStr;
    }

    public void setAsrStatusStr(String mAsrStatusStr) {
        this.mAsrStatusStr = mAsrStatusStr;
    }

    public String getAsrResult() {
        return mAsrResult;
    }

    public void setAsrResult(String mAsrResult) {
        this.mAsrResult = mAsrResult;
    }

    public String getHint() {
        return mHint;
    }

    public void setHint(String mHint) {
        this.mHint = mHint;
    }

    @Override
    public String toString() {
        return "ASRMessage{" +
                "mAsrStatus=" + mAsrStatus +
                ", mAsrStatusStr='" + mAsrStatusStr + '\'' +
                ", mAsrResult='" + mAsrResult + '\'' +
                ", mHint='" + mHint + '\'' +
                ", mMessageType=" + mMessageType +
                ", mMessageResult=" + mMessageResult +
                ", mCreatedAt=" + mCreatedAt +
                ", mShowMessage='" + mShowMessage + '\'' +
                ", mUser=" + mUser +
                '}';
    }
}
