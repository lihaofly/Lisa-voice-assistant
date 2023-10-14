package com.lihao.lisa.view.messageItems.holder;

import android.animation.Animator;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.lihao.lisa.R;
import com.lihao.lisa.util.BaseMessage;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class SentMessageHolder extends RecyclerView.ViewHolder {
    TextView messageText, timeText, dateText;
    LottieAnimationView msgLoading;

    public SentMessageHolder(View itemView) {
        super(itemView);
        dateText = (TextView) itemView.findViewById(R.id.text_gchat_date_me);
        messageText = (TextView) itemView.findViewById(R.id.text_gchat_message_me);
        timeText = (TextView) itemView.findViewById(R.id.text_gchat_timestamp_me);
        msgLoading = (LottieAnimationView) itemView.findViewById(R.id.message_loading_user);

    }

    public void bind(BaseMessage message) {
        messageText.setText(message.getShowMessage());

        // Format the stored timestamp into a readable String using method.
        SimpleDateFormat format =new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
        String time = format.format(message.getCreatedAt());
        timeText.setText(time);

        SimpleDateFormat formatDate =new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = formatDate.format(message.getCreatedAt());
        dateText.setText(date);

        updateLoadingAnimation();
    }


    private void updateLoadingAnimation() {
        if (!msgLoading.isAnimating()) {
            msgLoading.setRepeatCount(2);
            msgLoading.playAnimation();

            msgLoading.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    msgLoading.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    msgLoading.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                    //msgLoading.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                    //msgLoading.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}