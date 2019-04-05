package com.example.arduinobluetooth.utilities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.util.ArrayList;

public class MySingleton {

    public static final String TAG = MySingleton.class
            .getSimpleName();

    private BluetoothSocket mBluetoothSocket;
    public boolean mBluetoothOriginallyEnabled = false;
    private ArrayList<BluetoothDevice> mDevicesList = new ArrayList<>();

    private static MySingleton mInstance;

    public static synchronized MySingleton getInstance() {
        if(mInstance == null){
            mInstance = new MySingleton();
        }

        return mInstance;
    }

    private MySingleton(){}

    public void saveBluetoothSocket(BluetoothSocket bluetoothSocket){
        mBluetoothSocket = bluetoothSocket;
    }

    public BluetoothSocket retrieveBluetoothSocket(){
        return mBluetoothSocket;
    }

    public void saveDevicesList(ArrayList<BluetoothDevice> devicesList){
        mDevicesList = devicesList;
    }

    public ArrayList<BluetoothDevice> retrieveDevicesList(){
        return mDevicesList;
    }
}