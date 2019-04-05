package com.example.arduinobluetooth.BluetoothSocket;

import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothCommunicationThread extends AsyncTask<BluetoothSocket, Void, String> {

    private BluetoothSocket mBluetoothSocket;
    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private IMessageAvailable mMessageAvailable;
    private String mValueToWrite;

    public BluetoothCommunicationThread(IMessageAvailable messageAvailable, String valueToWrite){
        mMessageAvailable = messageAvailable;
        mValueToWrite = valueToWrite;
    }

    @Override
    protected String doInBackground(BluetoothSocket... bluetoothSockets) {
        mBluetoothSocket = bluetoothSockets[0];
        InputStream tempInputStream = null;
        OutputStream tempOutputStream = null;
        try {
            tempInputStream = mBluetoothSocket.getInputStream();
            tempOutputStream = mBluetoothSocket.getOutputStream();
        } catch (IOException ex){

        }

        mInputStream = tempInputStream;
        mOutputStream = tempOutputStream;

        byte[] buffer = new byte[256];      //could make it 1024
        int bytesRead;
        String dMessage;

        //Write to BluetoothSocket OutputStream

        try {

            writeToBluetoothSocketOutputStream(mValueToWrite);
            dMessage = "";

            while(true){    //Continually listen for messages on the BluetoothSocket InputStream
                try {

                    bytesRead = mInputStream.read(buffer);

                    String strTemp = new String(buffer, 0, bytesRead);
                    dMessage += strTemp;
                    if(dMessage.contains("#")  && dMessage.contains("~")){
                        int endIndex = dMessage.indexOf("~");
                        dMessage = dMessage.trim().substring(endIndex - 2, endIndex);
                        break;
                    }
                } catch (IOException ex){
                    Log.e("BENNY CATCH", ex.getMessage());
                    break;
                }
            }
        } catch (Exception ex){
            dMessage = ex.getMessage();
        }

        return dMessage;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mMessageAvailable.onMessageAvailable(s);
    }

    public interface IMessageAvailable {
        void onMessageAvailable(String strMessage);
    }

    private void writeToBluetoothSocketOutputStream(String stringToWrite){
        byte[] outputBuffer = stringToWrite.getBytes();
        try {
            mOutputStream.write(outputBuffer);
        } catch (IOException ex){

        }
    }
}
