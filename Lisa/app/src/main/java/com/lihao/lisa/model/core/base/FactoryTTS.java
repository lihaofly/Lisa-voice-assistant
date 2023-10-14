package com.lihao.lisa.model.core.base;

import android.util.Log;

import com.lihao.lisa.model.core.AISpeech.tts.AISpeechTTS;
import com.lihao.lisa.model.core.baidu.tts.BaiduTTS;
import com.lihao.lisa.model.core.iFlytek.tts.iFlytekTTS;

public class FactoryTTS {
    public static final String BAIDU_ONLINE_TTS = "BaiduOnlineTTS";
    public static final String IFLYTEK_ONLINE_TTS = "iFlytekOnlineTTS";
    public static final String AISPEECH_ONLINE_TTS = "AISPEECHOnlineTTS";
    private static final String TAG = "FactoryTTS";

    public static BaseTTS CreateTTS(String ttsType){
        BaseTTS tts = null;
        if(ttsType == null){
            tts = null;
        }

        if(ttsType.equalsIgnoreCase(BAIDU_ONLINE_TTS)){
            tts = BaiduTTS.getInstance();
        }else if(ttsType.equalsIgnoreCase(IFLYTEK_ONLINE_TTS)){
            tts = iFlytekTTS.getInstance();
        }else if(ttsType.equalsIgnoreCase(AISPEECH_ONLINE_TTS)){
            tts = AISpeechTTS.getInstance();
        }else{
            Log.e(TAG, "CreateTTS: Not support type");
        }

        return tts;
    }

}
