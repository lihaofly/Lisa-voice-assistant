package com.lihao.lisa.model.core.base;

import android.util.Log;

import com.lihao.lisa.model.core.AISpeech.tts.AISpeechSynthesizer;
import com.lihao.lisa.model.core.BaiduSolution.tts.BaiduSynthesizer;
import com.lihao.lisa.model.core.iFlytek.tts.iFlytekSynthesizer;

public class FactoryTTS {
    public static final String BAIDU_TTS = "BaiduTTS";
    public static final String IFLYTEK_TTS = "iFlytekTTS";
    public static final String AISPEECH_TTS = "AISpeechTTS";
    private static final String TAG = "FactoryTTS";

    public static BaseSynthesizer CreateTTS(String ttsType){
        BaseSynthesizer tts = null;
        if(ttsType == null){
            tts = null;
        }

        if(ttsType.equalsIgnoreCase(BAIDU_TTS)){
            tts = BaiduSynthesizer.getInstance();
        }else if(ttsType.equalsIgnoreCase(IFLYTEK_TTS)){
            tts = iFlytekSynthesizer.getInstance();
        }else if(ttsType.equalsIgnoreCase(AISPEECH_TTS)){
            tts = AISpeechSynthesizer.getInstance();
        }else{
            Log.e(TAG, "CreateTTS: Not support type");
        }

        return tts;
    }

}
