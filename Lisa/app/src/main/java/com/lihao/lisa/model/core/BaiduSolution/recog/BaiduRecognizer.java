package com.lihao.lisa.model.core.BaiduSolution.recog;

import java.util.Map;
import java.util.HashMap;
import android.content.Context;
import org.json.JSONObject;

import com.lihao.lisa.util.MyLogger;
import com.lihao.lisa.model.core.BaiduSolution.recog.listener.IRecogListener;
import com.lihao.lisa.model.core.BaiduSolution.recog.listener.RecogEventListener;
import com.lihao.lisa.model.core.baidu.auth.AuthUtil;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;

/**
 * Class that wrap the Baidu recognizer interface
 */
public class BaiduRecognizer {
    private static final String TAG = BaiduRecognizer.class.getSimpleName();
    private EventManager asrEventManager;
    private EventListener eventListener;
    private static boolean isOfflineEngineLoaded = false;
    private static volatile boolean isInited = false;

    private static BaiduRecognizer sInStance = null;

    public static BaiduRecognizer getInstance(Context context, IRecogListener recogListener){
        if(sInStance == null) {
            synchronized (BaiduRecognizer.class) {
                if(sInStance == null){
                    sInStance = new BaiduRecognizer(context, recogListener);
                }
            }
        }
        return sInStance;
    }

    /**
     * Constructor
     *
     * @param context Context of application
     * @param recogListener Callback of recognize status and result.
     */
    private BaiduRecognizer(Context context, IRecogListener recogListener) {
        this(context, new RecogEventListener(recogListener));
    }

    /**
     * Constructor. Initialize Event manager in SDK
     *
     * @param context
     * @param eventListener recognize status and result
     */
    private BaiduRecognizer(Context context, EventListener eventListener) {
        if (isInited) {
            MyLogger.error(TAG, "Didn't release(), Don't create new instance");
            throw new RuntimeException("Didn't release(), Don't create new instance");
        }
        isInited = true;
        this.eventListener = eventListener;
        // Init EventManager of ASR. Only support one instance.
        asrEventManager = EventManagerFactory.create(context, "asr");
        // Register callback return status and asr result
        asrEventManager.registerListener(eventListener);
    }

    /**
     * Used of offline recognize
     *
     * @param params Load offline mode，Refer “ASR_KWS_LOAD_ENGINE”
     */
    public void loadOfflineEngine(Map<String, Object> params) {
        String json = new JSONObject(params).toString();
        MyLogger.debug(TAG, "Offline mode paramters: " + json);
        // Load offline command
        asrEventManager.send(SpeechConstant.ASR_KWS_LOAD_ENGINE, json, null, 0, 0);
        isOfflineEngineLoaded = true;
     }

    /**
     * Start voice recognize. It will trigger record the audio
     *
     * @param params set the asr parameters, null is supported
     */
    public void start(Map<String, Object> params) {
        if (!isInited) {
            throw new RuntimeException("release() was called");
        }
        Map<String, Object> tempParams = new HashMap<>();

        if(null == params) {
            tempParams = AuthUtil.getParam();
        }else{
            tempParams = params;
        }

        tempParams.put("pid", 15373);
        tempParams.put("accept-audio-volume", true);
        MyLogger.debug(TAG, "startASR: tempParams: " + tempParams);

        // Set recognize parameters
        String json = new JSONObject(tempParams).toString();
        MyLogger.debug(TAG, "Asr start parameters: " + json);
        asrEventManager.send(SpeechConstant.ASR_START, json, null, 0, 0);
    }


    /**
     * Stop voice recognize and waiting for result
     */
    public void stop() {
        MyLogger.debug(TAG, "Stop audio record");
        if (!isInited) {
            throw new RuntimeException("release() was called");
        }
        asrEventManager.send(SpeechConstant.ASR_STOP, "{}", null, 0, 0);
    }

    /**
     * Cancel recognize of current times. Will not response recognize result.
     *
     */
    public void cancel() {
        MyLogger.debug(TAG, "Cancel recognize");
        if (!isInited) {
            throw new RuntimeException("release() was called");
        }
        asrEventManager.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
    }

    /**
     * Unregister listener and unload offline command
     *
     */
    public void release() {
        if (asrEventManager == null) {
            return;
        }
        cancel();
        if (isOfflineEngineLoaded) {
            // Need to unload if upload before
            asrEventManager.send(SpeechConstant.ASR_KWS_UNLOAD_ENGINE, null, null, 0, 0);
            isOfflineEngineLoaded = false;
        }

        asrEventManager.unregisterListener(eventListener);
        asrEventManager = null;
        isInited = false;
    }

    /**
     * Register recognize status and result listener
     *
     */
    public void setEventListener(IRecogListener recogListener) {
        if (!isInited) {
            throw new RuntimeException("release() was called");
        }
        this.eventListener = new RecogEventListener(recogListener);
        asrEventManager.registerListener(eventListener);
    }
}
