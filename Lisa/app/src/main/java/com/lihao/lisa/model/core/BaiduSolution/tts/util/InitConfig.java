package com.lihao.lisa.model.core.BaiduSolution.tts.util;

import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;

import java.util.Map;

/**
 * @ClassName AutoCheck
 * @Author lihao.fly@163.com
 * @Data 2023/10/23
 * @Description  Class of TTS initial parameters
 */
public class InitConfig {
    /**
     * appId appKey 和 secretKey。注意如果需要离线合成功能,请在您申请的应用中填写包名。
     * 本demo的包名是com.baidu.tts.sample，定义在build.gradle中。
     */
    private String appId;

    private String appKey;

    private String secretKey;
    private String textModePath;
    private String voiceModePath;

    private String sn;

    /**
     * 纯在线或者离在线融合
     */
    private TtsMode ttsMode;


    /**
     * 初始化的其它参数，用于setParam
     */
    private Map<String, String> params;

    /**
     * 合成引擎的回调
     */
    private SpeechSynthesizerListener listener;

    private InitConfig() {

    }

    // 离在线SDK用
    public InitConfig(String appId, String appKey, String secretKey, String textModePath, String voiceModePath, TtsMode ttsMode,
                      SpeechSynthesizerListener listener) {
        this.appId = appId;
        this.appKey = appKey;
        this.secretKey = secretKey;
        this.textModePath = textModePath;
        this.voiceModePath = voiceModePath;
        this.ttsMode = ttsMode;
        this.listener = listener;
    }

    // 纯离线SDK用
    public InitConfig(String appId, String appKey, String secretKey, String textModePath, String voiceModePath, String sn, TtsMode ttsMode,
                      SpeechSynthesizerListener listener) {
        this(appId, appKey, secretKey, textModePath, voiceModePath, ttsMode, listener);
        this.sn = sn;
        if (sn != null) {
            // 纯离线sdk 才有的参数；离在线版本没有
            params.put(SpeechSynthesizer.PARAM_AUTH_SN, sn);
        }
    }

    public SpeechSynthesizerListener getListener() {
        return listener;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public String getAppId() {
        return appId;
    }
    public String getAppKey() {
        return appKey;
    }
    public String getSecretKey() {
        return secretKey;
    }
    public String getTextModePath() {
        return textModePath;
    }
    public String getVoiceModePath() {
        return voiceModePath;
    }
    public TtsMode getTtsMode() {
        return ttsMode;
    }
    public String getSn() {
        return sn;
    }
}
