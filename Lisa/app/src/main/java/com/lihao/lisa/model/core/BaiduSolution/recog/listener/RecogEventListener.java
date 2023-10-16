package com.lihao.lisa.model.core.BaiduSolution.recog.listener;

import com.lihao.lisa.util.MyLogger;
import com.baidu.speech.EventListener;
import com.baidu.speech.asr.SpeechConstant;
import org.json.JSONException;
import org.json.JSONObject;

public class RecogEventListener implements EventListener {
    private static final String TAG = RecogEventListener.class.getSimpleName();
    private IRecogListener listener;
    public RecogEventListener(IRecogListener listener) {
        this.listener = listener;
    }

    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        MyLogger.debug(TAG, "onEvent(): name:" + name + " params:" + params);
        String currentJson = params;

        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_LOADED)) {
            listener.onOfflineLoaded();
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_UNLOADED)) {
            listener.onOfflineUnLoaded();
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_READY)) {
            // 引擎准备就绪，可以开始说话
            listener.onAsrReady();
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_BEGIN)) {
            // 检测到用户的已经开始说话
            listener.onAsrBegin();
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_END)) {
            // 检测到用户的已经停止说话
            listener.onAsrEnd();
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
            RecogResult recogResult = RecogResult.parseJson(params);
            // 识别结果
            String[] results = recogResult.getResultsRecognition();
            if (recogResult.isFinalResult()) {
                // 最终识别结果，长语音每一句话会回调一次
                listener.onAsrFinalResult(results, recogResult);
            } else if (recogResult.isPartialResult()) {
                // 临时识别结果
                listener.onAsrPartialResult(results, recogResult);
            } else if (recogResult.isNluResult()) {
                // 语义理解结果
                listener.onAsrOnlineNluResult(new String(data, offset, length));
            }

        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_FINISH)) {
            // 识别结束
            RecogResult recogResult = RecogResult.parseJson(params);
            if (recogResult.hasError()) {
                int errorCode = recogResult.getError();
                int subErrorCode = recogResult.getSubError();
                MyLogger.error(TAG, "asr error:" + params);
                listener.onAsrFinishError(errorCode, subErrorCode, recogResult.getDesc(), recogResult);
            } else {
                listener.onAsrFinish(recogResult);
            }
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_LONG_SPEECH)) { // 长语音
            listener.onAsrLongFinish();
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_EXIT)) {
            listener.onAsrExit();
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_VOLUME)) {
            Volume vol = parseVolumeJson(params);
            listener.onAsrVolume(vol.volumePercent, vol.volume);
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_AUDIO)) {
            if (data.length != length) {
                MyLogger.error(TAG, "internal error: asr.audio callback data length is not equal to length param");
            }
            listener.onAsrAudio(data, offset, length);
        }
    }

    private Volume parseVolumeJson(String jsonStr) {
        Volume vol = new Volume();
        vol.origalJson = jsonStr;
        try {
            JSONObject json = new JSONObject(jsonStr);
            vol.volumePercent = json.getInt("volume-percent");
            vol.volume = json.getInt("volume");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return vol;
    }

    private class Volume {
        private int volumePercent = -1;
        private int volume = -1;
        private String origalJson;
    }

}
