package com.example.arduinobluetooth;

import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arduinobluetooth.BluetoothSocket.BluetoothCommunicationThread;
import com.example.arduinobluetooth.utilities.MySingleton;

import java.io.IOException;

public class Operation extends AppCompatActivity implements BluetoothCommunicationThread.IMessageAvailable {

    Button RedButton, GreenButton, YellowButton;
    BluetoothSocket mBluetoothSocket;
    private String mFeedbackMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation);

        mBluetoothSocket = MySingleton.getInstance().retrieveBluetoothSocket();

        RedButton = (Button)findViewById(R.id.btnRed);
        GreenButton = (Button)findViewById(R.id.btnGreen);
        YellowButton = (Button)findViewById(R.id.btnYellow);

        setButtonFeatures(RedButton);
        setButtonFeatures(GreenButton);
        setButtonFeatures(YellowButton);

        //RED
        RedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(((Button)v).getText().equals(getString(R.string.red_on_text))){
                if(mBluetoothSocket != null){
                    startCommunicationThread("^11*");
                }
            } else {
                if(mBluetoothSocket != null){
                    startCommunicationThread("^10*");
                }
            }
            }
        });

        //GREEN
        GreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(((Button)v).getText().equals(getString(R.string.green_on_text))){
                if(mBluetoothSocket != null){
                    startCommunicationThread("^21*");
                }
            } else {
                if(mBluetoothSocket != null){
                    startCommunicationThread("^20*");
                }
            }
            }
        });

        //YELLOW
        YellowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(((Button)v).getText().equals(getString(R.string.yellow_on_text))){
                if(mBluetoothSocket != null){
                    startCommunicationThread("^31*");
                }
            } else {
                if(mBluetoothSocket != null){
                    startCommunicationThread("^30*");
                }
            }
            }
        });
    }

    private void setButtonFeatures(Button aButton){
        try{
            switch (aButton.getId()){
                case R.id.btnRed:
                    //aButton.getBackground().setColorFilter(Color.parseColor("#9A2617"), PorterDuff.Mode.SRC_OVER);
                    aButton.getBackground().setColorFilter(getResources().getColor(R.color.darkRedColor), PorterDuff.Mode.SRC_OVER);
                    aButton.setTextColor(Color.parseColor("#FFFFFF"));
                    break;
                case R.id.btnGreen:
                    //aButton.getBackground().setColorFilter(Color.parseColor("#006400"), PorterDuff.Mode.SRC_OVER);
                    aButton.getBackground().setColorFilter(getResources().getColor(R.color.darkGreenColor), PorterDuff.Mode.SRC_OVER);
                    aButton.setTextColor(Color.parseColor("#FFFFFF"));
                    break;
                case R.id.btnYellow:
                    //aButton.getBackground().setColorFilter(Color.parseColor("#999900"), PorterDuff.Mode.SRC_OVER);
                    aButton.getBackground().setColorFilter(getResources().getColor(R.color.darkYellowColor), PorterDuff.Mode.SRC_OVER);
                    aButton.setTextColor(Color.parseColor("#FFFFFF"));
                    break;
            }
        } catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void changeButtonColors(String returnString){
        switch (returnString){
            case "11":
                RedButton.getBackground().setColorFilter(getResources().getColor(R.color.lightRedColor), PorterDuff.Mode.SRC_OVER);
                RedButton.setTextColor(Color.BLACK);
                RedButton.setText(getString(R.string.red_off_text));
                break;
            case "10":
                RedButton.getBackground().setColorFilter(getResources().getColor(R.color.darkRedColor), PorterDuff.Mode.SRC_OVER);
                RedButton.setTextColor(Color.WHITE);
                RedButton.setText(getString(R.string.red_on_text));
                break;
            case "21":
                GreenButton.getBackground().setColorFilter(getResources().getColor(R.color.lightGreenColor), PorterDuff.Mode.SRC_OVER);
                GreenButton.setTextColor(Color.BLACK);
                GreenButton.setText(getString(R.string.green_off_text));
                break;
            case "20":
                GreenButton.getBackground().setColorFilter(getResources().getColor(R.color.darkGreenColor), PorterDuff.Mode.SRC_OVER);
                GreenButton.setTextColor(Color.WHITE);
                GreenButton.setText(getString(R.string.green_on_text));
                break;
            case "31":
                YellowButton.getBackground().setColorFilter(getResources().getColor(R.color.lightYellowColor), PorterDuff.Mode.SRC_OVER);
                YellowButton.setTextColor(Color.BLACK);
                YellowButton.setText(getString(R.string.yellow_off_text));
                break;
            case "30":
                YellowButton.getBackground().setColorFilter(getResources().getColor(R.color.darkYellowColor), PorterDuff.Mode.SRC_OVER);
                YellowButton.setTextColor(Color.WHITE);
                YellowButton.setText(getString(R.string.yellow_on_text));
                break;
            default:
        }
    }

    @Override
    public void onMessageAvailable(String strMessage) {
        mFeedbackMessage = strMessage;
        changeButtonColors(mFeedbackMessage);
    }

    public void startCommunicationThread(String stringToWrite){
        new BluetoothCommunicationThread(this, stringToWrite).execute(mBluetoothSocket);
    }
}
