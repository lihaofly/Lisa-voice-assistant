package com.lihao.lisa.model.core.base;

import android.content.Context;
import android.os.Handler;

import com.lihao.lisa.model.core.baidu.tts.listener.MessageListener;

import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;

public abstract class BaseTTS {

    public BaseTTS(){
    }
    public abstract String GetTTSEngineName();
    public abstract boolean InitializeTTS(Context context, Handler Handler) throws CertificateEncodingException, NoSuchAlgorithmException;
    public abstract boolean StartSpeak(String ttsStr);
    public abstract boolean StopSpeak();
    public abstract boolean Destroy();
}
