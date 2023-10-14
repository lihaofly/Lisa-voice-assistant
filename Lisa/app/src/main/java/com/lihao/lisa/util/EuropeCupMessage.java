package com.lihao.lisa.util;

import com.lihao.lisa.model.features.InformationSearch.EuropeCupBean;
import com.lihao.lisa.view.util.User;
import java.util.List;

public class EuropeCupMessage extends BaseMessage{

    private EuropeCupBean mEuropeCupBean;

    public EuropeCupMessage(int mMessageResult) {
        super(BaseMessage.EUROPECUP_MESSAGE, mMessageResult);
        if(mShowMessage.isEmpty()){
            this.mShowMessage = "为您找到如下结果";
        }
    }

    public EuropeCupMessage(int mMessageResult, String mShowMessage, EuropeCupBean mEuropeCupBean) {
        super(BaseMessage.EUROPECUP_MESSAGE, mMessageResult, mShowMessage);
        this.mEuropeCupBean = mEuropeCupBean;
        if(mShowMessage.isEmpty()){
            this.mShowMessage = "为您找到如下结果";
        }
    }

    public EuropeCupMessage(int mMessageResult, String mShowMesssage, User mUser) {
        super(BaseMessage.EUROPECUP_MESSAGE, mMessageResult, mShowMesssage, mUser);
        if(mShowMessage.isEmpty()){
            this.mShowMessage = "为您找到如下结果";
        }
    }

    public EuropeCupBean getEuropeCupBean() {
        return mEuropeCupBean;
    }

    public void setEuropeCupBean(EuropeCupBean mEuropeCupBean) {
        this.mEuropeCupBean = mEuropeCupBean;
    }
}
