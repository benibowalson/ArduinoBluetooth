package com.example.arduinobluetooth.BluetoothSocket;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.UUID;

public class BluetoothSocketThread extends AsyncTask<BluetoothDevice, Void, BluetoothSocket> {

    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket mBluetoothSocket = null;
    private IConnectionTaskCompleted taskCompleted;

    public BluetoothSocketThread(IConnectionTaskCompleted taskCompleted){
        this.taskCompleted = taskCompleted;
    }

    @Override
    protected BluetoothSocket doInBackground(BluetoothDevice... bluetoothDevices) {
        try {

            if(mBluetoothSocket == null){
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                BluetoothDevice aRemoteDevice = bluetoothDevices[0];
                mBluetoothSocket = aRemoteDevice.createInsecureRfcommSocketToServiceRecord(myUUID);

                bluetoothAdapter.cancelDiscovery();
                mBluetoothSocket.connect();      //start connection
            }

        } catch (IOException ex){
            mBluetoothSocket = null;
        } catch (Exception ex){
            mBluetoothSocket = null;
        }

        return mBluetoothSocket;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(BluetoothSocket socket) {
        super.onPostExecute(socket);
        taskCompleted.onConnectionTaskCompleted(socket);
    }

    public interface IConnectionTaskCompleted {
        void onConnectionTaskCompleted(BluetoothSocket socket);
    }
}
