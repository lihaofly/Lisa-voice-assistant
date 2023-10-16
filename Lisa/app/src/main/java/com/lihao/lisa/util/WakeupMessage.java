package com.lihao.lisa.util;

import com.lihao.lisa.model.core.BaiduSolution.wakeup.WakeupBean;
import com.lihao.lisa.view.util.User;

public class WakeupMessage extends AssistantMessage{
    WakeupBean mWakeupBean;

    public WakeupMessage(int mMessageResult, WakeupBean mWakeupBean) {
        super(mMessageResult);
        this.mWakeupBean = mWakeupBean;
        this.mMessageType = WAKEUP_MESSAGE;
    }

    public WakeupMessage(int mMessageResult, String mShowMessage, WakeupBean mWakeupBean) {
        super(mMessageResult, mShowMessage);
        this.mWakeupBean = mWakeupBean;
        this.mMessageType = WAKEUP_MESSAGE;
    }

    public WakeupMessage(int mMessageResult, String mShowMessage, User mUser, WakeupBean mWakeupBean) {
        super(mMessageResult, mShowMessage, mUser);
        this.mWakeupBean = mWakeupBean;
        this.mMessageType = WAKEUP_MESSAGE;
    }
}
