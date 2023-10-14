package com.lihao.lisa.view.messageItems.EuropeCup;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lihao.lisa.R;
import com.lihao.lisa.model.features.InformationSearch.EuropeCupBean.ResultBean;
import com.lihao.lisa.model.features.InformationSearch.EuropeCupBean.ResultBean.DataBean;

import java.util.List;

public class ECScheduleDateList {
    private RecyclerView mMessageRecycler;
    private ECScheduleDateAdapter mMessageAdapter;
    private Context mContext;

    public ECScheduleDateList(Context context, RecyclerView mMessageRecycler, ResultBean schResultBean) {
        this.mMessageRecycler = mMessageRecycler;
        this.mMessageAdapter = new ECScheduleDateAdapter(context, schResultBean);
        this.mMessageRecycler.setAdapter(mMessageAdapter);
        this.mMessageRecycler.setLayoutManager(new LinearLayoutManager(context));
    }

    public void NotifyChange(){
        this.mMessageAdapter.notifyDataSetChanged();
    }


    public class ECScheduleDateHolder extends RecyclerView.ViewHolder{
        private RecyclerView mScheduleDateList;
        private Context mContext;
        private TextView mScheduleDate;

        public ECScheduleDateHolder(View itemView, Context mContext) {
            super(itemView);
            mScheduleDateList = (RecyclerView)itemView.findViewById(R.id.recycler_europe_cup_date_List);
            mScheduleDate = (TextView) itemView.findViewById(R.id.schedule_Item_date);
            this.mContext = mContext;
        }

        public void bind(DataBean dataBean) {

            ECScheduleList EcschList = new ECScheduleList(mContext,mScheduleDateList,dataBean);
            String date = String.format("日期：%s",dataBean.getSchedule_date_format());
            mScheduleDate.setText(date);
            EcschList.NotifyChange();
        }
    }

    public class ECScheduleDateAdapter extends RecyclerView.Adapter {
        private static final int VIEW_TYPE_MESSAGE_SCHEDULE_DATE_DEFAULT = 0;
        private static final int VIEW_TYPE_MESSAGE_SCHEDULE_DATE_LIST = 1;
        private static final String TAG = "ECScheduleDateAdapter";

        private Context mContext;
        private ResultBean mEuropeCupBean;

        public ECScheduleDateAdapter(Context context, ResultBean resultBean) {
            this.mContext = context;
            this.mEuropeCupBean = resultBean;
        }

        @Override
        public int getItemCount() {
            int count = 0;
            List<DataBean> dataBean = mEuropeCupBean.getData();
            count = dataBean.size();
            return count;
        }

        @Override
        public int getItemViewType(int position) {
            return VIEW_TYPE_MESSAGE_SCHEDULE_DATE_LIST;
        }

        // Inflates the appropriate layout according to the ViewType.
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;

            if (viewType == VIEW_TYPE_MESSAGE_SCHEDULE_DATE_LIST) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_europe_cup_date_items, parent, false);
                return new ECScheduleDateHolder(view, mContext);
            }
            return null;
        }

        // Passes the message object to a ViewHolder so that the contents can be bound to UI.
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            List<DataBean> dataBean = mEuropeCupBean.getData();
            if (dataBean.size() > 0) {
                switch (holder.getItemViewType()) {
                    case VIEW_TYPE_MESSAGE_SCHEDULE_DATE_LIST:
                        ((ECScheduleDateHolder) holder).bind(dataBean.get(position));
                        break;
                    default:
                        Log.w(TAG, "onBindViewHolder: Not Supported");
                        break;
                }
            }
        }

    }



}
