package com.lihao.lisa.view.messageItems.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lihao.lisa.R;
import com.lihao.lisa.model.features.InformationSearch.EuropeCupBean;
import com.lihao.lisa.util.BaseMessage;
import com.lihao.lisa.util.EuropeCupMessage;
import com.lihao.lisa.view.messageItems.EuropeCup.ECScheduleDateList;

public class EuropeCupMessageHolder extends RecyclerView.ViewHolder{
    TextView messageText, dateText;
    RecyclerView mSchedule;
    Context mContext;


    public EuropeCupMessageHolder(View itemView, Context mContext) {
        super(itemView);
        messageText = (TextView)itemView.findViewById(R.id.text_gchat_message_europe_cup);
        dateText = (TextView)itemView.findViewById(R.id.date_europe_cup);
        mSchedule = (RecyclerView)itemView.findViewById(R.id.recycler_europe_cup_schedule);
        this.mContext = mContext;
    }


    public void bind(BaseMessage message) {
        messageText.setText(message.getShowMessage());
        EuropeCupMessage ecMessage = (EuropeCupMessage)message;
        EuropeCupBean europeCupBean = ecMessage.getEuropeCupBean();

        ECScheduleDateList EcSchDateList = new ECScheduleDateList(mContext, mSchedule,europeCupBean.getResult());
        EcSchDateList.NotifyChange();
    }


}

