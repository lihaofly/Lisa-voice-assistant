package com.lihao.lisa.util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class MyLogger {
    private static final String TAG = "LisaVoiceAssistant";

    private static final String DEBUG = "DEBUG";
    private static final String INFO = "INFO";
    private static final String WARNING = "WARNING";
    private static final String ERROR = "ERROR";

    private static final boolean ENABLE = true;

    private static Handler handler;

    public static void debug(String tag, String message) {
        log(DEBUG, tag, message);
    }

    public static void info(String tag, String message) {
        log(INFO, tag, message);
    }

    public static void error(String tag, String message) {
        log(ERROR, tag, message);
    }

    public static void warning(String tag, String message) {
        log(ERROR, tag, message);
    }

    public static void setHandler(Handler handler) {
        MyLogger.handler = handler;
    }

    private static void log(String level, String subTag, String message) {
        if (ENABLE) {
            if (level.equals(DEBUG)) {
                Log.d(TAG, "[" + subTag +"] " + message);
            }else if (level.equals(INFO)) {
                Log.i(TAG, "[" + subTag +"] " +message);
            }else if (level.equals(WARNING)) {
                Log.w(TAG, "[" + subTag +"] " +message);
            }else if (level.equals(ERROR)) {
                Log.e(TAG, "[" + subTag +"] " +message);
            }

            if (handler != null) {
                Message msg = Message.obtain();
                msg.obj = "[" + level + "]" + message + "\n";
                handler.sendMessage(msg);
            }
        }
    }
}
