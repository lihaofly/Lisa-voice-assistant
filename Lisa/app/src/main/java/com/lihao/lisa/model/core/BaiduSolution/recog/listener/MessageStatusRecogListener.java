package com.lihao.lisa.model.core.BaiduSolution.recog.listener;

import android.os.Handler;
import android.os.Message;
import com.baidu.aip.asrwakeup3.core.util.AsrMessage;
import com.baidu.speech.asr.SpeechConstant;
import com.lihao.lisa.model.core.BaiduSolution.recog.listener.IRecogListener;
import com.lihao.lisa.util.MyLogger;

public class MessageStatusRecogListener implements IRecogListener, IStatus {
    private static final String TAG = MessageStatusRecogListener.class.getSimpleName();
    private Handler handler;
    private long speechEndTime = 0;
    private int status = STATUS_NONE;
    private final int ASR_MESSAGE = 1;

    public MessageStatusRecogListener(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onAsrReady() {
        MyLogger.debug(TAG, "onAsrReady()");
        status = STATUS_READY;
        speechEndTime = 0;
        AsrMessage asrMessage = new AsrMessage(status,SpeechConstant.CALLBACK_EVENT_WAKEUP_READY,
                "","Asr Engine ready",System.currentTimeMillis());
        sendStatusMessage(asrMessage);
    }

    @Override
    public void onAsrBegin() {
        MyLogger.debug(TAG, "onAsrBegin()");
        status = STATUS_SPEAKING;
        AsrMessage asrMessage = new AsrMessage(status,SpeechConstant.CALLBACK_EVENT_ASR_BEGIN,
                "","Detected speaking begin",System.currentTimeMillis());
        sendStatusMessage(asrMessage);
    }

    @Override
    public void onAsrEnd() {
        MyLogger.debug(TAG, "onAsrEnd()");
        status = STATUS_RECOGNITION;
        speechEndTime = System.currentTimeMillis();
        AsrMessage asrMessage = new AsrMessage(status,SpeechConstant.CALLBACK_EVENT_ASR_END,
                "","Detected speaking ending",System.currentTimeMillis());
        sendStatusMessage(asrMessage);
    }

    @Override
    public void onAsrPartialResult(String[] results, RecogResult recogResult) {
        MyLogger.debug(TAG, "onAsrPartialResult()");
        AsrMessage asrMessage = new AsrMessage(status,SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL,
                recogResult.getOrigalJson(),"Detected speaking in process",System.currentTimeMillis());
        sendStatusMessage(asrMessage);
    }

    @Override
    public void onAsrFinalResult(String[] results, RecogResult recogResult) {
        MyLogger.debug(TAG, "onAsrFinalResult()");
        status = STATUS_FINISHED;

        AsrMessage asrMessage = new AsrMessage(status,SpeechConstant.CALLBACK_EVENT_ASR_FINISH,
                recogResult.getOrigalJson(),"Asr Finished",System.currentTimeMillis());
        sendStatusMessage(asrMessage);

        if (speechEndTime > 0) {
            String message = "Recognize finish, Result: ”" + results[0] + "”";
            long currentTime = System.currentTimeMillis();
            long diffTime = currentTime - speechEndTime;
            message += "; Duration from start speaking to end [" + diffTime + "ms]" + currentTime;
            MyLogger.debug(TAG, "onAsrFinalResult: message:" + message);
        }
        speechEndTime = 0;
    }

    @Override
    public void onAsrFinishError(int errorCode, int subErrorCode, String descMessage,
                                 RecogResult recogResult) {
        MyLogger.debug(TAG, "onAsrFinishError()");
        status = STATUS_FINISHED;
        AsrMessage asrMessage = new AsrMessage(status,SpeechConstant.CALLBACK_EVENT_ASR_ERROR,
                recogResult.getOrigalJson(),"Asr Error happened",System.currentTimeMillis());
        sendStatusMessage(asrMessage);

        if (speechEndTime > 0) {
            String message = "[asr.finish event] Recongnize failed. Error code: " + errorCode + " ," + subErrorCode + " ; " + descMessage;
            long diffTime = System.currentTimeMillis() - speechEndTime;
            message += ". Duration from start speaking to end[" + diffTime + "ms]";
            MyLogger.debug(TAG, "onAsrFinishError: error: " + message);
        }
        speechEndTime = 0;
    }

    @Override
    public void onAsrOnlineNluResult(String nluResult) {
        MyLogger.debug(TAG, "onAsrOnlineNluResult()");
        status = STATUS_FINISHED;
        if (!nluResult.isEmpty()) {
            AsrMessage asrMessage = new AsrMessage(STATUS_NLU_FINISHED,SpeechConstant.CALLBACK_EVENT_ASR_FINISH,
                    nluResult,"Online ASR in processing",System.currentTimeMillis());
            sendStatusMessage(asrMessage);
        }
    }

    @Override
    public void onAsrFinish(RecogResult recogResult) {
        MyLogger.debug(TAG, "onAsrFinish()");
        status = STATUS_FINISHED;
        AsrMessage asrMessage = new AsrMessage(STATUS_ALL_FINISHED,SpeechConstant.CALLBACK_EVENT_ASR_FINISH,
                recogResult.getOrigalJson(),"ASR Finish",System.currentTimeMillis());
        sendStatusMessage(asrMessage);
    }

    @Override
    public void onAsrLongFinish() {
        MyLogger.debug(TAG, "onAsrLongFinish()");
        status = STATUS_LONG_SPEECH_FINISHED;
        AsrMessage asrMessage = new AsrMessage(status,SpeechConstant.CALLBACK_EVENT_ASR_LONG_SPEECH,
                "","long ASR Finish",System.currentTimeMillis());
        sendStatusMessage(asrMessage);
    }

    @Override
    public void onOfflineLoaded() {
        MyLogger.debug(TAG, "onOfflineLoaded()");
        AsrMessage asrMessage = new AsrMessage(status,SpeechConstant.CALLBACK_EVENT_ASR_LOADED,
                "","离线资源加载成功。没有此回调可能离线语法功能不能使用。",System.currentTimeMillis());
        sendStatusMessage(asrMessage);
    }

    @Override
    public void onOfflineUnLoaded() {
        MyLogger.debug(TAG, "onOfflineUnLoaded()");
        AsrMessage asrMessage = new AsrMessage(status,SpeechConstant.CALLBACK_EVENT_ASR_UNLOADED,
                "","Unload offline resource successful",System.currentTimeMillis());
        sendStatusMessage(asrMessage);
    }

    @Override
    public void onAsrExit() {
        MyLogger.debug(TAG, "onAsrExit()");
        status = STATUS_NONE;
        AsrMessage asrMessage = new AsrMessage(status,SpeechConstant.CALLBACK_EVENT_ASR_EXIT,
                "","Asr finish, Engine to ready",System.currentTimeMillis());
        sendStatusMessage(asrMessage);
    }

    @Override
    public void onAsrAudio(byte[] data, int offset, int length) {
        MyLogger.debug(TAG, "onAsrAudio()");
        if (offset != 0 || data.length != length) {
            byte[] actualData = new byte[length];
            System.arraycopy(data, 0, actualData, 0, length);
            data = actualData;
        }

        MyLogger.debug(TAG, "Audio data callback, length:" + data.length);
    }

    @Override
    public void onAsrVolume(int volumePercent, int volume) {
        MyLogger.debug(TAG, "onAsrVolume()");
        MyLogger.debug(TAG, "Audio volume percentage: " + volumePercent + " ; volume: " + volume);
    }


    private void sendStatusMessage(AsrMessage asrMessage) {

        if (handler == null) {
            MyLogger.debug(TAG, "sendStatusMessage: handler is null");
            return;
        }
        MyLogger.debug(TAG, "sendStatusMessage: " + asrMessage.toString());

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
            MyLogger.info(TAG, message);
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
