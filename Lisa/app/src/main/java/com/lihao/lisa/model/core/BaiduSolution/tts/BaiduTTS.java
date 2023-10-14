package com.lihao.lisa.model.core.baidu.tts;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.TtsMode;
import com.lihao.lisa.model.core.baidu.tts.listener.MessageListener;
import com.lihao.lisa.model.core.baidu.tts.listener.UiMessageListener;
import com.lihao.lisa.model.core.baidu.tts.util.Auth;
import com.lihao.lisa.model.core.base.BaseTTS;

import android.content.Context;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.lihao.lisa.model.core.baidu.tts.util.IOfflineResourceConst.DEFAULT_SDK_TTS_MODE;
import static com.lihao.lisa.model.core.baidu.tts.util.IOfflineResourceConst.PARAM_SN_NAME;
import static com.lihao.lisa.model.core.baidu.tts.util.IOfflineResourceConst.TEXT_MODEL;
import static com.lihao.lisa.model.core.baidu.tts.util.IOfflineResourceConst.VOICE_MALE_MODEL;

public class BaiduTTS extends BaseTTS {
    private static BaiduTTS mInstance = null;
    private static final String TAG = "BaiduTTS";
    private Context mContext;
    private Handler mHandler;
    private MessageListener mMsgListener;
    //protected Handler mainHandler;

    protected String appId;
    protected String appKey;
    protected String secretKey;
    protected String sn;
    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    private TtsMode ttsMode = DEFAULT_SDK_TTS_MODE;
    private boolean isOnlineSDK = TtsMode.ONLINE.equals(DEFAULT_SDK_TTS_MODE);
    // ================ 纯离线sdk或者选择TtsMode.ONLINE  以下参数无用;
    private static final String TEMP_DIR = "/sdcard/baiduTTS"; // 重要！请手动将assets目录下的3个dat 文件复制到该目录
    // 请确保该PATH下有这个文件
    private static final String TEXT_FILENAME = TEMP_DIR + "/" + TEXT_MODEL;
    // 请确保该PATH下有这个文件 ，m15是离线男声
    private static final String MODEL_FILENAME = TEMP_DIR + "/" + VOICE_MALE_MODEL;

    protected SpeechSynthesizer mSpeechSynthesizer;

    private BaiduTTS(){
        super();

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

            }
        };
    }

    public static BaiduTTS getInstance(){
        if( mInstance == null ){
            synchronized (BaiduTTS.class){
                if( mInstance == null ){
                    mInstance = new BaiduTTS();
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
     * 注意此处为了说明流程，故意在UI线程中调用。
     * 实际集成中，该方法一定在新线程中调用，并且该线程不能结束。具体可以参考NonBlockSyntherizer的写法
     */
    @Override
    public boolean InitializeTTS(Context context, Handler Handler) {
        LoggerProxy.printable(true); // 日志打印在logcat中

        mContext = context;
        mMsgListener = new UiMessageListener(Handler);

        appId = Auth.getInstance(mContext).getAppId();
        appKey = Auth.getInstance(mContext).getAppKey();
        secretKey = Auth.getInstance(mContext).getSecretKey();
        sn = Auth.getInstance(mContext).getSn(); // 纯离线合成必须有此参数；离在线合成SDK没有此参数

        Log.d(TAG, "BaiduTTS: appID: " + appId);
        Log.d(TAG, "BaiduTTS: appkey: " + appKey);
        Log.d(TAG, "BaiduTTS: secretKey: " + secretKey);
        Log.d(TAG, "BaiduTTS: sn: " + sn);

        boolean isSuccess;
        if (!isOnlineSDK) {
            // 检查2个离线资源是否可读
            isSuccess = checkOfflineResources();
            if (!isSuccess) {
                return false;
            } else {
                Log.d(TAG, "initTTs: 离线资源存在并且可读, 目录："+ TEMP_DIR);
            }
        }
        // 日志更新在UI中，可以换成MessageListener，在logcat中查看日志
        //UiMessageListener listener = new UiMessageListener(mHandler);

        // 1. 获取实例
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(mContext);

        // 2. 设置listener
        mSpeechSynthesizer.setSpeechSynthesizerListener(mMsgListener);

        // 3. 设置appId，appKey.secretKey
        int result = mSpeechSynthesizer.setAppId(appId);
        checkResult(result, "setAppId");
        result = mSpeechSynthesizer.setApiKey(appKey, secretKey);
        checkResult(result, "setApiKey");

        // 4. 如果是纯离线SDK需要离线功能的话
        if (!isOnlineSDK) {
            // 文本模型文件路径 (离线引擎使用)， 注意TEXT_FILENAME必须存在并且可读
            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, TEXT_FILENAME);
            // 声学模型文件路径 (离线引擎使用)， 注意TEXT_FILENAME必须存在并且可读
            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, MODEL_FILENAME);

            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
            // 该参数设置为TtsMode.MIX生效。
            // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
            // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
            // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
            // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        }

        // 5. 以下setParam 参数选填。不填写则默认值生效
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声  3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "4");
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "9");//设置合成的音量，0-15 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");//设置合成的语速，0-15 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");//设置合成的语调，0-15 ，默认 5
        // mSpeechSynthesizer.setAudioStreamType(AudioManager.MODE_IN_CALL); // 调整音频输出

        if (sn != null) {
            // 纯离线sdk这个参数必填；离在线sdk没有此参数
            mSpeechSynthesizer.setParam(PARAM_SN_NAME, sn);
        }

        // x. 额外 ： 自动so文件是否复制正确及上面设置的参数
        Map<String, String> params = new HashMap<>();
        // 复制下上面的 mSpeechSynthesizer.setParam参数
        // 上线时请删除AutoCheck的调用
        if (!isOnlineSDK) {
            params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, TEXT_FILENAME);
            params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, MODEL_FILENAME);
        }

//        // 检测参数，通过一次后可以去除，出问题再打开debug
//        InitConfig initConfig = new InitConfig(appId, appKey, secretKey, ttsMode, params, listener);
//        AutoCheck.getInstance(mContext).check(initConfig, new Handler() {
//            @Override
//            /**
//             * 开新线程检查，成功后回调
//             */
//            public void handleMessage(Message msg) {
//                if (msg.what == 100) {
//                    AutoCheck autoCheck = (AutoCheck) msg.obj;
//                    synchronized (autoCheck) {
//                        String message = autoCheck.obtainDebugMessage();
//                        //print(message); // 可以用下面一行替代，在logcat中查看代码
//                        Log.w("AutoCheckMessage", message);
//                    }
//                }
//            }
//
//        });

        // 6. 初始化
        result = mSpeechSynthesizer.initTts(ttsMode);
        checkResult(result, "initTts");
        return true;
    }


    @Override
    public boolean StartSpeak(String ttsStr) {
        /* 以下参数每次合成时都可以修改
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
         *  设置在线发声音人： 0 普通女声（默认） 1 普通男声  3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "5"); 设置合成的音量，0-9 ，默认 5
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5"); 设置合成的语速，0-9 ，默认 5
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5"); 设置合成的语调，0-9 ，默认 5
         *
         */
        Log.d(TAG, "StartSpeak: ttsStr: "+ ttsStr);
        Log.d(TAG, "StartSpeak: ttsStr length: "+ ttsStr.length());
        if(ttsStr.length() > 60)
           ttsStr = ttsStr.substring(0,60);
        if (mSpeechSynthesizer == null || ttsStr.equals("")) {
            Log.e(TAG, "speak: [ERROR], 初始化失败");
            return false;
        }
        int result = mSpeechSynthesizer.speak(ttsStr);
        Log.d(TAG, "speak: 合成并播放 按钮已经点击");
        checkResult(result, "speak");
        return true;
    }

    @Override
    public boolean StopSpeak() {
        Log.d(TAG, "stop: 停止合成引擎 按钮已经点击");
        int result = mSpeechSynthesizer.stop();
        checkResult(result, "stop");
        return true;
    }

    public boolean Destroy() {
        if (mSpeechSynthesizer != null) {
            mSpeechSynthesizer.stop();
            mSpeechSynthesizer.release();
            mSpeechSynthesizer = null;
            Log.d(TAG, "onDestroy: 释放资源成功");
        }
        return true;
    }

    private void checkResult(int result, String method) {
        if (result != 0) {
            Log.d(TAG, "checkResult: error code :"+ result + " method:" + method);
        }
    }

    /**
     * 在线SDK不需要调用，纯离线SDK会检查资源文件
     *
     * 检查 TEXT_FILENAME, MODEL_FILENAME 这2个文件是否存在，不存在请自行从assets目录里手动复制
     *
     * @return 检测是否成功
     */
    private boolean checkOfflineResources() {
        String[] filenames = {TEXT_FILENAME, MODEL_FILENAME};
        for (String path : filenames) {
            File f = new File(path);
            if (!f.canRead()) {
                Log.e(TAG, "[ERROR] 文件不存在或者不可读取，请从demo的assets目录复制同名文件到："
                        + f.getAbsolutePath());
                Log.e(TAG, "checkOfflineResources: [ERROR] 初始化失败！！！");
                return false;
            }
        }
        return true;
    }
}
