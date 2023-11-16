package com.lihao.lisa.model.core.iFlytek.tts;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.os.Handler;
import android.text.TextUtils;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.msc.util.log.DebugLog;
import com.iflytek.cloud.util.ResourceUtil;
import com.iflytek.cloud.util.ResourceUtil.RESOURCE_TYPE;
import com.lihao.lisa.model.core.base.BaseSynthesizer;
import com.lihao.lisa.model.core.iFlytek.tts.listener.iFlytekTTSListener;
import com.lihao.lisa.model.util.config.Config;
import com.lihao.lisa.util.MyLogger;

/**
 * @ClassName iFlytekSynthesizer
 * @Author lihao.fly@163.com
 * @Data 2023/10/26
 * @Description Used to create Synthesizer instance and provide Synthesizer feature.
 */
public class iFlytekSynthesizer extends BaseSynthesizer {
    private static final String TAG = iFlytekSynthesizer.class.getSimpleName();
    private static iFlytekSynthesizer mInstance = null;

    //The Synthesizer instance from engine
    private SpeechSynthesizer mSpeechSynthesizer;
    //The listener used to get synthesizer engine status
    private iFlytekTTSListener mIFlytekTtsListener;

    //For authority - start
    private String mAppID;
    //For authority - end

    private String mTTSStreamType;
    private String mTTSRequestFocus;
    private String mTTSDumpType;
    private String mTTSDumpPath;
    private String mIsAudioDataNotify;

    private iFlytekSynthesizer() {
        super();
    }

    public static iFlytekSynthesizer getInstance(){
        if(mInstance == null){
            synchronized (iFlytekSynthesizer.class){
                if(mInstance == null){
                    mInstance = new iFlytekSynthesizer();
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
        loadConfig();
        SpeechUtility.createUtility(mContext, "appid=" + mAppID);
        mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(mContext, mTtsInitListener);
        if(mSpeechSynthesizer == null){
            MyLogger.error(TAG, "InitializeTTS: Error");
        }else{
            mIFlytekTtsListener = new iFlytekTTSListener(mHandler);

            setParam();
        }
        return true;
    }

    @Override
    public boolean StartSpeak(String ttsStr){
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

        int result = mSpeechSynthesizer.startSpeaking(ttsStr, mIFlytekTtsListener);
        checkResult(result, "StartSpeak");
        return true;
    }

    @Override
    public boolean StopSpeak(){
        MyLogger.debug(TAG, "StopSpeak: Stop Speak");
        if (mSpeechSynthesizer == null) {
            MyLogger.error(TAG, "StopSpeak: Initialize failed");
            return false;
        }
        mSpeechSynthesizer.stopSpeaking();
        return true;
    }

    @Override
    public boolean Destroy() {
        MyLogger.debug(TAG, "Destroy");
        if (mSpeechSynthesizer == null) {
            MyLogger.error(TAG, "Destroy: Initialize failed");
            return false;
        }
        mSpeechSynthesizer.destroy();
        return true;
    }

    /**
     * Load the TTS prompt configurations from assert config file: Config.properties
     */
    @Override
    protected boolean loadConfig(){
        if(mContext != null) {
            mAppID = Config.getInstance(mContext).getConfigItem("IFLYTEK_AUTHORITY_APPID");
            mTTSMode = Config.getInstance(mContext).getConfigItem("IFLYTEK_TTS_MODE");
            mTTSPrompter = Config.getInstance(mContext).getConfigItem("IFLYTEK_TTS_PROMPTER");
            mTTSVolume = Config.getInstance(mContext).getConfigItem("IFLYTEK_TTS_VOLUME");
            mTTSSpeed = Config.getInstance(mContext).getConfigItem("IFLYTEK_TTS_SPEED");
            mTTSPitch = Config.getInstance(mContext).getConfigItem("IFLYTEK_TTS_PITCH");
            mTTSStreamType = Config.getInstance(mContext).getConfigItem("IFLYTEK_TTS_STREAM_TYPE");
            mTTSRequestFocus = Config.getInstance(mContext).getConfigItem("IFLYTEK_TTS_REQUEST_FOCUS");
            mTTSDumpType = Config.getInstance(mContext).getConfigItem("IFLYTEK_TTS_DUMP_TYPE");
            mTTSDumpPath = Config.getInstance(mContext).getConfigItem("IFLYTEK_TTS_DUMP_DATA_PATH");
            mIsAudioDataNotify = Config.getInstance(mContext).getConfigItem("IFLYTEK_TTS_DATA_NOTIFY");
            mMaxTTSLength =Integer.parseInt(Config.getInstance(mContext).getConfigItem("IFLYTEK_TTS_MAX_LENGTH"));
            return true;
        }else{
            return false;
        }
    }

    /**
     * Set listener of synthesizer initialize.
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            MyLogger.debug(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                MyLogger.error(TAG, "Initialize failed, Error code: "+code+",Refer https://www.xfyun.cn/document/error-code");
            } else {
                //Start Speaking must called after received this listener.
                MyLogger.debug(TAG, "Initialize successful");
            }
        }
    };

    /**
     * Set parameters of TTS engine
     */
    private void setParam(){
        //Clear parameters
        mSpeechSynthesizer.setParameter(SpeechConstant.PARAMS, null);

        if(mTTSMode.equals("TTS_MODE_ONLINE")) {
            mSpeechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            mSpeechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, mTTSPrompter);
        }else if(mTTSMode.equals("TTS_MODE_OFFLINE")){
            mSpeechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            mSpeechSynthesizer.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath());
            mSpeechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, mTTSPrompter);
        }else if(mTTSMode.equals("TTS_MODE_XTTS")){
            mSpeechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_XTTS);
            mSpeechSynthesizer.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath());
            mSpeechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, mTTSPrompter);
        }else{
            MyLogger.error(TAG,"Not support TTS mode");
            return;
        }

//        //支持实时音频返回，仅在synthesizeToUri条件下支持
//        mSpeechSynthesizer.setParameter(SpeechConstant.TTS_DATA_NOTIFY, mIsAudioDataNotify);
//        //mTts.setParameter(SpeechConstant.TTS_BUFFER_TIME,"1");

        //Set tts volume
        mSpeechSynthesizer.setParameter(SpeechConstant.VOLUME, mTTSVolume);
        //Set tts speed
        mSpeechSynthesizer.setParameter(SpeechConstant.SPEED, mTTSSpeed);
        //Set tts pitch
        mSpeechSynthesizer.setParameter(SpeechConstant.PITCH, mTTSPitch);

        //Set tts audio stream type
        mSpeechSynthesizer.setParameter(SpeechConstant.STREAM_TYPE, mTTSStreamType);

        //Set If will interrupt media player, Default value is true
        mSpeechSynthesizer.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, mTTSRequestFocus);

        //Set TTS audio dump data type and path
        mSpeechSynthesizer.setParameter(SpeechConstant.AUDIO_FORMAT, mTTSDumpType);
        mSpeechSynthesizer.setParameter(SpeechConstant.TTS_AUDIO_PATH, mTTSDumpPath);
    }

    private void checkResult(int result, String method) {
        if (result != ErrorCode.SUCCESS) {
            MyLogger.debug(TAG, "checkResult: error code :"+ result + " method:" + method);
        }
    }

    private String getResourcePath() {
        StringBuffer tempBuffer = new StringBuffer();
        String type = "tts";
        if (mTTSMode.equals("TTS_MODE_XTTS")) {
            type = "xtts";
        }
        //合成通用资源
        tempBuffer.append(generateResourcePath(mContext, type + "/common.jet"));
        tempBuffer.append(";");
        //发音人资源
        tempBuffer.append(generateResourcePath(mContext, type + "/" + mTTSPrompter + ".jet"));
        MyLogger.debug(TAG,"getResourcePath() path: " + tempBuffer.toString());

        return tempBuffer.toString();
    }


    private static String generateResourcePath(Context context,String filePath) {
        if (!TextUtils.isEmpty(filePath) && context != null) {
            String packagePath = context.getPackageResourcePath();
            MyLogger.debug(TAG,"Package resource path packagePath: " + packagePath);
            MyLogger.debug(TAG,"Package resource path filePath: " + filePath);
            AssetFileDescriptor assetFileDescriptor = null;
            String resourcePath = null;
            long offset = 0L;
            long length = 0L;

            try {
                assetFileDescriptor = context.getAssets().openFd(filePath);
                offset = assetFileDescriptor.getStartOffset();
                MyLogger.debug(TAG,"Package resource path offset: " + offset);
                length = assetFileDescriptor.getLength();
                MyLogger.debug(TAG,"Package resource path length: " + length);

                resourcePath = "fo|" + packagePath + "|" + offset + "|" + length;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (assetFileDescriptor != null) {
                        assetFileDescriptor.close();
                        assetFileDescriptor = null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return resourcePath;
        } else {
            return null;
        }
    }
}
