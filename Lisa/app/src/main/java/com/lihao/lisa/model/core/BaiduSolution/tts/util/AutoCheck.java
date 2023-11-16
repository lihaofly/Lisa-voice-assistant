package com.lihao.lisa.model.core.BaiduSolution.tts.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import androidx.core.content.ContextCompat;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;
import org.json.JSONObject;

import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SynthesizerTool;
import com.baidu.tts.client.TtsMode;

/**
 * @ClassName AutoCheck
 * @Author lihao.fly@163.com
 * @Data 2023/10/23
 * @Description  Class used to check the initial parameters
 */
public class AutoCheck {
    private static AutoCheck instance;
    private LinkedHashMap<String, Check> checks;
    private static Context context;
    private boolean hasError = false;
    volatile boolean isFinished = false;

    public static AutoCheck getInstance(Context context) {
        if (instance == null || AutoCheck.context != context) {
            instance = new AutoCheck(context);
        }
        return instance;
    }

    public void check(final InitConfig initConfig, final Handler handler) {
        Thread tempThread = new Thread(new Runnable() {
            @Override
            public void run() {
                AutoCheck obj = innerCheck(initConfig);
                isFinished = true;
                synchronized (obj) {
                    Message msg = handler.obtainMessage(100, obj);
                    handler.sendMessage(msg);
                }
            }
        });
        tempThread.start();
    }

    private AutoCheck innerCheck(InitConfig config) {
        boolean isOnlineSdk = TtsMode.ONLINE.equals(config.getTtsMode());
        checks.put("Check_Permissions", new CheckPermissions(context));
        checks.put("Check_So_library", new CheckSoLib(context, isOnlineSdk));
        checks.put("Check_AppId_AppKey_SecretKey",new CheckAppInfo(config.getAppId(), config.getAppKey(), config.getSecretKey()));
        checks.put("Check_PackageName", new ApplicationIdCheck(context, config.getAppId()));

        if (!isOnlineSdk) {
            checks.put("Check_Test_Resource", new CheckOfflineResourceFile(config.getTextModePath()));
            checks.put("Check_Voice_Resource", new CheckOfflineResourceFile(config.getVoiceModePath()));
        }

        for (Map.Entry<String, Check> e : checks.entrySet()) {
            Check check = e.getValue();
            check.check();
            if (check.hasError()) {
                break;
            }
        }
        return this;
    }

    public String obtainErrorMessage() {
        PrintConfig config = new PrintConfig();
        return formatString(config);
    }

    public String obtainDebugMessage() {
        PrintConfig config = new PrintConfig();
        return formatString(config);
    }

    public String obtainAllMessage() {
        PrintConfig config = new PrintConfig();
        return formatString(config);
    }

    public String formatString(PrintConfig config) {
        StringBuilder sb = new StringBuilder();
        hasError = false;

        for (HashMap.Entry<String, Check> entry : checks.entrySet()) {
            Check check = entry.getValue();
            String testName = entry.getKey();

            if (config.withInfo && check.hasError()) {
                if (!hasError) {
                    hasError = true;
                }

                sb.append("[Error][").append(testName).append("] ").append(check.getErrorMessage()).append("\n");
                if (check.hasFix()) {
                    sb.append("[Fix method][").append(testName).append("] ").append(check.getFixMessage()).append("\n");
                }
            }

            if (config.withInfo && check.hasInfo()) {
                sb.append("[Info][").append(testName).append("] ").append(check.getInfoMessage()).append("\n");
            }

            if (config.withLog && (config.withLogOnSuccess || hasError) && check.hasLog()) {
                sb.append("[log]:" + check.getLogMessage()).append("\n");
            }
        }

        if (!hasError) {
            sb.append("[AutoCheckResul] Congratulations!!! There is no error found!!!\n");
        }else{
            sb.append("[AutoCheckResul] Error found, Please refer the above log\n");
        }

        return sb.toString();
    }

    public void clear() {
        checks.clear();
        hasError = false;
    }

    private AutoCheck(Context context) {
        this.context = context;
        checks = new LinkedHashMap<>();
    }

    private static class PrintConfig {
        public boolean withFix = true;
        public boolean withInfo = true;
        public boolean withLog = true;
        public boolean withLogOnSuccess = true;

        public PrintConfig() {
            this.withFix = true;
            this.withInfo = true;
            this.withLog = true;
            this.withLogOnSuccess = true;
        }

        public PrintConfig(boolean withFix, boolean withInfo, boolean withLog, boolean withLogOnSuccess) {
            this.withFix = withFix;
            this.withInfo = withInfo;
            this.withLog = withLog;
            this.withLogOnSuccess = withLogOnSuccess;
        }
    }


    /**
     * Check permissions requested
     */
    private static class CheckPermissions extends Check {
        private Context context;

        public CheckPermissions(Context context) {
            this.context = context;
        }


        @Override
        public void check() {
            String[] permissions = {
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.BROADCAST_STICKY,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.MODIFY_AUDIO_SETTINGS,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_WIFI_STATE,
            };

            ArrayList<String> toApplyList = new ArrayList<String>();

            for (String perm : permissions) {
                if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(context, perm)) {
                    toApplyList.add(perm);
                }
            }
            if (!toApplyList.isEmpty()) {
                errorMessage = "No permissions: " + toApplyList;
                fixMessage = "Please add the permission in app AndroidManifest.xml";
            }else{
                infoMessage = "Check Permission Pass!!";
            }
        }
    }


    /**
     * Class to check if so lib already exist in path
     */
    private static class CheckSoLib extends Check {
        private Context context;
        private String[] soNames;

        public CheckSoLib(Context context, boolean isOnlineSdk) {
            this.context = context;
            if (isOnlineSdk) {
                soNames = new String[]{"libBDSpeechDecoder_V1.so"};
            } else {
                soNames = new String[]{"libbd_etts.so", "libBDSpeechDecoder_V1.so", "libgnustl_shared.so"};
            }
        }

        @Override
        public void check() {
            String path = context.getApplicationInfo().nativeLibraryDir;
            appendLogMessage("So lib file path: " + path);
            File[] files = new File(path).listFiles();
            TreeSet<String> set = new TreeSet<>();
            if (files != null) {
                for (File file : files) {
                    if (file.canRead()) {
                        set.add(file.getName());
                    }
                }
            }
            appendLogMessage("File list in JNI path: " + set.toString());
            for (String name : soNames) {
                if (!set.contains(name)) {
                    errorMessage = "Jni path: " + path + " Lack so file: " + name + ", File list in current path: " + set.toString();
                    fixMessage = "Please add the so file in project and set the dependency correct.";
                    break;
                }
            }

            if(!hasError()){
                infoMessage = "Check So Lib Pass!!";
            }
        }
    }

    private static class CheckOfflineResourceFile extends Check {
        private String filePath;

        public CheckOfflineResourceFile(String path) {
            filePath = path;
        }

        @Override
        public void check() {
            if(null != filePath){
                File file = new File(filePath);
                if (!file.exists()) {
                    errorMessage = "No resource file: " + filePath;
                } else if (!file.canRead()) {
                    errorMessage = "No read permission for resource file: " + filePath;
                } else if (!SynthesizerTool.verifyModelFile(filePath)) {
                    errorMessage = "Resource file invalid: " + filePath;
                }
                if (hasError()) {
                    fixMessage = "Please add text mode or voice mode resource file: " + filePath;
                }
            }else{
                errorMessage = "Resource path is null";
                fixMessage = "Please set the resourece path";
            }

            if(!hasError()){
                infoMessage = "Check Offline TTS Resource Pass!!";
            }
        }
    }

    private static class ApplicationIdCheck extends Check {
        private String appId;
        private Context context;

        public ApplicationIdCheck(Context context, String appId) {
            this.appId = appId;
            this.context = context;
        }

        @Override
        public void check() {
            logMessage.append("AppId：" + appId +
                    " Package Name: " + getApplicationId());
        }

        private String getApplicationId() {
            return context.getPackageName();
        }
    }


    private static class CheckAppInfo extends Check {
        private String appId;
        private String appKey;
        private String secretKey;

        public CheckAppInfo(String appId, String appKey, String secretKey) {
            this.appId = appId;
            this.appKey = appKey;
            this.secretKey = secretKey;
        }

        public void check() {
            do {
                appendLogMessage("Check appId: " + appId + " ,appKey: " + appKey + " ,secretKey: " + secretKey);
                if (appId == null || appId.isEmpty()) {
                    errorMessage = "appId is null";
                    fixMessage = "Please add appID";
                    break;
                }
                if (appKey == null || appKey.isEmpty()) {
                    errorMessage = "appKey is null";
                    fixMessage = "Please add appKey";
                    break;
                }
                if (secretKey == null || secretKey.isEmpty()) {
                    errorMessage = "secretKey is null";
                    fixMessage = "Please add secretKey";
                    break;
                }
            } while (false);

            try {
                checkOnline();
            } catch (UnknownHostException e) {
                infoMessage = "No network ignore online check: " + e.getMessage();
            } catch (Exception e) {
                errorMessage = e.getClass().getCanonicalName() + ":" + e.getMessage();
                fixMessage = "Exception happen. Please try again.";
            }

            if(!hasError()){
                infoMessage = "Check AppID&AppKey&SecretKey Pass!!";
            }
        }

        private void checkOnline() throws Exception {
            String urlpath = "https://openapi.baidu.com/oauth/2.0/token?grant_type=client_credentials&client_id="
                    + appKey + "&client_secret=" + secretKey;
            URL url = new URL(urlpath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(1000);
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder result = new StringBuilder();
            String line = "";
            do {
                line = reader.readLine();
                if (line != null) {
                    result.append(line);
                }
            } while (line != null);
            String res = result.toString();
            appendLogMessage("openapi return " + res);
            JSONObject jsonObject = new JSONObject(res);
            String error = jsonObject.optString("error");
            if (error != null && !error.isEmpty()) {
                throw new Exception("appkey secretKey error" + ", error:" + error + ", json is" + result);
            }
            String token = jsonObject.getString("access_token");
            if (token == null || !token.endsWith("-" + appId)) {
                throw new Exception("appId and appkey and appSecret not consistent. appId = " + appId + " ,token = " + token);
            }
        }
    }

    private abstract static class Check {
        protected String errorMessage = null;
        protected String fixMessage = null;
        protected String infoMessage = null;
        protected StringBuilder logMessage;

        public Check() {
            logMessage = new StringBuilder();
        }
        public abstract void check();

        public boolean hasError() {
            return errorMessage != null;
        }
        public boolean hasFix() {
            return fixMessage != null;
        }
        public boolean hasInfo() {
            return infoMessage != null;
        }
        public boolean hasLog() {
            return !logMessage.toString().isEmpty();
        }
        public void appendLogMessage(String message) {
            logMessage.append(message + "\n");
        }
        public String getErrorMessage() {
            return errorMessage;
        }
        public String getFixMessage() {
            return fixMessage;
        }
        public String getInfoMessage() {
            return infoMessage;
        }
        public String getLogMessage() {
            return logMessage.toString();
        }
    }
}
