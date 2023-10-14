package com.baidu.aip.asrwakeup3.core.recog;

/**
 * Created by fujiayi on 2017/6/14.
 */

public interface IStatus {

    int ASR_MESSAGE = 1;
    int STATUS_NONE = 2;

    int STATUS_READY = 3;
    int STATUS_SPEAKING = 4;
    int STATUS_RECOGNITION = 5;

    int STATUS_FINISHED = 6;
    int STATUS_LONG_SPEECH_FINISHED = 7;
    int STATUS_NLU_FINISHED = 8;
    int STATUS_ALL_FINISHED = 9;
    int STATUS_STOPPED = 10;

    int STATUS_WAITING_READY = 8001;
    int WHAT_MESSAGE_STATUS = 9001;

    int STATUS_WAKEUP_SUCCESS = 7001;
    int STATUS_WAKEUP_EXIT = 7003;
}
