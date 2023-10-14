package com.lihao.lisa.view.messageItems;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.lihao.lisa.R;
import com.lihao.lisa.util.BaseMessage;
import com.lihao.lisa.view.messageItems.holder.AssistantMessageHolder;
import com.lihao.lisa.view.messageItems.holder.EuropeCupMessageHolder;
import com.lihao.lisa.view.messageItems.holder.GuideMessageHolder;
import com.lihao.lisa.view.messageItems.holder.SentMessageHolder;
import com.lihao.lisa.view.messageItems.holder.WeatherMessageHolder;

import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_DEFAULT = 0;
    private static final int VIEW_TYPE_MESSAGE_USER = 1;
    private static final int VIEW_TYPE_MESSAGE_ASSISTANT = 2;
    private static final int VIEW_TYPE_MESSAGE_WEATHER = 3;
    private static final int VIEW_TYPE_MESSAGE_Europe_CUP = 4;
    private static final int VIEW_TYPE_MESSAGE_GUIDE = 5;
    private Context mContext;
    private List<BaseMessage> mMessageList;
    private static final String TAG = "MessageListAdapter";

    public MessageListAdapter(Context context, List<BaseMessage> messageList) {
        mContext = context;
        mMessageList = messageList;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        BaseMessage message = (BaseMessage) mMessageList.get(position);
        int result = VIEW_TYPE_MESSAGE_DEFAULT;
        switch(message.getMessageType())
        {
            case BaseMessage.ASR_MESSAGE:
                result = VIEW_TYPE_MESSAGE_USER;
                break;
            case BaseMessage.TTS_MESSAGE:
            case BaseMessage.EXCUTE_MESSAGE:
                result = VIEW_TYPE_MESSAGE_ASSISTANT;
                break;
            case BaseMessage.WEATHER_MESSAGE:
                result = VIEW_TYPE_MESSAGE_WEATHER;
                break;
            case BaseMessage.EUROPECUP_MESSAGE:
                result = VIEW_TYPE_MESSAGE_Europe_CUP;
                break;
            case BaseMessage.GUIDE_MESSAGE:
                result = VIEW_TYPE_MESSAGE_GUIDE;
                break;
            default:
                result = VIEW_TYPE_MESSAGE_DEFAULT;
                break;
        }
        return result;
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_USER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_user, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_ASSISTANT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_assistant, parent, false);
            return new AssistantMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_WEATHER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_weather, parent, false);
            return new WeatherMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_Europe_CUP) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_europe_cup, parent, false);
            return new EuropeCupMessageHolder(view, mContext);
        }else if (viewType == VIEW_TYPE_MESSAGE_GUIDE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_guide, parent, false);
            return new GuideMessageHolder(view);
        }


        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BaseMessage message = (BaseMessage) mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_USER:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_ASSISTANT:
                ((AssistantMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_WEATHER:
                ((WeatherMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_Europe_CUP:
                ((EuropeCupMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_GUIDE:
                ((GuideMessageHolder) holder).bind(message);
                break;
            default:
                Log.w(TAG, "onBindViewHolder: Not Supported");
                break;
        }
    }

}