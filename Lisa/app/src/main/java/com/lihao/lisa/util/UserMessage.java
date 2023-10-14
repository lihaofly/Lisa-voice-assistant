package com.lihao.lisa.util;

import com.lihao.lisa.view.util.User;

public class UserMessage extends BaseMessage{
    private final static String mNickname = "User";

    public UserMessage(int mMessageResult) {
        super(BaseMessage.USER_MESSAGE, mMessageResult,"", new User(mNickname));
    }

    public UserMessage(int mMessageResult, String mShowMesssage) {
        super(BaseMessage.USER_MESSAGE, mMessageResult, mShowMesssage, new User(mNickname));
    }

    public UserMessage(int mMessageResult, String mShowMesssage, User mUser) {
        super(BaseMessage.USER_MESSAGE, mMessageResult, mShowMesssage, mUser);
    }
}
