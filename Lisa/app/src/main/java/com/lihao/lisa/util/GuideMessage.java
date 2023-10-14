package com.lihao.lisa.util;

import com.lihao.lisa.view.util.User;

public class GuideMessage extends AssistantMessage{
    public GuideMessage(int mMessageResult) {
        super(mMessageResult);
        mMessageType = BaseMessage.GUIDE_MESSAGE;
        mShowMessage = "帮助";
    }

    public GuideMessage(int mMessageResult, String mShowMessage) {
        super(mMessageResult, mShowMessage);
        mMessageType = BaseMessage.GUIDE_MESSAGE;
        mShowMessage = "帮助";
    }

    public GuideMessage(int mMessageResult, String mShowMessage, User mUser) {
        super(mMessageResult, mShowMessage, mUser);
        mMessageType = BaseMessage.GUIDE_MESSAGE;
        mShowMessage = "帮助";
    }
}
