package com.lihao.lisa.view.messageItems.EuropeCup;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lihao.lisa.R;
import com.lihao.lisa.model.features.InformationSearch.EuropeCupBean.ResultBean.DataBean;

import java.util.List;

public class ECScheduleList {
    private static final String TAG = "ECScheduleList";
    private RecyclerView mMessageRecycler;
    private ECScheduleAdapter mMessageAdapter;

    public ECScheduleList(Context context, RecyclerView mMessageRecycler, DataBean scheduleDataBean) {
        this.mMessageRecycler = mMessageRecycler;
        this.mMessageAdapter = new ECScheduleAdapter(context, scheduleDataBean);
        this.mMessageRecycler.setAdapter(mMessageAdapter);
        this.mMessageRecycler.setLayoutManager(new LinearLayoutManager(context));
    }

    public void NotifyChange(){
        this.mMessageAdapter.notifyDataSetChanged();
    }




    public class ECScheduleHoder extends RecyclerView.ViewHolder{
        private TextView match_time, match_type,teama_name,teamb_name,teama_score,teamb_score;
        private ImageView teamaLogo,teambLogo;
        private Context mContext;

        public ECScheduleHoder(View itemView, Context context) {
            super(itemView);
            mContext = context;
            match_time = (TextView)itemView.findViewById(R.id.match_time);
            match_type = (TextView)itemView.findViewById(R.id.match_type);
            teama_name = (TextView)itemView.findViewById(R.id.team1);
            teamb_name = (TextView)itemView.findViewById(R.id.team2);
            teama_score = (TextView)itemView.findViewById(R.id.team1_score);
            teamb_score = (TextView)itemView.findViewById(R.id.team2_score);
            teamaLogo = (ImageView)itemView.findViewById(R.id.team1_country);
            teambLogo = (ImageView)itemView.findViewById(R.id.team2_country);
        }

        public void bind(DataBean.ScheduleListBean schListBean) {
            String dateTime = schListBean.getDate_time();
            String time = dateTime.substring(11,dateTime.length());
            String showTimeStr = String.format("比赛时间：%s",time);
            match_time.setText(showTimeStr);
            match_type.setText(schListBean.getMatch_type_des());
            teama_name.setText(schListBean.getTeama_name());
            teamb_name.setText(schListBean.getTeamb_name());
            teama_score.setText(schListBean.getTeama_score());
            teamb_score.setText(schListBean.getTeamb_score());

            if(schListBean.getTeama_logo_url() != null) {
                Glide.with(mContext).load(schListBean.getTeama_logo_url()).into(teamaLogo);
            }
            if(schListBean.getTeama_logo_url() != null) {
                Glide.with(mContext).load(schListBean.getTeamb_logo_url()).into(teambLogo);
            }


        }
    }


    public class ECScheduleAdapter extends RecyclerView.Adapter {
        private static final int VIEW_TYPE_MESSAGE_DEFAULT = 0;
        private static final int VIEW_TYPE_MESSAGE_SCHEDULE = 1;
        private static final String TAG = "ECScheduleAdapter";

        private Context mContext;
        private DataBean mECDataBean;

        public ECScheduleAdapter(Context context, DataBean europeCupDataBean) {
            this.mContext = context;
            this.mECDataBean = europeCupDataBean;
        }

        @Override
        public int getItemCount() {
            return mECDataBean.getSchedule_list().size();
        }

        @Override
        public int getItemViewType(int position) {
            return VIEW_TYPE_MESSAGE_SCHEDULE;
        }

        // Inflates the appropriate layout according to the ViewType.
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;

            if (viewType == VIEW_TYPE_MESSAGE_SCHEDULE) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_europe_cup_items, parent, false);
                return new ECScheduleHoder(view, mContext);
            }
            return null;
        }

        // Passes the message object to a ViewHolder so that the contents can be bound to UI.
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            List<DataBean.ScheduleListBean> scheduleListBean = mECDataBean.getSchedule_list();
            switch (holder.getItemViewType()) {
                case VIEW_TYPE_MESSAGE_SCHEDULE:
                    ((ECScheduleHoder) holder).bind(scheduleListBean.get(position));
                    break;
                default:
                    Log.w(TAG, "onBindViewHolder: Not Supported");
                    break;
            }
        }

    }

}
