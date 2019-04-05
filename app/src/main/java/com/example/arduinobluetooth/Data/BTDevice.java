package com.example.arduinobluetooth.Data;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

public class BTDevice implements Parcelable {
    private String deviceName;
    private String deviceMAC;

    public BTDevice(String deviceName, String deviceMAC){
        this.deviceName = deviceName;
        this.deviceMAC = deviceMAC;
    }

    private BTDevice(Parcel in) {
        deviceName = in.readString();
        deviceMAC = in.readString();
    }

    public static final Creator<BTDevice> CREATOR = new Creator<BTDevice>() {
        @Override
        public BTDevice createFromParcel(Parcel in) {
            return new BTDevice(in);
        }

        @Override
        public BTDevice[] newArray(int size) {
            return new BTDevice[size];
        }
    };

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceMAC() {
        return deviceMAC;
    }

    public void setDeviceMAC(String deviceMAC) {
        this.deviceMAC = deviceMAC;
    }

    @Override
    public String toString() {
        return this.deviceName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(deviceName);
        dest.writeString(deviceMAC);
    }
}
