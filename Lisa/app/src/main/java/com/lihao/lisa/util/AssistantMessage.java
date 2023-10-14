package com.lihao.lisa.util;

import com.lihao.lisa.view.util.User;

public class AssistantMessage extends BaseMessage{
    private final static String mNickname = "Lisa";

    public AssistantMessage(int mMessageResult) {
        super(BaseMessage.ASSISTANT_MESSAGE, mMessageResult, "", new User(mNickname));
    }

    public AssistantMessage(int mMessageResult, String mShowMessage) {
        super(BaseMessage.ASSISTANT_MESSAGE, mMessageResult, mShowMessage, new User(mNickname));
    }

    public AssistantMessage(int mMessageResult, String mShowMessage, User mUser) {
        super(BaseMessage.ASSISTANT_MESSAGE, mMessageResult, mShowMessage, mUser);
    }
}
