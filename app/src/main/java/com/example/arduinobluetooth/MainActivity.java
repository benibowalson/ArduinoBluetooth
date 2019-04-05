package com.example.arduinobluetooth;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.arduinobluetooth.Data.BTDevice;
import com.example.arduinobluetooth.utilities.MySingleton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button bluetoothButton;
    ArrayList<BTDevice> mDeviceList = new ArrayList<>();
    BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothButton = (Button)findViewById(R.id.btnBluetooth);
        setButtonFeatures();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //Store original Bluetooth state
        MySingleton.getInstance().mBluetoothOriginallyEnabled = mBluetoothAdapter.isEnabled();

        if(mBluetoothAdapter == null){
            bluetoothButton.setEnabled(false);
            Toast.makeText(this, "No Bluetooth Support!", Toast.LENGTH_LONG).show();
        } else {
            bluetoothButton.setEnabled(true);
            bluetoothButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent activityStartIntent = new Intent(MainActivity.this, BluetoothFunctions.class);
                    startActivity(activityStartIntent);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.my_menu_file, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ((item.getItemId())){
            case R.id.mnuAbout:
                //Show about dialogue
                showDialogue();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setButtonFeatures(){
        //bluetoothButton.getBackground().setColorFilter(Color.parseColor("#0D3D56"), PorterDuff.Mode.SRC_OVER);
        bluetoothButton.getBackground().setColorFilter(getResources().getColor(R.color.darkBlue), PorterDuff.Mode.SRC_OVER);
        bluetoothButton.setTextColor(Color.parseColor("#FFFFFF"));
    }

    private void showDialogue(){
        final Dialog myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.my_about_file);
        myDialog.setTitle("About ArduinoBluetooth");
        myDialog.show();
    }
}
