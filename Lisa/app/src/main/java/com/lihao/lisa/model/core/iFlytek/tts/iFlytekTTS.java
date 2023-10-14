package com.lihao.lisa.model.core.iFlytek.tts;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.widget.EditText;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.msc.util.FileUtil;
import com.iflytek.cloud.msc.util.log.DebugLog;
import com.lihao.lisa.model.core.baidu.tts.listener.MessageListener;
import com.lihao.lisa.model.core.base.BaseTTS;
import com.lihao.lisa.util.BaseMessage;
import com.lihao.lisa.util.TTSMessage;

import java.io.IOException;

import static com.lihao.lisa.util.BaseMessage.RESULT_SUCCESS;
import static com.lihao.lisa.util.TTSMessage.TTS_STATUS_INPUT_TEXT_FINISHED;

public class iFlytekTTS extends BaseTTS {
    private static final String TAG = "iFlytekTTS";
    private static final String APP_ID = "f1f23085";

    private Context mContext;
    private Handler mHandler;
    private SpeechSynthesizer mTts;
    private static iFlytekTTS mInstance = null;

    private iFlytekTTS() {
        super();
    }

    public static iFlytekTTS getInstance(){
        if(mInstance == null){
            synchronized (iFlytekTTS.class){
                if(mInstance == null){
                    mInstance = new iFlytekTTS();
                }
            }
        }
        return mInstance;
    }

    @Override
    public String GetTTSEngineName(){
        return "iFlytek";
    }

    @Override
    public boolean InitializeTTS(Context context, Handler Handler) {
        this.mContext = context;
        this.mHandler = Handler;
        SpeechUtility.createUtility(mContext, "appid=" + APP_ID);
        mTts = SpeechSynthesizer.createSynthesizer(mContext, mTtsInitListener);
        if(mTts == null){
            Log.e(TAG, "InitializeTTS: Error");
        }
        return true;
    }


    @Override
    public boolean StartSpeak(String ttsStr){
        //setParam();
        int code = mTts.startSpeaking(ttsStr, mTtsListener);
//			/**
//			 * 只保存音频不进行播放接口,调用此接口请注释startSpeaking接口
//			 * text:要合成的文本，uri:需要保存的音频全路径，listener:回调接口
//			*/
        String path = Environment.getExternalStorageDirectory()+"/tts.pcm";
        //	int code = mTts.synthesizeToUri(texts, path, mTtsListener);

        if (code != ErrorCode.SUCCESS) {
            //showTip("语音合成失败,错误码: " + code+",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
        }
        return true;
    }

    @Override
    public boolean StopSpeak(){
        mTts.stopSpeaking();
        return true;
    }

    @Override
    public boolean Destroy() {
        mTts.destroy();
        return true;
    }


    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Log.d(TAG, "初始化失败,错误码："+code+",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };


    /**
     * 参数设置
     * @return
     */
    private void setParam(){
//        // 清空参数
//        mTts.setParameter(SpeechConstant.PARAMS, null);
//        // 根据合成引擎设置相应参数
//        if(mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
//            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
//            //支持实时音频返回，仅在synthesizeToUri条件下支持
//            mTts.setParameter(SpeechConstant.TTS_DATA_NOTIFY, "1");
//            //	mTts.setParameter(SpeechConstant.TTS_BUFFER_TIME,"1");
//
//            // 设置在线合成发音人
//            mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
//            //设置合成语速
//            mTts.setParameter(SpeechConstant.SPEED, mSharedPreferences.getString("speed_preference", "50"));
//            //设置合成音调
//            mTts.setParameter(SpeechConstant.PITCH, mSharedPreferences.getString("pitch_preference", "50"));
//            //设置合成音量
//            mTts.setParameter(SpeechConstant.VOLUME, mSharedPreferences.getString("volume_preference", "50"));
//        }else {
//            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
//            mTts.setParameter(SpeechConstant.VOICE_NAME, "");
//
//        }
//
//        //设置播放器音频流类型
//        mTts.setParameter(SpeechConstant.STREAM_TYPE, mSharedPreferences.getString("stream_preference", "3"));
//        // 设置播放合成音频打断音乐播放，默认为true
//        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "false");
//
//        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
//        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "pcm");
//        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/tts.pcm");
    }


    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            //showTip("开始播放");
        }

        @Override
        public void onSpeakPaused() {
            //showTip("暂停播放");
        }

        @Override
        public void onSpeakResumed() {
            //showTip("继续播放");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // 合成进度
//            Log.e("MscSpeechLog_", "percent =" + percent);
//            mPercentForBuffering = percent;
//            showTip(String.format(getString(R.string.tts_toast_format),
//                    mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
//            Log.e("MscSpeechLog_", "percent =" + percent);
//            mPercentForPlaying = percent;
//            showTip(String.format(getString(R.string.tts_toast_format),
//                    mPercentForBuffering, mPercentForPlaying));
//
//            SpannableStringBuilder style=new SpannableStringBuilder(texts);
//            Log.e(TAG,"beginPos = "+beginPos +"  endPos = "+endPos);
//            style.setSpan(new BackgroundColorSpan(Color.RED),beginPos,endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            ((EditText) findViewById(R.id.tts_text)).setText(style);
        }

        @Override
        public void onCompleted(SpeechError error) {
            TTSMessage ttsMsg = new TTSMessage(RESULT_SUCCESS, TTS_STATUS_INPUT_TEXT_FINISHED);
            ttsMsg.setmProgress(10);
            sendMessage(ttsMsg, BaseMessage.TTS_MESSAGE);
        }


        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
//            //	 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
//            //	 若使用本地能力，会话id为null
//            if (SpeechEvent.EVENT_SESSION_ID == eventType) {
//                String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
//                Log.d(TAG, "session id =" + sid);
//            }
//
//            //当设置SpeechConstant.TTS_DATA_NOTIFY为1时，抛出buf数据
//            if (SpeechEvent.EVENT_TTS_BUFFER == eventType) {
//                byte[] buf = obj.getByteArray(SpeechEvent.KEY_EVENT_TTS_BUFFER);
//                Log.e("MscSpeechLog_", "bufis =" + buf.length);
//                container.add(buf);
//            }
        }
    };


    protected void sendMessage(TTSMessage obj, int action) {
        if (mHandler != null) {
            Message msg = Message.obtain();
            msg.what = action;
            msg.obj = obj;
            mHandler.sendMessage(msg);
        }
    }
}
