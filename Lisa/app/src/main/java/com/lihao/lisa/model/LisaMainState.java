package com.lihao.lisa.model;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.baidu.aip.asrwakeup3.core.inputstream.InFileStream;
import com.baidu.aip.asrwakeup3.core.recog.MyRecognizer;
import com.baidu.aip.asrwakeup3.core.recog.listener.IRecogListener;
import com.baidu.aip.asrwakeup3.core.recog.listener.MessageStatusRecogListener;
import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.lihao.lisa.model.core.baidu.tts.BaiduTTS;
import com.lihao.lisa.model.core.baidu.tts.listener.MessageListener;
import com.lihao.lisa.model.core.baidu.tts.listener.UiMessageListener;
import com.lihao.lisa.model.core.baidu.wakeup.BaiduWakeup;
import com.lihao.lisa.model.core.base.BaseTTS;
import com.lihao.lisa.model.core.base.FactoryTTS;
import com.lihao.lisa.model.features.weather.Weather;
import com.lihao.lisa.model.util.statemachine.State;
import com.lihao.lisa.model.util.statemachine.StateMachine;
import com.lihao.lisa.presenter.PresenterListener;
import com.lihao.lisa.util.ASRMessage;
import com.lihao.lisa.util.BaseMessage;
import com.lihao.lisa.util.TTSMessage;
import com.lihao.lisa.util.WeatherMessage;

import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import static com.baidu.aip.asrwakeup3.core.recog.IStatus.ASR_MESSAGE;
import static com.baidu.aip.asrwakeup3.core.recog.IStatus.STATUS_ALL_FINISHED;
import static com.baidu.aip.asrwakeup3.core.recog.IStatus.STATUS_FINISHED;
import static com.baidu.aip.asrwakeup3.core.recog.IStatus.STATUS_NLU_FINISHED;
import static com.baidu.aip.asrwakeup3.core.recog.IStatus.STATUS_NONE;
import static com.baidu.aip.asrwakeup3.core.recog.IStatus.STATUS_READY;
import static com.baidu.aip.asrwakeup3.core.recog.IStatus.STATUS_RECOGNITION;
import static com.baidu.aip.asrwakeup3.core.recog.IStatus.STATUS_SPEAKING;
import static com.lihao.lisa.model.core.baidu.tts.MainHandlerConstant.UI_CHANGE_INPUT_TEXT_SELECTION;
import static com.lihao.lisa.model.core.baidu.tts.MainHandlerConstant.UI_CHANGE_SYNTHES_TEXT_FINISHED;
import static com.lihao.lisa.model.core.base.FactoryTTS.AISPEECH_ONLINE_TTS;
import static com.lihao.lisa.model.core.base.FactoryTTS.BAIDU_ONLINE_TTS;
import static com.lihao.lisa.model.core.base.FactoryTTS.IFLYTEK_ONLINE_TTS;
import static com.lihao.lisa.util.BaseMessage.EUROPECUP_MESSAGE;
import static com.lihao.lisa.util.BaseMessage.EXCUTE_MESSAGE;
import static com.lihao.lisa.util.BaseMessage.TTS_MESSAGE;
import static com.lihao.lisa.util.BaseMessage.WAKEUP_MESSAGE;
import static com.lihao.lisa.util.BaseMessage.WEATHER_MESSAGE;
import static com.lihao.lisa.util.TTSMessage.TTS_STATUS_INPUT_TEXT_FINISHED;
import static com.lihao.lisa.util.TTSMessage.TTS_STATUS_INPUT_TEXT_SELECTION;
import static com.lihao.lisa.util.TTSMessage.TTS_STATUS_SYNTHES_TEXT_FINISHED;

public class LisaMainState extends StateMachine {
    private static final String TAG = "LisaMainState";
    private static final String TAG_STATE = "LisaMainState_State";
    private static LisaMainState mInstance = null;

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    private Context mContext = null;
    PresenterListener mListener = null;

    //Define the instance of components
    private BaseTTS mTTSInstance = null;
    private MyRecognizer mRecognizer = null;
    private BaiduWakeup mWakeup = null;
    private Handler mHandler = null;  //Define the handler of ASR engine
    private Dispatcher mDispatcher = null;

    //Define the events used in this state machine
    public static final int EVENT_INIT   = 1;
    public static final int EVENT_INIT_SUCCESS = 2;
    public static final int EVENT_PTT = 3;
    public static final int EVENT_START_TTS_END = 4;
    public static final int EVENT_SPEAK_START = 5;
    public static final int EVENT_ASR_READY = 6;
    public static final int EVENT_SPEAK_FINISHED = 7;
    public static final int EVENT_SPEAK_END = 8;
    public static final int EVENT_NLU_SUCCESS = 9;
    public static final int EVENT_FEEDBACK = 10;
    public static final int EVENT_TTS_PROGRESS = 11;
    public static final int EVENT_CANCEL = 12;
    public static final int EVENT_STOP = 13;

    //Instantiate the state
    private Idle mIdle = new Idle();
    private Initialize mInitialize = new Initialize();
    private Ready mReady = new Ready();
    private Greating mGreating = new Greating();
    private StartASR mStartASR = new StartASR();
    private AsrReady mAsrReady = new AsrReady();
    private Speaking mSpeaking = new Speaking();
    private Recognition mRecognition = new Recognition();
    private Execute mExecute = new Execute();
    private Prompt mPrompt = new Prompt();
    private Root mRoot = new Root();
    private Deinitialize mDeinitialize = new Deinitialize();


    public LisaMainState(String name, Context context, PresenterListener listener) {
        super(name,Looper.getMainLooper());
        Log.d(TAG, "ctor E");
        mContext = context;
        mListener = listener; //////

        // Add states, use indentation to show hierarchy
        addState(mRoot);
        addState(mIdle, mRoot);
        addState(mInitialize, mRoot);
        addState(mReady, mRoot);
        addState(mGreating, mRoot);
        addState(mStartASR, mRoot);
        addState(mAsrReady, mRoot);
        addState(mSpeaking, mRoot);
        addState(mRecognition, mRoot);
        addState(mExecute, mRoot);
        addState(mPrompt, mRoot);
        addState(mDeinitialize, mRoot);

        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Log.d(TAG, "handleMessage: msg:" + msg.toString());

                switch( msg.what ){
                    case ASR_MESSAGE:
                        ProcessAsrMessage(msg);
                        break;
                    case TTS_MESSAGE:
                        ProcessTTSMessage(msg);
                        break;
                    case WAKEUP_MESSAGE:
                        Log.d(TAG, "handleMessage: Wakeup_message");
                        mInstance.sendMessage(obtainMessage(EVENT_PTT));
                        break;
                    case WEATHER_MESSAGE:
                        WeatherMessage weatherMsg = (WeatherMessage)msg.obj;
                        Log.d(TAG_STATE, "handleMessage: weatherMsg: " + weatherMsg.toString());
                        mInstance.sendMessage(obtainMessage(EVENT_FEEDBACK, weatherMsg));
                        break;
                    case EXCUTE_MESSAGE:
                        mInstance.sendMessage(obtainMessage(EVENT_FEEDBACK, msg.obj));
                        break;
                    case EUROPECUP_MESSAGE:
                        mInstance.sendMessage(obtainMessage(EVENT_FEEDBACK, msg.obj));
                        break;
                    default:
                        break;
                }
            }
        };

        // Set the initial state
        setInitialState(mIdle);
        Log.d(TAG, "ctor X");
    }

    public static LisaMainState getInstance(Context context, PresenterListener listener) {
        if(mInstance == null){
            synchronized (LisaMainState.class){
                if(mInstance == null){
                    mInstance = new LisaMainState(TAG, context, listener);
                    mInstance.start();
                }
            }
        }
        return mInstance;
    }

    public void setListener(PresenterListener mListener) {
        this.mListener = mListener;
    }

    public void startSpeak(String text){
        mTTSInstance.StartSpeak(text);
    }

    public void ChangeTTSEngine(String text){
        Log.d(TAG, "ChangeTTSEngine: text: " + text);
        if(!text.equals(mTTSInstance.GetTTSEngineName())){
            mTTSInstance.Destroy();
            if(text.equals("iFlytek")){
                mTTSInstance = FactoryTTS.CreateTTS(IFLYTEK_ONLINE_TTS);
            }else if(text.equals("baidu")){
                mTTSInstance = FactoryTTS.CreateTTS(BAIDU_ONLINE_TTS);
            }else if(text.equals("AISpeech")){
                mTTSInstance = FactoryTTS.CreateTTS(AISPEECH_ONLINE_TTS);
            }else{
                Log.e(TAG, "ChangeTTSEngine: Not match");
            }

            try {
                mTTSInstance.InitializeTTS(mContext, mHandler);
            } catch (CertificateEncodingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean initRecognizer(){
        IRecogListener listener = new MessageStatusRecogListener(mHandler);
        mRecognizer = new MyRecognizer(mContext, listener);
        return true;
    }

    private boolean initWakeup(){
        mWakeup = BaiduWakeup.getInstance(mContext,mHandler);
        mWakeup.InitWakeup();
        return true;
    }

    private boolean initTTS() throws NoSuchAlgorithmException, CertificateEncodingException {
        mTTSInstance = FactoryTTS.CreateTTS(BAIDU_ONLINE_TTS);
        mTTSInstance.InitializeTTS(mContext, mHandler);
        return true;
    }

    private boolean initDispatcher(){
        mDispatcher = Dispatcher.getInstance(mContext, mHandler);
        return true;
    }

    private void startASR()
    {
        Map<String, Object> params = new HashMap<String,Object>();
        params.put("pid",15373);
        params.put("accept-audio-volume",false);
        Log.d(TAG, "startASR: params: " + params);
        mRecognizer.start(params);
    }

    private void ProcessAsrMessage(Message msg) {
//        Log.d(TAG, "ProcessAsrMessage: msg.what: " + msg.what +
//                " msg.arg1: " + msg.arg1 + " msg.arg2: " + msg.arg2);
        JSONObject jsonObject;
        String asrResult;
        ASRMessage asrMsg = null;
        if (msg.obj != null) {
            //Use the long package name for engineer to notice the define position
            asrMsg = new ASRMessage((com.baidu.aip.asrwakeup3.core.util.AsrMessage)msg.obj);
            Log.d(TAG, "ProcessAsrMessage: msg.obj: " + asrMsg.toString());

            switch (msg.arg1) {
                case STATUS_READY:
                    sendMessage(obtainMessage(EVENT_ASR_READY, asrMsg));
                    break;
                case STATUS_SPEAKING:
                    sendMessage(obtainMessage(EVENT_SPEAK_START, asrMsg));
                    break;
                case STATUS_FINISHED:
                    sendMessage(obtainMessage(EVENT_SPEAK_FINISHED, asrMsg));
                    break;
                case STATUS_RECOGNITION:
                    sendMessage(obtainMessage(EVENT_SPEAK_END));
                    break;
                case STATUS_NLU_FINISHED:
                    sendMessage(obtainMessage(EVENT_NLU_SUCCESS, asrMsg));
                    break;
                case STATUS_ALL_FINISHED:
                    break;
                case STATUS_NONE:
                    break;
                default:
                    Log.w(TAG, "handleMessage: Not support message");
                    break;
            }
        }
    }

    private void ProcessTTSMessage(Message msg) {
        if (msg.obj != null) {
            TTSMessage ttsMsg = (TTSMessage)msg.obj;
            Log.d(TAG, "ProcessTTSMessage: ttsMsg: " + ttsMsg.toString());

            switch (ttsMsg.getTTSStatus()) {
                case TTS_STATUS_INPUT_TEXT_FINISHED:
                    Log.d(TAG_STATE, "handleMessage: TTS Play finished");
                    mInstance.sendMessage(obtainMessage(EVENT_START_TTS_END, ttsMsg));
                    break;
                case TTS_STATUS_INPUT_TEXT_SELECTION:
                    mInstance.sendMessage(obtainMessage(EVENT_TTS_PROGRESS, ttsMsg));
                    break;
                default:
                    Log.w(TAG, "ProcessTTSMessage: Not support message");
                    break;
            }
        }
    }

    //Define the State
    class Root extends State {
        @Override public void enter() {
            Log.d(TAG_STATE, "Root.enter");
        }
        @Override public boolean processMessage(Message message) {
            boolean retVal = HANDLED;
            Log.d(TAG_STATE, "Root.processMessage what=" + message.what);
            switch(message.what) {
                case EVENT_CANCEL:
                    Interrupt();
                    transitionTo(mReady);
                    break;
                case EVENT_STOP:
                    Interrupt();
                    transitionTo(mDeinitialize);
                    break;
                default:
                    retVal = NOT_HANDLED;
                    break;
            }
            return retVal;
        }
        @Override public void exit() {
            Log.d(TAG_STATE, "Root.exit");
        }
    }

    class Idle extends State {
        @Override public void enter() {
            Log.d(TAG_STATE, "Idle.enter");
        }
        @Override public boolean processMessage(Message message) {
            boolean retVal;
            Log.d(TAG_STATE, "Idle.processMessage what=" + message.what);
            switch(message.what) {
                case EVENT_INIT:
                    retVal = HANDLED;
                    transitionTo(mInitialize);
                    break;
                default:
                    // Any message we don't understand in this state invokes unhandledMessage
                    retVal = NOT_HANDLED;
                    break;
            }
            return retVal;
        }
        @Override public void exit() {
            Log.d(TAG_STATE, "Idle.exit");
        }
    }

    class Initialize extends State {
        @Override public void enter() {
            Log.d(TAG_STATE, "Initialize.enter");
            //Start to initialize component
            //Initialize ASR
            initRecognizer();

            //Initialize TTS
            try {
                initTTS();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (CertificateEncodingException e) {
                e.printStackTrace();
            }

            //Initialize wakeup
            initWakeup();

            //Initialize Dispather
            initDispatcher();

            sendMessage(obtainMessage(EVENT_INIT_SUCCESS));
        }

        @Override public boolean processMessage(Message message) {
            boolean retVal;
            Log.d(TAG_STATE, "Initialize.processMessage what=" + message.what);
            switch(message.what) {
                case EVENT_INIT_SUCCESS:
                    transitionTo(mReady);
                    retVal = HANDLED;
                    break;
                default:
                    retVal = NOT_HANDLED;
                    break;
            }
            return retVal;
        }
        @Override public void exit() {
            Log.d(TAG_STATE, "Initialize.exit");
            mListener.onShowReady(null);
        }
    }

    class Ready extends State {
        @Override public void enter() {
            Log.d(TAG_STATE, "Ready.enter");
            mWakeup.StartWakeup();
        }
        @Override public boolean processMessage(Message message) {
            boolean retVal;
            Log.d(TAG_STATE, "Ready.processMessage what=" + message.what);
            switch(message.what) {
                case EVENT_PTT:
                    transitionTo(mGreating);
                    retVal = HANDLED;
                    break;
                default:
                    retVal = NOT_HANDLED;
                    break;
            }
            return retVal;
        }
        @Override public void exit() {
            Log.d(TAG_STATE, "Ready.exit");
        }
    }

    class Greating extends State {
        private String mPlayTTS;
        @Override public void enter() {
            Log.d(TAG_STATE, "Greating.enter");
            String ttsStr[] = {"有什么可以帮您？","我在！","我在呢！","主人，我能为您做点什么？","你好！"};

            Random ran =new Random(System.currentTimeMillis());
            int index = ran.nextInt(ttsStr.length);
            mPlayTTS = ttsStr[index];
            mTTSInstance.StartSpeak(mPlayTTS);
            mListener.onShowTTSPlaying(new TTSMessage(BaseMessage.RESULT_SUCCESS,
                    mPlayTTS,TTS_STATUS_INPUT_TEXT_FINISHED));
        }

        @Override public boolean processMessage(Message message) {
            boolean retVal = HANDLED;
            Log.d(TAG_STATE, "Greating.processMessage what=" + message.toString());
            switch(message.what) {
                case EVENT_START_TTS_END:
                    //mListener.onShowTTSPlaying((TTSMessage)message.obj);//TAG:Support show words one by one
                    transitionTo(mStartASR);
                    break;
                case EVENT_TTS_PROGRESS:
                    //mListener.onShowTTSPlaying((TTSMessage)message.obj);//TAG:Support show words one by one
                    break;
                default:
                    retVal = NOT_HANDLED;
                    break;
            }
            return retVal;
        }
        @Override public void exit() {
            Log.d(TAG_STATE, "Greating.exit");
        }
    }

    class StartASR extends State {
        @Override public void enter() {
            Log.d(TAG_STATE, "StartASR.enter");
            startASR();
        }

        @Override public boolean processMessage(Message message) {
            boolean retVal;
            Log.d(TAG_STATE, "StartASR.processMessage what=" + message.what);
            switch(message.what) {
                case EVENT_ASR_READY:
                    transitionTo(mAsrReady);
                    retVal = HANDLED;
                    break;
                default:
                    retVal = NOT_HANDLED;
                    break;
            }
            return retVal;
        }
        @Override public void exit() {
            Log.d(TAG_STATE, "StartASR.exit");
        }
    }


    class AsrReady extends State {
        @Override public void enter() {
            Log.d(TAG_STATE, "AsrReady.enter");
            startASR();
        }

        @Override public boolean processMessage(Message message) {
            boolean retVal;
            Log.d(TAG_STATE, "AsrReady.processMessage what=" + message.what);
            switch(message.what) {
                case EVENT_SPEAK_START:
                    transitionTo(mSpeaking);
                    retVal = HANDLED;
                    break;
                default:
                    retVal = NOT_HANDLED;
                    break;
            }
            return retVal;
        }
        @Override public void exit() {
            Log.d(TAG_STATE, "AsrReady.exit");
        }
    }

    class Speaking extends State {
        @Override public void enter() {
            Log.d(TAG_STATE, "Speaking.enter");
        }
        @Override public boolean processMessage(Message message) {
            boolean retVal = HANDLED;
            Log.d(TAG_STATE, "Speaking.processMessage what=" + message.what);
            ASRMessage asr = (ASRMessage)message.obj;
            if(asr!= null)
                Log.d(TAG, "Speaking.processMessage: Message:" + asr.toString());
            else
                Log.d(TAG, "Speaking.processMessage: ASR message is null");
            switch(message.what) {
                case EVENT_SPEAK_START:
                    mListener.onShowSpeaking(message);
                    break;
                case EVENT_SPEAK_FINISHED:
                    mListener.onShowFinished(message);
                    break;
                case EVENT_SPEAK_END:
                    transitionTo(mRecognition);
                    break;
                default:
                    retVal = NOT_HANDLED;
                    break;
            }
            return retVal;
        }
        @Override public void exit() {
            Log.d(TAG_STATE, "Speaking.exit");
        }
    }

    class Recognition extends State {
        @Override public void enter() {
            Log.d(TAG_STATE, "Recognition.enter");

        }
        @Override public boolean processMessage(Message message) {
            boolean retVal;
            Log.d(TAG_STATE, "Recognition.processMessage what=" + message.what);
            switch(message.what) {
                case EVENT_NLU_SUCCESS:
                    transitionTo(mExecute);
                    deferMessage(message);
                    retVal = HANDLED;
                    break;
                case EVENT_SPEAK_FINISHED:
                    mListener.onShowFinished(message);
                default:
                    retVal = NOT_HANDLED;
                    break;
            }
            return retVal;
        }
        @Override public void exit() {
            Log.d(TAG_STATE, "Recognition.exit");
        }
    }

    class Execute extends State {
        @Override public void enter() {
            Log.d(TAG_STATE, "Execute.enter");
            //transitionTo(mPrompt);
        }
        @Override public boolean processMessage(Message message) {
            boolean retVal;
            Log.d(TAG_STATE, "Execute.processMessage what=" + message.what);
            switch(message.what) {
                case EVENT_NLU_SUCCESS:
                    mDispatcher.DispatchMsg((ASRMessage)message.obj);
                case EVENT_FEEDBACK:
                    deferMessage(message);
                    transitionTo(mPrompt);
                    retVal = HANDLED;
                    break;
                default:
                    retVal = NOT_HANDLED;
                    break;
            }
            return retVal;
        }
        @Override public void exit() {
            Log.d(TAG_STATE, "Execute.exit");
        }
    }

    class Prompt extends State {
        @Override public void enter() {
            Log.d(TAG_STATE, "Prompt.enter");
        }

        @Override public boolean processMessage(Message message) {
            boolean retVal = HANDLED;
            Log.d(TAG_STATE, "Prompt.processMessage what=" + message.what);
            switch(message.what) {
                case EVENT_START_TTS_END:
                    //mListener.onShowFinished(message);
                    transitionTo(mReady);
                    break;
                case EVENT_FEEDBACK:
                    if(message.obj != null){
                        mListener.onShowResult(message);
                        mTTSInstance.StartSpeak(((BaseMessage)(message.obj)).getShowMessage());
                    }
                    break;
                case EVENT_TTS_PROGRESS:
                    break;
                default:
                    retVal = NOT_HANDLED;
                    break;
            }
            return retVal;
        }
        @Override public void exit() {
            mListener.onShowExit(null);
            Log.d(TAG_STATE, "Prompt.exit");
        }
    }

    class Deinitialize extends State {
        @Override public void enter() {
            Log.d(TAG_STATE, "Deinitialize.enter");

            mRecognizer.release();

            transitionTo(mIdle);
        }
        @Override public boolean processMessage(Message message) {
            boolean retVal;
            Log.d(TAG_STATE, "Deinitialize.processMessage what=" + message.what);
            switch(message.what) {
                default:
                    // Any message we don't understand in this state invokes unhandledMessage
                    retVal = NOT_HANDLED;
                    break;
            }
            return retVal;
        }
        @Override public void exit() {
            Log.d(TAG_STATE, "Deinitialize.exit");
        }
    }

    @Override
    public void onHalting() {
        Log.d(TAG, "halting");
        synchronized (this) {
            this.notifyAll();
        }
    }

    private void Interrupt(){
        mTTSInstance.StopSpeak();
        mRecognizer.cancel();
        mDispatcher.Cancel();
    }



}