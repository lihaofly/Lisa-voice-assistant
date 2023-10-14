package com.lihao.lisa.util;

import com.lihao.lisa.view.util.User;

public class ExecuteMessage extends AssistantMessage{
    public ExecuteMessage(int mMessageResult) {
        super(mMessageResult);
        mMessageType = BaseMessage.EXCUTE_MESSAGE;
    }

    public ExecuteMessage(int mMessageResult, String mShowMessage) {
        super(mMessageResult, mShowMessage);
        mMessageType = BaseMessage.EXCUTE_MESSAGE;

        if(mShowMessage.equals("")){
            this.mShowMessage = "对不起，这个我暂时还不会。";
        }else{
            this.mShowMessage =mShowMessage;
        }
    }

    public ExecuteMessage(int mMessageResult, String mShowMessage, User mUser) {
        super(mMessageResult, mShowMessage, mUser);
        mMessageType = BaseMessage.EXCUTE_MESSAGE;
        if(mShowMessage.equals("")){
            this.mShowMessage = "对不起，这个我暂时还不会。";
        }else{
            this.mShowMessage =mShowMessage;
        }
    }
}
