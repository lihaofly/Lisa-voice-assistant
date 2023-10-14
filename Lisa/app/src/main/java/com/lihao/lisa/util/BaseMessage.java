package com.lihao.lisa.util;

import com.lihao.lisa.view.util.User;

public class BaseMessage {
    //Define the message type
    public final static int ASR_MESSAGE = 1;
    public final static int TTS_MESSAGE = 2;
    public final static int WAKEUP_MESSAGE = 3;
    public final static int WEATHER_MESSAGE = 30;
    public final static int ASSISTANT_MESSAGE = 31;
    public final static int USER_MESSAGE = 32;
    public final static int EXCUTE_MESSAGE = 33;
    public final static int EUROPECUP_MESSAGE = 34;
    public final static int GUIDE_MESSAGE = 35;


    //Define the message result
    public final static int RESULT_SUCCESS = 1;
    public final static int RESULT_FAILED = -1;
    public final static int RESULT_TIMEOUT = -2;

    protected int mMessageType;
    protected int mMessageResult;
    protected long mCreatedAt; //Message created time
    protected String mShowMessage;//The message displayed in chat message box
    protected User mUser; //The information of user

    public BaseMessage(int mMessageType, int mMessageResult) {
        this.mMessageType = mMessageType;
        this.mMessageResult = mMessageResult;
        this.mCreatedAt =  System.currentTimeMillis();
    }

    public BaseMessage(int mMessageType, int mMessageResult, String mShowMesssage) {
        this.mMessageType = mMessageType;
        this.mMessageResult = mMessageResult;
        this.mShowMessage = mShowMesssage;
        this.mCreatedAt =  System.currentTimeMillis();
    }

    public BaseMessage(int mMessageType, int mMessageResult, String mShowMesssage, User mUser) {
        this.mMessageType = mMessageType;
        this.mMessageResult = mMessageResult;
        this.mShowMessage = mShowMesssage;
        this.mUser = mUser;
        this.mCreatedAt =  System.currentTimeMillis();
    }

    public int getMessageType() {
        return mMessageType;
    }

    public void setMessageType(int mMessageType) {
        this.mMessageType = mMessageType;
    }

    public int getMessageResult() {
        return mMessageResult;
    }

    public void setMessageResult(int mMessageResult) {
        this.mMessageResult = mMessageResult;
    }

    public String getShowMessage() {
        return mShowMessage;
    }

    public void setShowMessage(String mShowMessage) {
        this.mShowMessage = mShowMessage;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User mUser) {
        this.mUser = mUser;
    }

    public long getCreatedAt() {
        return mCreatedAt;
    }

    public void setmCreatedAt(long mCreatedAt) {
        this.mCreatedAt = mCreatedAt;
    }

    @Override
    public String toString() {
        return "BaseMessage{" +
                "mMessageType=" + mMessageType +
                ", mMessageResult=" + mMessageResult +
                ", mShowMessage='" + mShowMessage + '\'' +
                ", mUser=" + mUser +
                ", mCreatedAt=" + mCreatedAt +
                '}';
    }
}
