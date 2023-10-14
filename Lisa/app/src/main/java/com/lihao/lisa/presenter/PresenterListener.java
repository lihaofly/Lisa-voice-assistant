package com.lihao.lisa.presenter;

import android.os.Message;

import com.lihao.lisa.util.TTSMessage;

public interface PresenterListener {
    void onShowReady(Message msg);
    void onShowSpeaking(Message msg);
    void onShowRecognition(Message msg);
    void onShowFinished(Message msg);
    void onShowResult(Message msg);
    void onShowExit(Message msg);
    void onShowTTSPlaying(TTSMessage msg);
}
