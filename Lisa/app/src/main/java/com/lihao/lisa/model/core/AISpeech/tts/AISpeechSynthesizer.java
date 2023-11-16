package com.lihao.lisa.model.core.AISpeech.tts;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Environment;
import android.os.Handler;

import com.aispeech.AIEchoConfig;
import com.aispeech.DUILiteConfig;
import com.aispeech.DUILiteSDK;
import com.aispeech.DUILiteSDK.InitListener;
import com.aispeech.export.config.AICloudTTSConfig;
import com.aispeech.export.config.AILocalTTSConfig;
import com.aispeech.export.engines2.AICloudTTSEngine;
import com.aispeech.export.engines2.AILocalTTSEngine;
import com.aispeech.export.intent.AICloudTTSIntent;
import com.aispeech.export.intent.AILocalTTSIntent;
import com.aispeech.lite.AISampleRate;
import com.lihao.lisa.model.core.AISpeech.tts.listener.AISpeechTTSCloudListener;
import com.lihao.lisa.model.core.AISpeech.tts.listener.AISpeechTTSLocalListener;
import com.lihao.lisa.model.core.base.BaseSynthesizer;
import com.lihao.lisa.model.util.config.Config;
import com.lihao.lisa.util.MyLogger;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * @ClassName AISpeechSynthesizer
 * @Author lihao.fly@163.com
 * @Data 2023/10/23
 * @Description Used to create Synthesizer instance and provide Synthesizer feature.
 */
public class AISpeechSynthesizer extends BaseSynthesizer {
    private static final String TAG = AISpeechSynthesizer.class.getSimpleName();
    private static AISpeechSynthesizer mInstance = null;

    //The Synthesizer instance from engine
    private AICloudTTSEngine mSpeechCloudSynthesizer;
    private AILocalTTSEngine mSpeechLocalSynthesizer;
    //The listener used to get synthesizer engine status
    private AISpeechTTSCloudListener mAISpeechCloudListener;
    private AISpeechTTSLocalListener mAISpeechLocalListener;

    //For authority - start
    private String mAPIKey;
    private String mProductID;
    private String mProductKey;
    private String mProductSecret;
    //For authority - end

    //Specific parameters
    private String mTTSTextType; //AISpeech support text and ssml
    private AICloudTTSIntent mCloudTTSIntent;
    private AILocalTTSIntent mLocalTTSIntent;
    private String mDictResPath;
    private String mFrontResPath;
    private InitListener mInitListener;
    private String mSpeakerResPath;

    private AISpeechSynthesizer() {
        super();
    }

    public static AISpeechSynthesizer getInstance(){
        if( mInstance==null ){
            synchronized(AISpeechSynthesizer.class) {
                if( mInstance==null ){
                    mInstance = new AISpeechSynthesizer();
                }
            }
        }
        return mInstance;
    }

    @Override
    public String GetTTSEngineName(){
        return "AISpeech";
    }

    @Override
    public boolean InitializeTTS(Context context, Handler Handler) {
        mContext = context;
        mHandler = Handler;
        loadConfig();
        String str = getCertificateSHA1Fingerprint(mContext);
        //This SH256 code should added to DUI online website
        MyLogger.debug(TAG, "InitializeTTS() SH256: " + str);

        Authority(mAPIKey, mProductID, mProductKey, mProductSecret);

        if(mTTSMode.equals("TTS_MODE_ONLINE")) {
            MyLogger.debug(TAG, "InitializeTTS() TTS_MODE_ONLINE");

            // Create cloud synthesizer player
            mSpeechCloudSynthesizer = AICloudTTSEngine.createInstance();
            mAISpeechCloudListener = new AISpeechTTSCloudListener(mHandler);
            AICloudTTSConfig cloudConfig = new AICloudTTSConfig();

            cloudConfig.setUseCache(true);
            mSpeechCloudSynthesizer.init(cloudConfig, mAISpeechCloudListener);

            mCloudTTSIntent = new AICloudTTSIntent();
            mCloudTTSIntent.setTextType(mTTSTextType);
            mCloudTTSIntent.setSpeakingStyle("happy");
            mCloudTTSIntent.setSpeaker(mTTSPrompter);
            mCloudTTSIntent.setReturnPhone(true);
            mCloudTTSIntent.setSpeed(mTTSSpeed);
            mCloudTTSIntent.setVolume(mTTSVolume);
            MyLogger.debug(TAG, "mTTSTextType: " + mTTSTextType +
                                       " mTTSPrompter: " + mTTSPrompter +
                                       " mTTSSpeed: " + mTTSSpeed +
                                       " mTTSVolume: " + mTTSVolume);
            mCloudTTSIntent.setSaveAudioPath(Environment.getExternalStorageDirectory() + "/tts");
        }else if(mTTSMode.equals("TTS_MODE_OFFLINE")){
            // Create local synthesizer player
            mSpeechLocalSynthesizer = AILocalTTSEngine.createInstance();
            mAISpeechLocalListener = new AISpeechTTSLocalListener(mHandler);
            AILocalTTSConfig localConfig = new AILocalTTSConfig();

            localConfig.setEnableOptimization(true);
            localConfig.setDictResource(mDictResPath);
            localConfig.setFrontBinResource(mFrontResPath);
            MyLogger.debug(TAG,"mDictResPath: " + mDictResPath);
            MyLogger.debug(TAG,"mFrontResPath: " + mFrontResPath);
            localConfig.setLanguage(0);//不设置默认是0
            localConfig.setUseCache(false);
            String [] speakerList = new String[]{mSpeakerResPath};
            String[] mBackResBinArray = new String[]{ "tts/back_chuxif_220607.bin",
                                                      "tts/chuxif_ctn_lstm_20220816.bin",
                                                      "tts/chuxif_tha_lstm_20230107.bin",
                                                      "tts/cjhaof_lstm_210920.bin",
                                                      "tts/jlshim_lstm_230406.bin",
                                                      "tts/lzlinf_lstm_210827.bin",
                                                      "tts/xijunm_lstm_210820.bin",
                                                      "tts/back_gdfanf_natong_ttsv5_4.2n_230901.bin",
                                                      "tts/brettm_lstm_20221019.bin",
                                                      "tts/wjianm_lstm_230515.bin" };
            localConfig.addSpeakerResource(mBackResBinArray);

            mSpeechLocalSynthesizer.init(localConfig, mAISpeechLocalListener);//初始化合成引擎

            mLocalTTSIntent = new AILocalTTSIntent();

            if(mTTSTextType.equals("text")) {
                mLocalTTSIntent.setUseSSML(false);
            }else if(mTTSTextType.equals("ssml")) {
                mLocalTTSIntent.setUseSSML(true);
            }else{
                mLocalTTSIntent.setUseSSML(false);
            }
            mLocalTTSIntent.setSpeed(Float.parseFloat(mTTSSpeed));
            mLocalTTSIntent.setVolume(Integer.parseInt(mTTSVolume));// 设置合成音频的音量，范围为1～500
            mLocalTTSIntent.setLmargin(10);    //设置头部静音段，范围5-20
            mLocalTTSIntent.setRmargin(10);    //设置尾部静音段，范围5-20
            mLocalTTSIntent.setSaveAudioFilePath(Environment.getExternalStorageDirectory() + "/tts");
            mLocalTTSIntent.setUseTimeStamp(true);
            mLocalTTSIntent.setSleepTime(300);
            mLocalTTSIntent.setPlaySampleRate(AISampleRate.SAMPLE_RATE_16K);
            mLocalTTSIntent.setLanguage(0);
            mLocalTTSIntent.switchToSpeaker(mTTSPrompter);
        }else{
            return false;
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

        if (mSpeechCloudSynthesizer == null && mSpeechLocalSynthesizer == null) {
            MyLogger.error(TAG, "StartSpeak: Initialize failed");
            return false;
        }

        if(ttsStr.length() > mMaxTTSLength){
            ttsStr = ttsStr.substring(0,mMaxTTSLength);
            MyLogger.warning(TAG, "StartSpeak: TTS be cut. The support max length is: "
                    + mMaxTTSLength);
        }

        if(mTTSMode.equals("TTS_MODE_ONLINE")) {
            MyLogger.debug(TAG, "StartSpeak: TTS_MODE_ONLINE");
            mSpeechCloudSynthesizer.speak(mCloudTTSIntent, ttsStr, "1024");
        }else if(mTTSMode.equals("TTS_MODE_OFFLINE")){
            MyLogger.debug(TAG, "StartSpeak: TTS_MODE_OFFLINE");
            mSpeechLocalSynthesizer.speak(mLocalTTSIntent, ttsStr, "1024");
        }else{
            MyLogger.error(TAG, "TTS Mode wrong");
        }
        return true;
    }

    @Override
    public boolean StopSpeak(){
        MyLogger.debug(TAG, "StopSpeak: Stop Speak");
        if (mSpeechCloudSynthesizer == null && mSpeechLocalSynthesizer == null) {
            MyLogger.error(TAG, "StopSpeak: Initialize failed");
            return false;
        }
        if(mTTSMode.equals("TTS_MODE_ONLINE")) {
            mSpeechCloudSynthesizer.stop();
        }else if(mTTSMode.equals("TTS_MODE_OFFLINE")){
            mSpeechLocalSynthesizer.stop();
        }else{
            MyLogger.error(TAG, "TTS Mode wrong");
        }

        return true;
    }

    @Override
    public boolean Destroy() {
        if (mSpeechCloudSynthesizer == null && mSpeechLocalSynthesizer == null) {
            if(mTTSMode.equals("TTS_MODE_ONLINE")) {
                mSpeechCloudSynthesizer.destroy();
            }else if(mTTSMode.equals("TTS_MODE_OFFLINE")){
                mSpeechLocalSynthesizer.destroy();
            }else{
                MyLogger.error(TAG, "TTS Mode wrong");
            }
        }
        return true;
    }

    /**
     * Load the TTS prompt configurations from assert config file: Config.properties
     */
    @Override
    protected boolean loadConfig(){
        if(mContext != null) {
            mAPIKey = Config.getInstance(mContext).getConfigItem("AISPEECH_AUTHORITY_APPKEY");
            mProductID = Config.getInstance(mContext).getConfigItem("AISPEECH_AUTHORITY_PRODUCTID");
            mProductKey = Config.getInstance(mContext).getConfigItem("AISPEECH_AUTHORITY_PRODUCTKEY");
            mProductSecret = Config.getInstance(mContext).getConfigItem("AISPEECH_AUTHORITY_PRODUCTSECRET");
            mMaxTTSLength =Integer.parseInt(Config.getInstance(mContext).getConfigItem("AISPEECH_TTS_MAX_LENGTH"));

            mTTSMode = Config.getInstance(mContext).getConfigItem("AISPEECH_TTS_MODE");
            mTTSPrompter = Config.getInstance(mContext).getConfigItem("AISPEECH_TTS_PROMPTER");
            mTTSVolume = Config.getInstance(mContext).getConfigItem("AISPEECH_TTS_VOLUME");
            mTTSSpeed = Config.getInstance(mContext).getConfigItem("AISPEECH_TTS_SPEED");
            mTTSPitch = Config.getInstance(mContext).getConfigItem("AISPEECH_TTS_PITCH");
            mTTSTextType = Config.getInstance(mContext).getConfigItem("AISPEECH_TTS_TEXT_TYPE");

            mDictResPath = Config.getInstance(mContext).getConfigItem("AISPEECH_TTS_RESOURCE_PATH") +
                           Config.getInstance(mContext).getConfigItem("AISPEECH_TTS_DICT_RES_NAME");
            mFrontResPath = Config.getInstance(mContext).getConfigItem("AISPEECH_TTS_RESOURCE_PATH") +
                            Config.getInstance(mContext).getConfigItem("AISPEECH_TTS_FRONT_RES_NAME");

            mSpeakerResPath = Config.getInstance(mContext).getConfigItem("AISPEECH_TTS_RESOURCE_PATH") +
                              Config.getInstance(mContext).getConfigItem("AISPEECH_TTS_SPEAKER_RES_NAME");



            return true;
        }else{
            return false;
        }
    }

    /**
     * Use to do Authority
     * @param apiKey api key, Get from DUI online website
     * @param productID product id, Get from DUI online website
     * @param productKey product key, Get from DUI online website
     * @param productSecret product secret, Get from DUI online website
     */
    private void Authority(String apiKey, String productID, String productKey, String productSecret){
        MyLogger.debug(TAG, "Authority: Start");
        DUILiteConfig config = new DUILiteConfig(apiKey, productID, productKey, productSecret);

        config.setAuthTimeout(5000); //设置授权连接超时时长，默认5000ms

        config.setDeviceProfileDirPath("/sdcard/speech");  // 自定义设置授权文件的保存路径,需要确保该路径事先存在
        config.setAudioRecorderType(DUILiteConfig.TYPE_COMMON_MIC);

        // echo模式 ==>
        // config.setAudioRecorderType(DUILiteConfig.TYPE_COMMON_ECHO);
        if (config.getAudioRecorderType() == DUILiteConfig.TYPE_COMMON_ECHO) {
            AIEchoConfig aiEchoConfig = new AIEchoConfig();
            aiEchoConfig.setAecResource("echo/sspe_aec_ch2_mic1_ref1_asr_v2.0.0.90.bin"); // 设置echo的AEC资源文件
            aiEchoConfig.setChannels(2); //音频总的通道数
            aiEchoConfig.setMicNumber(1); //真实mic数
            // 默认为1,即左通道为rec录音音频,右通道为play参考音频（播放音频）
            // 若设置为2,通道会互换，即左通道为play参考音频（播放音频）,右通道为rec录音音频
            aiEchoConfig.setRecChannel(1);
            aiEchoConfig.setSavedDirPath("/sdcard/aispeech/aecPcmFile/");//设置保存的aec原始输入和aec之后的音频文件路径

            config.setEchoConfig(aiEchoConfig);
        }
        // echo模式 <==

        String core_version = DUILiteSDK.getCoreVersion();//获取内核版本号
        MyLogger.debug(TAG, "core version is: " + core_version);

        boolean isAuthorized = DUILiteSDK.isAuthorized(mContext);//查询授权状态，DUILiteSDK.init之后随时可以调
        MyLogger.debug(TAG, "DUILite SDK is isAuthorized ？ " + isAuthorized);

        mInitListener = new DUILiteSDK.InitListener() {
            @Override
            public void success() {
                MyLogger.debug(TAG, "授权成功! ");
            }

            @Override
            public void error(String errorCode,String errorInfo) {
                MyLogger.debug(TAG, "授权失败, errorcode: "+errorCode+",errorInfo:"+errorInfo);
            }
        };

        DUILiteSDK.init(mContext,config,mInitListener);
    }

    /**
     * Used to get the SHA1 of current application
     * @param context The context of application
     * @return Return SHA1 of application
     */
    private String getCertificateSHA1Fingerprint(Context context) {
        //获取包管理器
        PackageManager pm = context.getPackageManager();
        //获取当前要获取SHA1值的包名，也可以用其他的包名，但需要注意，
        //在用其他包名的前提是，此方法传递的参数Context应该是对应包的上下文。
        String packageName = context.getPackageName();
        //返回包括在包中的签名信息
        int flags = PackageManager.GET_SIGNATURES;
        PackageInfo packageInfo = null;
        try {
            //获得包的所有内容信息类
            packageInfo = pm.getPackageInfo(packageName, flags);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //签名信息
        Signature[] signatures = packageInfo.signatures;
        byte[] cert = signatures[0].toByteArray();
        //将签名转换为字节数组流
        InputStream input = new ByteArrayInputStream(cert);
        //证书工厂类，这个类实现了出厂合格证算法的功能
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X509");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //X509证书，X.509是一种非常通用的证书格式
        X509Certificate c = null;
        try {
            c = (X509Certificate) cf.generateCertificate(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String hexString = null;


        try {
            //加密算法的类，这里的参数可以使MD4,MD5等加密算法
            MessageDigest md = MessageDigest.getInstance("SHA256");
            //获得公钥
            byte[] publicKey = md.digest(c.getEncoded());
            //字节到十六进制的格式转换
            hexString = byte2HexFormatted(publicKey);
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        return hexString;
    }

    /**
     * Change byte data to hex code
     * @param arr byte data
     * @return
     */
    private String byte2HexFormatted(byte[] arr) {
        StringBuilder str = new StringBuilder(arr.length * 2);
        for (int i = 0; i < arr.length; i++) {
            String h = Integer.toHexString(arr[i]);
            int l = h.length();
            if (l == 1)
                h = "0" + h;
            if (l > 2)
                h = h.substring(l - 2, l);
            str.append(h.toUpperCase());
            if (i < (arr.length - 1))
                str.append(':');
        }
        return str.toString();
    }
}
