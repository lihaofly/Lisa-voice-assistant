package com.lihao.lisa.model.core.base;

import android.content.Context;
import android.os.Handler;

import com.lihao.lisa.model.core.BaiduSolution.util.BaiduException;
import com.lihao.lisa.util.MyLogger;

import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
/**
 * @ClassName BaseSynthesizer
 * @Author lihao.fly@163.com
 * @Data 2023/11/06
 * @Description Base class of Synthesizer. The synthesizer engine need to inherit from this class
 */
public class BaseSynthesizer {
    private static final String TAG = BaseSynthesizer.class.getSimpleName();
    protected String mTTSPrompter;
    protected String mTTSVolume;
    protected String mTTSSpeed;
    protected String mTTSPitch;
    protected String mTTSMode;
    protected int mMaxTTSLength;
    protected Context mContext;
    protected Handler mHandler;

    public BaseSynthesizer(){
        mTTSPrompter = null;
        mTTSVolume = null;
        mTTSSpeed = null;
        mTTSPitch = null;
        mTTSMode = null;
        mMaxTTSLength = 0;
        mContext = null;
        mHandler = null;
        MyLogger.debug(TAG, "BaseSynthesizer()");
    }

    public String GetTTSEngineName()
    {
        MyLogger.debug(TAG, "GetTTSEngineName()");
        return null;
    }

    public boolean InitializeTTS(Context context, Handler Handler) throws CertificateEncodingException, NoSuchAlgorithmException, BaiduException{
        MyLogger.debug(TAG, "InitializeTTS()");
        return false;
    }

    public boolean StartSpeak(String ttsStr){
        MyLogger.debug(TAG, "StartSpeak()");
        return false;
    }

    public boolean StopSpeak(){
        MyLogger.debug(TAG, "StopSpeak()");
        return false;
    }

    public boolean Destroy(){
        MyLogger.debug(TAG, "Destroy()");
        return false;
    }

    protected boolean loadConfig(){
        MyLogger.debug(TAG, "loadConfig()");
        return false;
    }
}
