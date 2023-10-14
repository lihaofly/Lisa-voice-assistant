package com.lihao.lisa.view;

import android.os.Message;

import com.lihao.lisa.presenter.LisaPresenter;
import com.lihao.lisa.util.TTSMessage;

public interface LisaView extends BaseView<LisaPresenter>{
    void showReady(Message msg);
    void showSpeaking(Message msg);
    void showRecognition(Message msg);
    void showFinished(Message msg);
    void showExit(Message msg);
    void showResult(Message msg);
    void showTTSPlaying(TTSMessage msg);
}
