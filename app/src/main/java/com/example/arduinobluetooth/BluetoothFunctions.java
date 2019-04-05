package com.example.arduinobluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.arduinobluetooth.Adapter.RecyclerAdapter;
import com.example.arduinobluetooth.BluetoothSocket.BluetoothSocketThread;
import com.example.arduinobluetooth.utilities.MyDividerItemDecoration;
import com.example.arduinobluetooth.utilities.MySingleton;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class BluetoothFunctions extends AppCompatActivity
        implements RecyclerAdapter.IClickHandler, BluetoothSocketThread.IConnectionTaskCompleted {

    private static final int LOCATION_REQUEST_CODE = 2;
    private static final int MY_BLUETOOTH_ON_REQUEST_CODE = 1;

    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerAdapter mRecyclerAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private final String TAG = BluetoothFunctions.class.getSimpleName();
    private String mConnectionResult;
    private BluetoothSocket mSocket;

    BluetoothAdapter mBTAdapter = BluetoothAdapter.getDefaultAdapter();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_devices);

        mDeviceList = MySingleton.getInstance().retrieveDevicesList();

        mRecyclerView = (RecyclerView)findViewById(R.id.rv_Recycler);
        mRecyclerAdapter = new RecyclerAdapter(this, mDeviceList, this);

        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new MyDividerItemDecoration(this, MyDividerItemDecoration.VERTICAL_LIST, 36));
        mRecyclerView.setAdapter(mRecyclerAdapter);

        if(savedInstanceState == null){
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                checkSpecialBluetoothPermissions(); //Before starting Bluetooth
            } else {
                promptBluetoothTurnOn();            //Else just start Bluetooth
            }
        }
    }

    @Override
    public void onRecyclerItemClicked(int aPosition) {
        BluetoothDevice aDevice = mDeviceList.get(aPosition);
        Toast.makeText(this, "Processing for..." + aDevice.getName(), Toast.LENGTH_LONG).show();

        if(!devicePairedAlready(aDevice)){
            subscribeToBluetoothDevicePairingResultNotice();
            promptRemoteDevicePairing(mDeviceList.get(aPosition));  //Handle pair results in Broadcast Receiver
        } else {
            new BluetoothSocketThread(this).execute(aDevice);   //Connect if paired already
        }
    }

    @Override
    public void onConnectionTaskCompleted(BluetoothSocket socket) {
        mSocket = socket;
        if(mSocket != null){
            unregisterReceiver(myBroadcastReceiver);
            Toast.makeText(this, "Connected...", Toast.LENGTH_LONG).show();
            MySingleton.getInstance().saveBluetoothSocket(mSocket);
            Intent startActivityIntent = new Intent(BluetoothFunctions.this, Operation.class);
            startActivity(startActivityIntent);
        }
    }

    private void whiteNotificationBar(View view){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    private void promptBluetoothTurnOn(){
        if(!mBTAdapter.isEnabled()){ //If not turned on already
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, MY_BLUETOOTH_ON_REQUEST_CODE);
        } else { //Turned on already
            scanForBluetoothDevices();
        }
    }

    private void promptRemoteDevicePairing(BluetoothDevice aRemoteDevice){
        try{
            Method method = aRemoteDevice.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(aRemoteDevice, (Object[]) null);
        } catch (Exception e){
            Toast.makeText(this, "Error..." + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void promptRemoteDeviceUnpair(BluetoothDevice aRemoteDevice){
        try{
            Method method = aRemoteDevice.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(aRemoteDevice, (Object[]) null);
        } catch (Exception e){
            Toast.makeText(this, "Error..." + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    final BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice remoteBluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            //When a device is found
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                //Get Bluetooth device from the Intent
                //Add Device to List

                mDeviceList.add(remoteBluetoothDevice);
                MySingleton.getInstance().saveDevicesList(mDeviceList);
                mRecyclerAdapter.swapMyData(mDeviceList);
            }

            if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
                final int currentState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int previousState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if(currentState == BluetoothDevice.BOND_BONDED && previousState == BluetoothDevice.BOND_BONDING){
                    //Successful Pairing
                    Toast.makeText(BluetoothFunctions.this, "Paired Successfully!", Toast.LENGTH_LONG).show();

                    //Connect if pairing successful
                    AsyncTask connectionTask = new BluetoothSocketThread(BluetoothFunctions.this).execute(remoteBluetoothDevice);

                } else if(currentState == BluetoothDevice.BOND_NONE && previousState == BluetoothDevice.BOND_BONDED){
                    //Successful Unpairing
                    Toast.makeText(BluetoothFunctions.this, "Now Unpaired!", Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    private void scanForBluetoothDevices(){
        if(mBTAdapter.isDiscovering()){
            mBTAdapter.cancelDiscovery();
        }

        mDeviceList.clear();

        try {
            //To receive notice when OS broadcasts a "FOUND" message
            subscribeToBluetoothDeviceFoundNotice();
            mBTAdapter.startDiscovery();

            Toast.makeText(this, "Discovering now...", Toast.LENGTH_LONG).show();
        } catch (Exception ex){
            Toast.makeText(this, "Error.." + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void checkSpecialBluetoothPermissions(){

        boolean fineLocationPermitted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
        boolean coarseLocationPermitted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
        boolean hasSpecialPermissionsAlready = fineLocationPermitted && coarseLocationPermitted;

        if(!hasSpecialPermissionsAlready){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            promptBluetoothTurnOn();
        }
    }

    private void subscribeToBluetoothDeviceFoundNotice(){
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(myBroadcastReceiver, intentFilter);
    }

    private void subscribeToBluetoothDevicePairingResultNotice(){
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(myBroadcastReceiver, intentFilter);
    }

    private boolean devicePairedAlready(BluetoothDevice aRemoteDevice){
        boolean pairedAlready = false;
        Set<BluetoothDevice> pairedDevicesList = mBTAdapter.getBondedDevices();

        if(pairedDevicesList.size() > 0){
            for(BluetoothDevice aDevice: pairedDevicesList){
                if(aDevice.getAddress().equals(aRemoteDevice.getAddress())){    //check if address is the same
                    pairedAlready = true;
                    break;
                }
            }
        }

        return pairedAlready;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == LOCATION_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                promptBluetoothTurnOn();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == MY_BLUETOOTH_ON_REQUEST_CODE){
            if(mBTAdapter.isEnabled()){
                Toast.makeText(this, "Yeah! Bluetooth turned on!", Toast.LENGTH_LONG).show();
                scanForBluetoothDevices();
            }
        }
    }

    private void restoreBluetoothState(){
        if(MySingleton.getInstance().mBluetoothOriginallyEnabled){
            if(!mBTAdapter.isEnabled()) mBTAdapter.enable();
        } else {
            if(mBTAdapter.isEnabled()) mBTAdapter.disable();
        }
    }

    /*
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        MySingleton.getInstance().saveDevicesList(mDeviceList);
    }
    */

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        restoreBluetoothState();
    }
}
