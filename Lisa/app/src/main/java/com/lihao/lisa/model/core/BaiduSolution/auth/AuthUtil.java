package com.lihao.lisa.model.core.baidu.auth;

import com.baidu.speech.asr.SpeechConstant;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 为方便说明 apiKey 简称ak，secretKey简称 sk
 * ak sk为敏感信息，泄露后别人可使用ak sk 消耗你的调用次数，造成财产损失，请妥善保存
 * 建议你将ak sk保存在自己服务端，通过接口请求获得。
 * 如果暂时没有后端服务接口，建议将ak sk加密存储减少暴露风险。
 **/
public class AuthUtil {
    public static String getApplicationID(){
        //Add package name of your application
        return  "com.lihao.lisa";
    }
    public static String getAppId(){
        //Add appId
        return  "40986487";
    }
    public static String getAk(){
        //Add apiKey
        return "xxxxxx";
    }
    public static String getSk(){
        //Add secretKey
        return  "xxxxxx";
    }

    public static Map<String, Object> getParam(){
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(SpeechConstant.APP_ID, getAppId());
        params.put(SpeechConstant.APP_KEY, getAk());
        params.put(SpeechConstant.SECRET, getSk());
        return  params;
    }
}
