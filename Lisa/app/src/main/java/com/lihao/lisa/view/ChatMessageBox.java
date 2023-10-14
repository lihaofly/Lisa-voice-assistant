package com.lihao.lisa.view;

import android.content.Context;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lihao.lisa.util.BaseMessage;
import com.lihao.lisa.view.messageItems.MessageListAdapter;
import com.lihao.lisa.view.util.SpeedyLinearLayoutManager;
import com.lihao.lisa.view.util.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatMessageBox {
    private static final String TAG = "Lisa: ChatMessageBox";
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private String inputingMessage;
    List<BaseMessage> mMessageList;
//    private static ChatMessageBox mInstance = null;

//    public static ChatMessageBox getInstance(Context context, RecyclerView view){
//        if(mInstance == null){
//            synchronized (ChatMessageBox.class){
//                if(mInstance == null){
//                    mInstance = new ChatMessageBox(context, view);
//                }
//            }
//        }
//        return mInstance;
//    }

    public ChatMessageBox(Context context, RecyclerView view){
        Log.d(TAG, "ChatMessageBox: ");
        mMessageRecycler = view;

        mMessageRecycler.setVerticalFadingEdgeEnabled(true);
        mMessageRecycler.setFadingEdgeLength(1000);
        mMessageList = new ArrayList<BaseMessage>();

        //mMessageRecycler.setLayoutManager(new LinearLayoutManager(context));
        mMessageRecycler.setLayoutManager(new SpeedyLinearLayoutManager(context));

        mMessageAdapter = new MessageListAdapter(context, mMessageList);
        mMessageRecycler.setAdapter(mMessageAdapter);
    }

    public boolean AddMessage(BaseMessage message){
        mMessageList.add(message);
        mMessageAdapter.notifyDataSetChanged();
        //mMessageRecycler.scrollToPosition(mMessageList.size()-1);
        mMessageRecycler.smoothScrollToPosition(mMessageList.size()-1);
        return true;
    }

    public boolean UpdateMessage(BaseMessage message, boolean finish){
        Log.d(TAG, "UpdateMessage: message: "+message.toString());
        if(inputingMessage == null){
            Log.d(TAG, "UpdateMessage: input Message is empty");
            inputingMessage = message.getShowMessage();
            mMessageList.add(message);
        }else{
            Log.d(TAG, "UpdateMessage: input Message is Not empty");
            mMessageList.set(mMessageList.size()-1, message);
        }
        mMessageAdapter.notifyDataSetChanged();
        mMessageAdapter.notifyItemChanged(mMessageList.size()-1);
        //mMessageRecycler.scrollToPosition(mMessageList.size() - 1);
        mMessageRecycler.smoothScrollToPosition(mMessageList.size()-1);

        if(finish){
            inputingMessage = null;
        }
        return true;
    }

    public void Refresh(){
        Log.d(TAG, "Refresh: ");
        mMessageAdapter.notifyDataSetChanged();
    }


}
