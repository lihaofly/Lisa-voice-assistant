package com.lihao.lisa.view;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.baidu.aip.asrwakeup3.core.util.AsrMessage;
import com.google.gson.JsonObject;
import com.lihao.lisa.R;
import com.lihao.lisa.model.core.baidu.tts.BaiduTTS;
import com.lihao.lisa.model.core.baidu.tts.TTSTestActivity;
import com.lihao.lisa.model.core.iFlytek.tts.iFlytekTTS;
import com.lihao.lisa.model.features.InformationSearch.InformationSearch;
import com.lihao.lisa.model.features.weather.Weather;
import com.lihao.lisa.presenter.LisaPresenter;
import com.lihao.lisa.util.ASRMessage;
import com.lihao.lisa.util.AssistantMessage;
import com.lihao.lisa.util.BaseMessage;
import com.lihao.lisa.util.GuideMessage;
import com.lihao.lisa.util.TTSMessage;
import com.lihao.lisa.util.WeatherMessage;
import com.lihao.lisa.view.util.FadingEdgeTopRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static com.lihao.lisa.util.BaseMessage.WEATHER_MESSAGE;
import static com.lihao.lisa.util.TTSMessage.TTS_STATUS_INPUT_TEXT_FINISHED;
import static com.lihao.lisa.util.TTSMessage.TTS_STATUS_SYNTHES_TEXT_FINISHED;
import static com.lihao.lisa.view.util.User.USER_TYPE_RECIEVER;
import static com.lihao.lisa.view.util.User.USER_TYPE_SENDER;
import static com.lihao.lisa.view.util.User.USER_TYPE_WEATHER;


public class MainActivity extends AppCompatActivity implements LisaView {
    private static final String TAG = "Lisa_MainActivity";
    private LottieAnimationView mLottieView;
    private ChatMessageBox mChatMessageBox;
    private LisaPresenter mPresenter;
    private TextView mPowerDecl;
    String strTemp;
    private iFlytekTTS iflytekTTS;
    ActivityResultLauncher mSettingLauncher = registerForActivityResult(new ResultContract(), new ActivityResultCallback<String>() {
        @Override
        public void onActivityResult(String result) {
            Log.d(TAG, "onActivityResult: result: " + result);

            try {
                JSONObject object = new JSONObject(result);
                String ttsEngine = object.getString("TTS_Engine");
                String asrEngine = object.getString("VR_Engine");
                if(ttsEngine != null){
                    Log.d(TAG, "onActivityResult: ttsEngine: " + ttsEngine);
                    mPresenter.ChangeTTSEngine(ttsEngine);
                    updateSupplierName(asrEngine,ttsEngine);
                }

                if(asrEngine != null ){
                    Log.d(TAG, "onActivityResult: asrEngine: " + asrEngine);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            

        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Log.d(TAG, "onCreate: " + strTemp);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);//Hide menu bar
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);//Hide status bar
        setContentView(R.layout.activity_main);
        initPermission();
        //View related init
        initWidget();

        setPresenter(new LisaPresenter(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_setting:
                //startActivity(new Intent(this, SettingActivity.class));
                mSettingLauncher.launch("");
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        mPresenter.stopEngine();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        if(mChatMessageBox!=null)
            mChatMessageBox.Refresh();

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: ");
    }

    private void initWidget() {
        Log.d(TAG, "initWidget: ");
        //mChatMessageBox = ChatMessageBox.getInstance(this, findViewById(R.id.recycler_gchat));
        mChatMessageBox = new ChatMessageBox(getApplicationContext(), findViewById(R.id.recycler_gchat));
        mPowerDecl = (TextView) findViewById(R.id.PowerDecl);
        EditText inputTex = (EditText)findViewById(R.id.edit_gchat_message);
        Button sendBnt = (Button)findViewById(R.id.button_gchat_send);
        sendBnt.setOnClickListener(new View.OnClickListener(){
            @NonNull
            @Override
            public String toString() {
                return super.toString();
            }

            @Override
            public void onClick(View v) {
                String inputMeg = inputTex.getText().toString();
                engineerTest(inputMeg);
                inputTex.setText("");
            }
        });

        mLottieView=findViewById(R.id.voice_icon);
        mLottieView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( mLottieView.isAnimating() ){
                    mLottieView.setProgress(0);
                    mLottieView.cancelAnimation();
                    mPresenter.cancelASR();
                }else{
                    Log.d(TAG, "onClick: Voice icon");
                    mLottieView.playAnimation();
                    mPresenter.startASR();
                }
            }
        });

    }


    private void showhelper(){
        GuideMessage guideMsg = new GuideMessage(BaseMessage.RESULT_SUCCESS);
        mChatMessageBox.AddMessage(guideMsg);
    }

    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String[] permissions = {
                Manifest.permission.INTERNET,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                // 进入到这里代表没有权限.
            }
        }
        String[] tmpList = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }
    }

    private void animationControl(boolean start) {
        if (mLottieView != null) {
            if (start) {
                if (!mLottieView.isAnimating()) {
                    mLottieView.playAnimation();
                }
            } else {
                if (mLottieView.isAnimating()) {
                    mLottieView.setProgress(0);
                    mLottieView.cancelAnimation();
                }

            }
        } else {
            Log.w(TAG, "AnimationControl: mLottieView haven't initialization");
        }
    }

    private void showMessage(String resultStr) {
        Toast toast = Toast.makeText(MainActivity.this, resultStr, Toast.LENGTH_SHORT);
        toast.show();
    }

    //Override for LisaView interface
    @Override
    public void setPresenter(LisaPresenter presenter) {
        mPresenter = presenter;
        mPresenter.initializeModel(getApplicationContext());
    }

    @Override
    public void showReady(Message msg){
        Log.d(TAG, "showReady: ");
        showhelper();
    }

    @Override
    public void showSpeaking(Message msg){
        Log.d(TAG, "showSpeaking: ");
        animationControl(true);
        mChatMessageBox.UpdateMessage((BaseMessage) msg.obj, false);
    }

    @Override
    public void showRecognition(Message msg){
        Log.d(TAG, "showRecognition: ");
        try {
            animationControl(true);
            mChatMessageBox.UpdateMessage((BaseMessage) msg.obj, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showFinished(Message msg) {
        Log.d(TAG, "showFinished: ");
        mChatMessageBox.UpdateMessage((BaseMessage) msg.obj, true);
    }

    @Override
    public void showResult(Message msg) {
        Log.d(TAG, "showResult: ");
        if(msg.obj != null){
            BaseMessage resultMsg = (BaseMessage)msg.obj;
            mChatMessageBox.AddMessage(resultMsg);
        }
    }

    @Override
    public void showExit(Message msg) {
        Log.d(TAG, "showExit: ");
        animationControl(false);
    }

    @Override
    public void showTTSPlaying(TTSMessage msg) {
        Log.d(TAG, "showTTSPlaying: msg:" + msg);
        if(msg != null){
            animationControl(true);
            //mChatMessageBox.UpdateMessage(msg, msg.getIsIsFinished());
            mChatMessageBox.AddMessage(msg);
        }
    }

    private void engineerTest(String input){
        if(input.startsWith("1")){

        }else if(input.startsWith("2")){

        }else if(input.startsWith("3")){
        }else if(input.startsWith("4")){
        }else if(input.startsWith("5")){
        }else if(input.startsWith("6")){
        }else if(input.startsWith("7")){
            startActivity(new Intent(this, TTSTestActivity.class));
        }else{
        }
    }

    private void updateSupplierName(String asr, String tts){
        String str = String.format("Powered by %s TTS and %s ASR",tts, asr);
        mPowerDecl.setText(str);
    }


    class ResultContract extends ActivityResultContract<String, String> {
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, String input) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            intent.putExtra("input", input);
            return intent;
        }

        @Override
        public String parseResult(int resultCode, @Nullable Intent intent) {
            return intent.getStringExtra("Setting");
        }
    }


}