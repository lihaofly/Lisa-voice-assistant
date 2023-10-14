package com.baidu.aip.asrwakeup3.core.recog.listener;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.baidu.aip.asrwakeup3.core.recog.RecogResult;
import com.baidu.aip.asrwakeup3.core.util.AsrMessage;
import com.baidu.speech.asr.SpeechConstant;

/**
 * Created by fujiayi on 2017/6/16.
 * Refactor by Lihao on 2021/6/23
 */

public class MessageStatusRecogListener extends StatusRecogListener {
    private Handler handler;
    private long speechEndTime = 0;
    private static final String TAG = "Lisa-MSRecogListener";

    public MessageStatusRecogListener(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onAsrReady() {
        Log.d(TAG, "onAsrReady");
        super.onAsrReady();
        speechEndTime = 0;
        AsrMessage asrMessage = new AsrMessage(status,SpeechConstant.CALLBACK_EVENT_WAKEUP_READY,
                "","Asr Engine ready",System.currentTimeMillis());
        sendStatusMessage(asrMessage);
    }

    @Override
    public void onAsrBegin() {
        Log.d(TAG, "onAsrBegin");
        super.onAsrBegin();
        AsrMessage asrMessage = new AsrMessage(status,SpeechConstant.CALLBACK_EVENT_ASR_BEGIN,
                "","Detected speaking begin",System.currentTimeMillis());
        sendStatusMessage(asrMessage);
    }

    @Override
    public void onAsrEnd() {
        Log.d(TAG, "onAsrEnd");
        super.onAsrEnd();
        speechEndTime = System.currentTimeMillis();
        AsrMessage asrMessage = new AsrMessage(status,SpeechConstant.CALLBACK_EVENT_ASR_END,
                "","Detected speaking ending",System.currentTimeMillis());
        sendStatusMessage(asrMessage);
    }

    @Override
    public void onAsrPartialResult(String[] results, RecogResult recogResult) {
        Log.d(TAG, "onAsrPartialResult");
        AsrMessage asrMessage = new AsrMessage(status,SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL,
                recogResult.getOrigalJson(),"Detected speaking in process",System.currentTimeMillis());
        sendStatusMessage(asrMessage);
    }

    @Override
    public void onAsrFinalResult(String[] results, RecogResult recogResult) {
        super.onAsrFinalResult(results, recogResult);
        Log.d(TAG, "onAsrFinalResult");

        AsrMessage asrMessage = new AsrMessage(status,SpeechConstant.CALLBACK_EVENT_ASR_FINISH,
                recogResult.getOrigalJson(),"Asr Finished",System.currentTimeMillis());
        sendStatusMessage(asrMessage);

        if (speechEndTime > 0) {
            String message = "识别结束，结果是”" + results[0] + "”";
            long currentTime = System.currentTimeMillis();
            long diffTime = currentTime - speechEndTime;
            message += "；说话结束到识别结束耗时[" + diffTime + "ms]" + currentTime;
            Log.d(TAG, "onAsrFinalResult: message:" + message);
        }
        speechEndTime = 0;
    }

    @Override
    public void onAsrFinishError(int errorCode, int subErrorCode, String descMessage,
                                 RecogResult recogResult) {
        super.onAsrFinishError(errorCode, subErrorCode, descMessage, recogResult);
        Log.d(TAG, "onAsrFinishError");

        AsrMessage asrMessage = new AsrMessage(status,SpeechConstant.CALLBACK_EVENT_ASR_ERROR,
                recogResult.getOrigalJson(),"Asr Error happened",System.currentTimeMillis());
        sendStatusMessage(asrMessage);

        if (speechEndTime > 0) {
            String message = "【asr.finish事件】识别错误, 错误码：" + errorCode + " ," + subErrorCode + " ; " + descMessage;
            long diffTime = System.currentTimeMillis() - speechEndTime;
            message += "。说话结束到识别结束耗时【" + diffTime + "ms】";
            Log.d(TAG, "onAsrFinishError: error: " + message);
        }
        speechEndTime = 0;
    }

    @Override
    public void onAsrOnlineNluResult(String nluResult) {
        super.onAsrOnlineNluResult(nluResult);
        Log.d(TAG, "onAsrOnlineNluResult");
        if (!nluResult.isEmpty()) {
            AsrMessage asrMessage = new AsrMessage(STATUS_NLU_FINISHED,SpeechConstant.CALLBACK_EVENT_ASR_FINISH,
                    nluResult,"Online ASR in processing",System.currentTimeMillis());
            sendStatusMessage(asrMessage);
        }
    }

    @Override
    public void onAsrFinish(RecogResult recogResult) {
        super.onAsrFinish(recogResult);
        Log.d(TAG, "onAsrFinish");
        AsrMessage asrMessage = new AsrMessage(STATUS_ALL_FINISHED,SpeechConstant.CALLBACK_EVENT_ASR_FINISH,
                recogResult.getOrigalJson(),"ASR Finish",System.currentTimeMillis());
        sendStatusMessage(asrMessage);
    }

    /**
     * 长语音识别结束
     */
    @Override
    public void onAsrLongFinish() {
        super.onAsrLongFinish();
        Log.d(TAG, "onAsrLongFinish");
        AsrMessage asrMessage = new AsrMessage(status,SpeechConstant.CALLBACK_EVENT_ASR_LONG_SPEECH,
                "","long ASR Finish",System.currentTimeMillis());
        sendStatusMessage(asrMessage);
    }


    /**
     * 使用离线命令词时，有该回调说明离线语法资源加载成功
     */
    @Override
    public void onOfflineLoaded() {
        Log.d(TAG, "onOfflineLoaded");
        AsrMessage asrMessage = new AsrMessage(status,SpeechConstant.CALLBACK_EVENT_ASR_LOADED,
                "","离线资源加载成功。没有此回调可能离线语法功能不能使用。",System.currentTimeMillis());
        sendStatusMessage(asrMessage);
    }

    /**
     * 使用离线命令词时，有该回调说明离线语法资源加载成功
     */
    @Override
    public void onOfflineUnLoaded() {
        Log.d(TAG, "onOfflineUnLoaded");
        AsrMessage asrMessage = new AsrMessage(status,SpeechConstant.CALLBACK_EVENT_ASR_UNLOADED,
                "","离线资源卸载成功",System.currentTimeMillis());
        sendStatusMessage(asrMessage);
    }

    @Override
    public void onAsrExit() {
        super.onAsrExit();
        Log.d(TAG, "onAsrExit");
        AsrMessage asrMessage = new AsrMessage(status,SpeechConstant.CALLBACK_EVENT_ASR_EXIT,
                "","Asr finish, Engine to ready",System.currentTimeMillis());
        sendStatusMessage(asrMessage);
    }

    private void sendStatusMessage(AsrMessage asrMessage) {

        if (handler == null) {
            Log.d(TAG, "sendStatusMessage: handler is null");
            return;
        }
        Log.d(TAG, "sendStatusMessage: " + asrMessage.toString());

        Message msg = Message.obtain();
        msg.what = ASR_MESSAGE;
        msg.arg1 = asrMessage.getAsrStatus();

        msg.obj = asrMessage;
        handler.sendMessage(msg);
    }

    private void sendStatusMessage(String message, int what, boolean highlight) {
        if ( what != STATUS_FINISHED) {
            message += "  ;time=" + System.currentTimeMillis();
        }
        if (handler == null) {
            Log.i(TAG, message);
            return;
        }
        Message msg = Message.obtain();
        msg.what = ASR_MESSAGE;
        msg.arg1 = what;

        if (highlight) {
            msg.arg2 = 1;
        }
        msg.obj = message + " ";
        handler.sendMessage(msg);
    }
}
