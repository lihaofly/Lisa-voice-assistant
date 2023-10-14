package com.lihao.lisa.model.core.AISpeech.tts;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.aispeech.AIEchoConfig;
import com.aispeech.AIError;
import com.aispeech.DUILiteConfig;
import com.aispeech.DUILiteSDK;
import com.aispeech.common.AIConstant;
import com.aispeech.export.config.AICloudTTSConfig;
import com.aispeech.export.engines2.AICloudTTSEngine;
import com.aispeech.export.intent.AICloudTTSIntent;
import com.aispeech.export.listeners.AITTSListener;
import com.lihao.lisa.model.core.base.BaseTTS;
import com.lihao.lisa.util.BaseMessage;
import com.lihao.lisa.util.TTSMessage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import static com.lihao.lisa.util.BaseMessage.RESULT_SUCCESS;
import static com.lihao.lisa.util.TTSMessage.TTS_STATUS_INPUT_TEXT_FINISHED;

public class AISpeechTTS extends BaseTTS {
    private AICloudTTSEngine mEngine;
    private AICloudTTSIntent intent;
    private static AISpeechTTS mInstance = null;
    private static final String TAG = "AISpeechTTS";
    private Context mContext;
    private Handler mHandler;

    private AISpeechTTS() {
        super();
    }

    public static AISpeechTTS getInstance(){
        if( mInstance==null ){
            synchronized(AISpeechTTS.class) {
                if( mInstance==null ){
                    mInstance = new AISpeechTTS();
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
        String str = getCertificateSHA1Fingerprint(mContext);

        Log.d(TAG, "InitializeTTS: SH256: " + str);

        // 创建云端合成播放器
        mEngine = AICloudTTSEngine.createInstance();
        AICloudTTSConfig config = new AICloudTTSConfig();

        Authority();

        mEngine.init(config, new AITTSListenerImpl());
        intent = new AICloudTTSIntent();
        intent.setTextType("text"); // 合成的文本类型, text or ssml, default is text
        //intent.setSpeaker("yaayif");
        intent.setSpeed("0.8");
        intent.setVolume("100");
        intent.setSaveAudioPath(Environment.getExternalStorageDirectory() + "/tts");//设置合成音的保存路径
        return true;
    }

    @Override
    public boolean StartSpeak(String ttsStr){
        mEngine.speak(intent, ttsStr, "1024");
        return true;
    }

    @Override
    public boolean StopSpeak(){
        mEngine.stop();
        return true;
    }

    @Override
    public boolean Destroy() {
        mEngine.destroy();
        return true;
    }

    private class AITTSListenerImpl implements AITTSListener {

        @Override
        public void onInit(int status) {
            Log.d(TAG, "onInit()");
            if (status == AIConstant.OPT_SUCCESS) {
                Log.i(TAG, "初始化成功!");
            } else {
                Log.i(TAG, "初始化失败!");
            }
        }

        @Override
        public void onError(String utteranceId, AIError error) {
            Log.e(TAG, "onError: " + utteranceId + "," + error.toString());
        }


        @Override
        public void onReady(String utteranceId) {
            Log.d(TAG, "onReady: " + utteranceId);
        }


        @Override
        public void onCompletion(String utteranceId) {
            Log.d(TAG, "合成完成 onCompletion: " + utteranceId);
            Log.d(TAG, "onSpeechFinish: ");
            TTSMessage ttsMsg = new TTSMessage(RESULT_SUCCESS, TTS_STATUS_INPUT_TEXT_FINISHED);
            ttsMsg.setmProgress(10);
            sendMessage(ttsMsg, BaseMessage.TTS_MESSAGE);
        }


        @Override
        public void onProgress(int currentTime, int totalTime, boolean isRefTextTTSFinished) {
            Log.d(TAG, "当前:" + currentTime + "ms, 总计:" + totalTime + "ms, 可信度:" + isRefTextTTSFinished);
        }

        @Override
        public void onSynthesizeStart(String s) {

        }

        @Override
        public void onSynthesizeDataArrived(String s, byte[] bytes) {

        }

        @Override
        public void onSynthesizeFinish(String s) {

        }
    }


    private void Authority(){
        /**
         * 初始化授权信息
         * @param apikey 从DUI平台产品里获取的ApiKey
         * @param productId 产品ID
         * @param productKey 产品Key
         * @param productSecret 产品Secret
         */
        Log.d(TAG, "Authority: Start");
//        DUILiteConfig config = new DUILiteConfig(
//                "647e0a7a688a647e0a7a688a60f50335",
//                "279603632",
//                "5fb3fe136eed351968fb6f2f8e9f46f2",
//                "fe9fa1986810d1e7a0cc4fd45c86d9c7");
        DUILiteConfig config = new DUILiteConfig(
                "86816fe9b54586816fe9b54560f4ff12",
                "279603541",
                "251608d17a28b40a5cdd2fec35ae31be",
                "5f61f0d9601e3012017d1be1384b632d");


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
        Log.d(TAG, "core version is: " + core_version);

        boolean isAuthorized = DUILiteSDK.isAuthorized(mContext);//查询授权状态，DUILiteSDK.init之后随时可以调
        Log.d(TAG, "DUILite SDK is isAuthorized ？ " + isAuthorized);

        DUILiteSDK.init(mContext,
                config,
                new DUILiteSDK.InitListener() {
                    @Override
                    public void success() {
                        Log.d(TAG, "授权成功! ");
                    }

                    @Override
                    public void error(String errorCode,String errorInfo) {
                        Log.d(TAG, "授权失败, errorcode: "+errorCode+",errorInfo:"+errorInfo);
                    }
                });
    }

    //这个是获取SHA1的方法
    public static String getCertificateSHA1Fingerprint(Context context) {
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

    //这里是将获取到得编码进行16进制转换
    private static String byte2HexFormatted(byte[] arr) {
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

    protected void sendMessage(TTSMessage obj, int action) {
        if (mHandler != null) {
            Message msg = Message.obtain();
            msg.what = action;
            msg.obj = obj;
            mHandler.sendMessage(msg);
        }
    }

}
