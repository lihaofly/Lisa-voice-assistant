package com.lihao.lisa.view.messageItems.holder;

import android.animation.Animator;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.lihao.lisa.R;
import com.lihao.lisa.util.BaseMessage;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class GuideMessageHolder extends RecyclerView.ViewHolder {
    TextView messageText, timeText, nameText, dateText;
    ImageView profileImage;
    LottieAnimationView msgLoading;

    public GuideMessageHolder(View itemView) {
        super(itemView);
        messageText = (TextView) itemView.findViewById(R.id.text_gchat_message_other);
        timeText = (TextView) itemView.findViewById(R.id.text_gchat_timestamp_other);
        nameText = (TextView) itemView.findViewById(R.id.text_gchat_user_other);
        profileImage = (ImageView) itemView.findViewById(R.id.image_gchat_profile_other);
        dateText = (TextView) itemView.findViewById(R.id.text_gchat_split_line);
        msgLoading = (LottieAnimationView) itemView.findViewById(R.id.message_loading);
    }

    public void bind(BaseMessage message) {
        messageText.setText(message.getShowMessage());

        // Format the stored timestamp into a readable String using method.
        SimpleDateFormat formatTime =new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
        String time = formatTime.format(message.getCreatedAt());

        timeText.setText(time);
        nameText.setText(message.getUser().getNickname());

        SimpleDateFormat formatDate =new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = formatDate.format(message.getCreatedAt());
        dateText.setText(date);

        // Insert the profile image from the URL into the ImageView.
        //Utils.displayRoundImageFromUrl(mContext, message.getSender().getProfileUrl(), profileImage);
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