package com.lihao.lisa.model.core.BaiduSolution.tts;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.lihao.lisa.model.core.BaiduSolution.tts.listener.BaiduTTSListener;
import com.lihao.lisa.model.core.BaiduSolution.tts.util.AutoCheck;
import com.lihao.lisa.model.core.BaiduSolution.tts.util.InitConfig;
import com.lihao.lisa.model.core.BaiduSolution.util.BaiduException;
import com.lihao.lisa.model.core.base.BaseSynthesizer;
import com.lihao.lisa.model.util.config.Config;
import com.lihao.lisa.util.MyLogger;

import android.content.Context;

import java.io.File;


/**
 * @ClassName BaiduSynthesizer
 * @Author lihao.fly@163.com
 * @Data 2023/10/23
 * @Description Used to create Synthesizer instance and provide Synthesizer feature.
 */
public class BaiduSynthesizer extends BaseSynthesizer {
    private static final String TAG = BaiduSynthesizer.class.getSimpleName();
    private static BaiduSynthesizer mInstance = null;

    //The Synthesizer instance from engine
    protected SpeechSynthesizer mSpeechSynthesizer;
    //The listener used to get synthesizer engine status
    private BaiduTTSListener mBaiduTtsListener;

    //For authority - start
    private String mAppId;
    private String mAppKey;
    private String mSecretKey;
    private String mSN;
    //For authority - end

    private String mTextModePath;
    private String mVoiceModePath;
    private String mResourcePath;

    private String mMixMode = SpeechSynthesizer.MIX_MODE_DEFAULT;
    private TtsMode mBaiduTTSMode;
    private boolean mIsOnlineMode = false;

    private BaiduSynthesizer(){
        super();
    }

    public static BaiduSynthesizer getInstance(){
        if( mInstance == null ){
            synchronized (BaiduSynthesizer.class){
                if( mInstance == null ){
                    mInstance = new BaiduSynthesizer();
                }
            }
        }
        return mInstance;
    }

    @Override
    public String GetTTSEngineName(){
        return "baidu";
    }

    /**
     * Initialize TTS engine
     * Should called in new Thread and this thread can Not be terminate
     */
    @Override
    public boolean InitializeTTS(Context context, Handler Handler) throws BaiduException {
        boolean isSuccess;
        if(null == context)
        {
            MyLogger.error(TAG, "context is not allowed to null");
            throw new BaiduException("Are you calling with null context ?");
        }else if (Thread.currentThread().getId() == Looper.getMainLooper().getThread().getId()) {
            //MyLogger.error(TAG, "This api should not be called in main thread");
            //throw new BaiduException("Are you calling the method on the main thread?");
        }
        mContext = context;
        loadConfig();
        LoggerProxy.printable(true);

        mBaiduTtsListener = new BaiduTTSListener(Handler);

        //For offline mode to check the resource file
        if (!checkOfflineResources()) {
            return false;
        }

        //1. Get Instance
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(mContext);

        //2. Set the listener
        mSpeechSynthesizer.setSpeechSynthesizerListener(mBaiduTtsListener);

        //3. Set appId, appKey, secretKey
        int result = mSpeechSynthesizer.setAppId(mAppId);
        checkResult(result, "setAppId");
        result = mSpeechSynthesizer.setApiKey(mAppKey, mSecretKey);
        checkResult(result, "setApiKey");

        //4. Set resource file path
        // Need to load resource file for offline and mix mode
        if (!mIsOnlineMode) {
            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, mTextModePath);
            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mVoiceModePath);
            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_AUTH_SN, mSN);
            if(mBaiduTTSMode == TtsMode.MIX) {
                mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, mMixMode);
            }
        }
        //4. Set Prompter, Volume, Speed, Pitch
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, mTTSPrompter);
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, mTTSVolume);
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, mTTSSpeed);
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, mTTSPitch);
        // mSpeechSynthesizer.setAudioStreamType(AudioManager.MODE_IN_CALL); //Set Audio Output

        //5. Check the parameter. unmark in debug phase.
        //checkParameters(mAppId, mAppKey, mSecretKey, mTextModePath,mVoiceModePath, mBaiduTTSMode, mMsgListener);

        //6. init TTS
        result = mSpeechSynthesizer.initTts(mBaiduTTSMode);
        checkResult(result, "InitializeTTS");
        return true;
    }

    @Override
    public boolean StartSpeak(String ttsStr) {
        MyLogger.debug(TAG, "StartSpeak: ttsStr: "+ ttsStr + " length: " + ttsStr.length());
        if (ttsStr.equals("")) {
            MyLogger.warning(TAG, "StartSpeak: TTS is null");
            return false;
        }

        if (mSpeechSynthesizer == null) {
            MyLogger.error(TAG, "StartSpeak: Initialize failed");
            return false;
        }

        if(ttsStr.length() > mMaxTTSLength){
           ttsStr = ttsStr.substring(0,mMaxTTSLength);
            MyLogger.warning(TAG, "StartSpeak: TTS be cut. The support max length is: "
                    + mMaxTTSLength);
        }

        int result = mSpeechSynthesizer.speak(ttsStr);
        MyLogger.debug(TAG, "StartSpeak: Start to Play TTS");
        checkResult(result, "StartSpeak");
        return true;
    }

    @Override
    public boolean StopSpeak() {
        MyLogger.debug(TAG, "StopSpeak: Stop Speak");
        if (mSpeechSynthesizer == null) {
            MyLogger.error(TAG, "StopSpeak: Initialize failed");
            return false;
        }
        int result = mSpeechSynthesizer.stop();
        checkResult(result, "StopSpeak");
        return true;
    }

    @Override
    public boolean Destroy() {
        int result = 0;
        if (mSpeechSynthesizer != null) {
            result = mSpeechSynthesizer.stop();
            result = mSpeechSynthesizer.release();
            mSpeechSynthesizer = null;
            MyLogger.debug(TAG, "onDestroy: Release TTS resource");
        }
        checkResult(result, "Destroy");
        return true;
    }

    /**
     * Load the TTS prompt configurations from assert config file: Config.properties
     */
    @Override
    protected boolean loadConfig(){
        if(mContext != null) {
            mAppId = Config.getInstance(mContext).getConfigItem("BAIDU_AUTHORITY_APPID");
            mAppKey = Config.getInstance(mContext).getConfigItem("BAIDU_AUTHORITY_APPKEY");
            mSecretKey = Config.getInstance(mContext).getConfigItem("BAIDU_AUTHORITY_SECRETKEY");
            mSN = Config.getInstance(mContext).getConfigItem("BAIDU_AUTHORITY_SN");
            MyLogger.debug(TAG, "BaiduTTS: appID: " + mAppId +
                    " BaiduTTS: appkey: " + mAppKey +
                    " BaiduTTS: secretKey: " + mSecretKey +
                    " BaiduTTS: sn: " + mSN);
            mMaxTTSLength = Integer.parseInt(Config.getInstance(mContext).getConfigItem("BAIDU_TTS_MAX_LENGTH"));

            mResourcePath = Config.getInstance(mContext).getConfigItem("BAIDU_TTS_RESOURCE_PATH");
            mTextModePath = mResourcePath + Config.getInstance(mContext).getConfigItem("BAIDU_TTS_TEXT_MODEL_NAME");
            MyLogger.debug(TAG," mTextModePath: " + mTextModePath);
            String seletedPrompter = Config.getInstance(mContext).getConfigItem("BAIDU_TTS_SELECTED_PROMPTER");
            MyLogger.debug(TAG,"seletedPrompter: " + seletedPrompter);
            mVoiceModePath = mResourcePath + Config.getInstance(mContext).getConfigItem(seletedPrompter);
            MyLogger.debug(TAG," mVoiceModePath: " + mVoiceModePath);
            mTTSMode = Config.getInstance(mContext).getConfigItem("BAIDU_TTS_MODE");
            MyLogger.debug(TAG,"ttsMode: " + mTTSMode);

            if(mTTSMode.equals("TTS_MODE_ONLINE")){
                mBaiduTTSMode = TtsMode.ONLINE;
                mIsOnlineMode = true;
            }else if(mTTSMode.equals("TTS_MODE_OFFLINE")){
                mBaiduTTSMode = TtsMode.OFFLINE;
                mIsOnlineMode = false;
            }else if(mTTSMode.equals("TTS_MODE_MIX")){
                mBaiduTTSMode = TtsMode.MIX;
                mIsOnlineMode = false;
            }else{
                mBaiduTTSMode = TtsMode.ONLINE; //Default value
                MyLogger.error(TAG,"TTS Mode configuration error");
            }

            String mixMode = Config.getInstance(mContext).getConfigItem("BAIDU_TTS_MIX_MODE");
            if(mixMode.equals("MIX_MODE_DEFAULT")){
                mMixMode = SpeechSynthesizer.MIX_MODE_DEFAULT;
            }else if(mixMode.equals("MIX_MODE_WIFI")){
                mMixMode = SpeechSynthesizer.MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI;
            }else if(mixMode.equals("MIX_MODE_NETWORK")){
                mMixMode = SpeechSynthesizer.MIX_MODE_HIGH_SPEED_NETWORK;
            }else if(mixMode.equals("MIX_MODE_SYNTHESIZE")){
                mMixMode = SpeechSynthesizer.MIX_MODE_HIGH_SPEED_SYNTHESIZE;
            }else{
                MyLogger.error(TAG,"TTS Mix Mode configuration error");
            }

            mTTSPrompter = Config.getInstance(mContext).getConfigItem("BAIDU_TTS_PROMPTER");
            mTTSVolume = Config.getInstance(mContext).getConfigItem("BAIDU_TTS_VOLUME");
            mTTSSpeed = Config.getInstance(mContext).getConfigItem("BAIDU_TTS_SPEED");
            mTTSPitch = Config.getInstance(mContext).getConfigItem("BAIDU_TTS_PITCH");
            return true;
        }else{
            return false;
        }
    }

    private void checkResult(int result, String method) {
        if (result != 0) {
            MyLogger.debug(TAG, "checkResult: error code :"+ result + " method:" + method);
        }
    }

    /**
     * Check if the resource file in right path, Not applicable for Online mode.
     *
     * @return true: resource file valid, false: resource file invalid
     */
    private boolean checkOfflineResources() {
        boolean result = true;
        if(!mIsOnlineMode) {
            String[] filenames = {mTextModePath, mVoiceModePath};
            for (String path : filenames) {
                MyLogger.debug(TAG, "Path: " + path);
                File f = new File(path);
                if (!f.canRead()) {
                    MyLogger.error(TAG, "resource file invalid or no read permission"
                            + f.getAbsolutePath());
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Check the validation of parameters. Can enable in debug phase.
     */
    private void checkParameters(String appId,
                                 String appKey,
                                 String secretKey,
                                 String textModePath,
                                 String voiceModePath,
                                 TtsMode ttsMode,
                                 SpeechSynthesizerListener listener){

        InitConfig initConfig = new InitConfig(appId, appKey, secretKey,textModePath,voiceModePath,ttsMode,listener);
        AutoCheck.getInstance(mContext).check(initConfig, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 100) {
                    AutoCheck autoCheck = (AutoCheck) msg.obj;
                    synchronized (autoCheck) {
                        String message = autoCheck.obtainDebugMessage();
                        MyLogger.debug("AutoCheckMessage", "\n" + message);
                    }
                }
            }

        });
    }
}
