package com.lihao.lisa.model.features.InformationSearch;


import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.lihao.lisa.util.BaseMessage;
import com.lihao.lisa.util.EuropeCupMessage;
import com.lihao.lisa.util.ExecuteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.lihao.lisa.util.BaseMessage.EUROPECUP_MESSAGE;
import static com.lihao.lisa.util.BaseMessage.EXCUTE_MESSAGE;

/**
 * 笑话接口示例代码
 */
public class InformationSearch {

    //随机笑话
    public static final String RANDOM_JOKE = "http://v.juhe.cn/joke/randJoke.php?key=%s";

    public static final String EUROPE_CUP = "http://apis.juhe.cn/fapig/euro2020/schedule?key=%s";

    public static final int SEARCH_TYPE_NONE = 0;
    public static final int SEARCH_TYPE_JOKE = 1;
    public static final int SEARCH_TYPE_EUROPE_CUP = 2;

    public int mLastSearchType = SEARCH_TYPE_NONE;

    //申请接口的请求key
    // TODO: 您需要改为自己的请求key
    public static final String KEY = "58b3068ad90ae214ae4522331228c6c7";

    public static final String KEY_EUROPCUP = "c8df6d99ac353cec302f6f1da8943b4b";
    private static final String TAG = "InformationSearch";

    private static InformationSearch mInstance = null;
    private Handler mHandler;

    public InformationSearch(Handler mHandler) {
        this.mHandler = mHandler;
    }

    public static InformationSearch getInstance(Handler mHandler){
        if(mInstance == null){
            synchronized(InformationSearch.class){
                if(mInstance == null){
                    mInstance = new InformationSearch(mHandler);
                }
            }
        }
        return mInstance;
    }


    private void syncNetAccess(String urlStr) throws IOException {

        //创建OkHttpClient实例，主要用于请求网络
        OkHttpClient okHttpClient = new OkHttpClient();

        //创建Request实例，可以配置接口地址和请求头
        Request okRequest = new Request.Builder().url(urlStr).build();

        //GET请求，用Response接受相应结果
        Response response = okHttpClient.newCall(okRequest).execute();

        //response.body()为请求返回数据 JSON格式
        Log.d("+++++++++++++++++++++",response.body().string());
    }

    //异步GET方法
    private void asyncNetAccess(String urlStr) throws IOException {

        //创建OkHttpClient实例，主要用于请求网络
        OkHttpClient okHttpClient = new OkHttpClient();

        //创建Request实例，这里配置了请求头 addHeader()
        Request okRequest = new Request.Builder().url(urlStr).addHeader("name","value").build();

        //GET请求，回调函数中获取相应结果
        okHttpClient.newCall(okRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: Response: " + e.getMessage());
                mLastSearchType = SEARCH_TYPE_NONE;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseStr = response.body().string();
                Log.d(TAG, "onResponse: Response: " + responseStr);



                try {
                    if (mLastSearchType == SEARCH_TYPE_JOKE) {
                        JSONObject jsonObject = null;
                        jsonObject = new JSONObject(responseStr);
                        String reason = jsonObject.getString("reason");
                        JSONArray results = jsonObject.getJSONArray("result");
                        Log.d(TAG, "onResponse: reason: " + reason + " results.length():" + results.length());
                        if (reason.equals("success") && results.length() > 0) {
                            String JokeStr = results.getJSONObject(0).getString("content");
                            Message message = Message.obtain();
                            message.what = EXCUTE_MESSAGE;
                            message.obj = new ExecuteMessage(BaseMessage.RESULT_FAILED, JokeStr);
                            mHandler.sendMessage(message);
                        }

                        mLastSearchType = SEARCH_TYPE_NONE;
                    } else if (mLastSearchType == SEARCH_TYPE_EUROPE_CUP) {
                        Gson gson = new Gson();
                        EuropeCupBean europeCupBean = gson.fromJson(responseStr, EuropeCupBean.class);
                        Log.d(TAG, "onResponse2: europeCupBean: " + europeCupBean.toString());

                        Message message = Message.obtain();
                        message.what = EUROPECUP_MESSAGE;
                        message.obj = new EuropeCupMessage(BaseMessage.RESULT_SUCCESS, "",europeCupBean);
                        mHandler.sendMessage(message);
                    }else {
                        mLastSearchType = SEARCH_TYPE_NONE;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    /**
     * 随机笑话
     *
     */
    public void SearchJoke() throws IOException {
        //发送http请求的url
        mLastSearchType = SEARCH_TYPE_JOKE;
        String url = String.format(RANDOM_JOKE, KEY);
        Log.d(TAG, "printC: url: " + url);
        asyncNetAccess(url);
    }

    public void SearchEuropeCup()throws IOException {
        Log.d(TAG, "SearchEuropeCup: ");
        String url = String.format(EUROPE_CUP, KEY_EUROPCUP);
        mLastSearchType = SEARCH_TYPE_EUROPE_CUP;
        Log.d(TAG, "printC: url: " + url);
        asyncNetAccess(url);
    }


    /**
     * 最新笑话
     *
     * @param pageSize int 每页数量
     */
//    public static void printB( int pageSize) {
//        //发送http请求的url
//        String url = String.format(URL_B, KEY, pageSize);
//        final String response = doGet(url);
//        System.out.println("接口返回：" + response);
//        try {
//            JSONObject jsonObject = JSONObject.fromObject(response);
//            int error_code = jsonObject.getInt("error_code");
//            if (error_code == 0) {
//                System.out.println("调用接口成功");
//                JSONArray result = jsonObject.getJSONObject("result").getJSONArray("data");
//                result.stream().map(JSONObject::fromObject).forEach(hour -> {
//                    System.out.println("content：" + ((JSONObject) hour).getString("content"));
//                    System.out.println("hashId：" + ((JSONObject) hour).getString("hashId"));
//                    System.out.println("unixtime：" + ((JSONObject) hour).getString("unixtime"));
//                    System.out.println("updatetime：" + ((JSONObject) hour).getString("updatetime"));
//                });
//
//            } else {
//                System.out.println("调用接口失败：" + jsonObject.getString("reason"));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 按更新时间查询笑话
//     *
//     * @param time     long 时间戳
//     * @param pageSize int 每页数量
//     */
//    public static void printA(long time, int pageSize) {
//        //发送http请求的url
//        String url = String.format(URL_A, KEY, time, pageSize);
//
//        final String response = doGet(url);
//        System.out.println("接口返回：" + response);
//        try {
//            JSONObject jsonObject = JSONObject.fromObject(response);
//            int error_code = jsonObject.getInt("error_code");
//            if (error_code == 0) {
//                System.out.println("调用接口成功");
//                JSONArray result = jsonObject.getJSONObject("result").getJSONArray("data");
//                result.stream().map(JSONObject::fromObject).forEach(hour -> {
//                    System.out.println("content：" + ((JSONObject) hour).getString("content"));
//                    System.out.println("hashId：" + ((JSONObject) hour).getString("hashId"));
//                    System.out.println("unixtime：" + ((JSONObject) hour).getString("unixtime"));
//                    System.out.println("updatetime：" + ((JSONObject) hour).getString("updatetime"));
//                });
//
//            } else {
//                System.out.println("调用接口失败：" + jsonObject.getString("reason"));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    /**
     * get方式的http请求
     *
     * @param httpUrl 请求地址
     * @return 返回结果
     */
    public static String doGet(String httpUrl) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        String result = null;// 返回结果字符串
        try {
            // 创建远程url连接对象
            URL url = new URL(httpUrl);
            // 通过远程url连接对象打开一个连接，强转成httpURLConnection类
            connection = (HttpURLConnection) url.openConnection();
            // 设置连接方式：get
            connection.setRequestMethod("GET");
            // 设置连接主机服务器的超时时间：15000毫秒
            connection.setConnectTimeout(15000);
            // 设置读取远程返回的数据时间：60000毫秒
            connection.setReadTimeout(60000);
            // 发送请求
            connection.connect();
            // 通过connection连接，获取输入流
            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();
                // 封装输入流，并指定字符集
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                // 存放数据
                StringBuilder sbf = new StringBuilder();
                String temp;
                while ((temp = bufferedReader.readLine()) != null) {
                    sbf.append(temp);
                    sbf.append(System.getProperty("line.separator"));
                }
                result = sbf.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != bufferedReader) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();// 关闭远程连接
            }
        }
        return result;
    }


    /**
     * post方式的http请求
     *
     * @param httpUrl 请求地址
     * @param param   请求参数
     * @return 返回结果
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String doPost(String httpUrl, String param) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        BufferedReader bufferedReader = null;
        String result = null;
        try {
            URL url = new URL(httpUrl);
            // 通过远程url连接对象打开连接
            connection = (HttpURLConnection) url.openConnection();
            // 设置连接请求方式
            connection.setRequestMethod("POST");
            // 设置连接主机服务器超时时间：15000毫秒
            connection.setConnectTimeout(15000);
            // 设置读取主机服务器返回数据超时时间：60000毫秒
            connection.setReadTimeout(60000);
            // 默认值为：false，当向远程服务器传送数据/写数据时，需要设置为true
            connection.setDoOutput(true);
            // 设置传入参数的格式:请求参数应该是 name1=value1&name2=value2 的形式。
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            // 通过连接对象获取一个输出流
            outputStream = connection.getOutputStream();
            // 通过输出流对象将参数写出去/传输出去,它是通过字节数组写出的
            outputStream.write(param.getBytes());
            // 通过连接对象获取一个输入流，向远程读取
            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();
                // 对输入流对象进行包装:charset根据工作项目组的要求来设置
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder sbf = new StringBuilder();
                String temp;
                // 循环遍历一行一行读取数据
                while ((temp = bufferedReader.readLine()) != null) {
                    sbf.append(temp);
                    sbf.append(System.getProperty("line.separator"));
                }
                result = sbf.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != bufferedReader) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != outputStream) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }
}

