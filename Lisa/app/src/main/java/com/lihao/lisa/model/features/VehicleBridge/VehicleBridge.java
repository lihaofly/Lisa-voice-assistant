package com.lihao.lisa.model.features.VehicleBridge;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

public class VehicleBridge {
    private static final String TAG = "Lisa-VehicleBrigde";
    private BluetoothChatService mChatService = null;
    private final String address = "B8:9F:09:F4:01:B2";
    private BluetoothAdapter mBluetoothAdapter;

    private static VehicleBridge mInstance = null;

    private VehicleBridge() {
        mChatService = new BluetoothChatService();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static VehicleBridge getInstance(){
        if( mInstance == null ){
            synchronized (VehicleBridge.class){
                if( mInstance == null ){
                    mInstance = new VehicleBridge();
                }
            }
        }
        return mInstance;
    }

    public void Connect(String device){
        Log.d(TAG, "Connect: start");
        BluetoothDevice device1 = mBluetoothAdapter.getRemoteDevice(device);
        mChatService.connect(device1, false);
    }

    public void SendMessage(String msg){
        Log.d(TAG, "SendMessage: msg: " + msg);
        mChatService.write(msg.getBytes());
    }
}
