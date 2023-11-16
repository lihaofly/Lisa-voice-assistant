package com.lihao.lisa.presenter;

import android.content.Context;
import android.os.Message;

import com.lihao.lisa.model.LisaMainState;
import com.lihao.lisa.util.TTSMessage;
import com.lihao.lisa.view.LisaView;

public class LisaPresenter implements BasePresenter, PresenterListener {
    private static final String TAG = "Lisa: LisaPresenter";
    LisaMainState mLisaMainState = null;
    LisaView mLisaView;
    Context mContext;

    public LisaPresenter(LisaView mListView) {
        this.mLisaView = mListView;
    }

    public boolean initializeModel(Context context){
        boolean result = false;
        mContext = context;
        mLisaMainState = LisaMainState.getInstance(context, this);
        mLisaMainState.setListener(this);
        //mLisaMainState = new LisaMainState("LisaMainState",context, this);
        mLisaMainState.setContext(context);
        if(mLisaMainState != null){
            mLisaMainState.sendMessage(mLisaMainState.obtainMessage(LisaMainState.EVENT_INIT));
            result = true;
        }

        return result;
    }

    public void startSpeak(String text){
        mLisaMainState.startSpeak(text);
    }

    public void startASR(){
        mLisaMainState.sendMessage(mLisaMainState.obtainMessage(LisaMainState.EVENT_PTT));
    }

    public void cancelASR(){
        mLisaMainState.sendMessage(mLisaMainState.obtainMessage(LisaMainState.EVENT_CANCEL));
    }

    public void stopEngine(){
        mLisaMainState.sendMessage(mLisaMainState.obtainMessage(LisaMainState.EVENT_STOP));
    }

    public void ChangeTTSEngine(String text){
        mLisaMainState.ChangeTTSEngine(text);

    }


    //The follow is the callback from model
    @Override
    public void onDestroy() {

    }

    @Override
    public void onShowReady(Message msg) {
        mLisaView.showReady(msg);
    }

    @Override
    public void onShowSpeaking(Message msg) {
        mLisaView.showSpeaking(msg);
    }

    @Override
    public void onShowRecognition(Message msg) {
        mLisaView.showRecognition(msg);
    }

    @Override
    public void onShowFinished(Message msg) {
        mLisaView.showFinished(msg);
    }

    @Override
    public void onShowResult(Message msg) {
        mLisaView.showResult(msg);
    }
    @Override
    public void onShowExit(Message msg) {
        mLisaView.showExit(msg);
    }

    @Override
    public void onShowTTSPlaying(TTSMessage msg) {
        mLisaView.showTTSPlaying(msg);
    }
}
