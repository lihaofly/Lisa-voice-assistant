package com.lihao.lisa.model.core.BaiduSolution.tts.listener;

import android.os.Handler;

import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizerListener;

import com.lihao.lisa.model.core.base.BaseSynthesizerListener;
import com.lihao.lisa.util.MyLogger;

/**
 * @ClassName BaiduTTSListener
 * @Author lihao.fly@163.com
 * @Data 2023/10/23
 * @Description Register to SDK listen the status of Speech Synthesizer
 */

public class BaiduTTSListener extends BaseSynthesizerListener implements SpeechSynthesizerListener {
    private static final String TAG = BaiduTTSListener.class.getSimpleName();

    public BaiduTTSListener(Handler handler) {
        super(handler);
    }

    @Override
    public void onSpeechStart(String utteranceId) {
        MyLogger.debug(TAG, "Play Start, Serial number: " + utteranceId);
        onBaseSpeakStart();
    }

    /**
     * 播放进度回调接口，分多次回调
     *
     * @param utteranceId
     * @param progress    如合成“百度语音问题”这6个字， progress肯定是从0开始，到6结束。 但progress无法保证和合成到第几个字对应。
     */
    @Override
    public void onSpeechProgressChanged(String utteranceId, int progress) {
        MyLogger.debug(TAG, "Play progress：" + progress + " Serial number:" + utteranceId );
        onBaseSpeakProgress(progress, 0,0);
    }

    /**
     * 播放正常结束，每句播放正常结束都会回调，如果过程中出错，则回调onError,不再回调此接口
     *
     * @param utteranceId
     */
    @Override
    public void onSpeechFinish(String utteranceId) {
        MyLogger.debug(TAG, "Play End, Serial number: " + utteranceId);
        onBaseSpeakCompleted();
    }

    /**
     * 播放开始，每句播放开始都会回调
     *
     * @param utteranceId
     */
    @Override
    public void onSynthesizeStart(String utteranceId) {
        MyLogger.debug(TAG, "Synthesize Start, Serial number: " + utteranceId);
    }

    /**
     * 语音流 16K采样率 16bits编码 单声道 。
     *
     * @param utteranceId
     * @param bytes       二进制语音 ，注意可能有空data的情况，可以忽略
     * @param progress    如合成“百度语音问题”这6个字， progress肯定是从0开始，到6结束。 但progress无法和合成到第几个字对应。
     *                    engineType 下版本提供。1:音频数据由离线引擎合成； 0：音频数据由在线引擎（百度服务器）合成。
     */

    public void onSynthesizeDataArrived(String utteranceId, byte[] bytes, int progress) {
        MyLogger.debug(TAG, "Voice synthesis progress: " + progress + " Serial number: "+ utteranceId);
    }

    @Override
    // engineType 下版本提供。1:音频数据由离线引擎合成； 0：音频数据由在线引擎（百度服务器）合成。
    public void onSynthesizeDataArrived(String utteranceId, byte[] bytes, int progress, int engineType) {
        onSynthesizeDataArrived(utteranceId, bytes, progress);
        MyLogger.debug(TAG, "Synthesize Type: " + (engineType == 1? "Offline":"Online"));
    }

    /**
     * 合成正常结束，每句合成正常结束都会回调，如果过程中出错，则回调onError，不再回调此接口
     *
     * @param utteranceId
     */
    @Override
    public void onSynthesizeFinish(String utteranceId) {
        MyLogger.debug(TAG, "Synthesize end, Serial number: " + utteranceId);
    }

    /**
     * 当合成或者播放过程中出错时回调此接口
     *
     * @param utteranceId
     * @param speechError 包含错误码和错误信息
     */
    @Override
    public void onError(String utteranceId, SpeechError speechError) {
        MyLogger.error(TAG,"Error happen:" + speechError.description + ", Error code: "
                + speechError.code + ", Serial number:" + utteranceId);
        onBaseSpeakError(speechError.toString());
    }

}
