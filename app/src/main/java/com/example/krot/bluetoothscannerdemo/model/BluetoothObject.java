package com.example.krot.bluetoothscannerdemo.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Krot on 1/8/18.
 */

public class BluetoothObject {

    @Nullable
    private String deviceName;

    @NonNull
    private String deviceMacAddress;

    public BluetoothObject(String deviceName, @NonNull String deviceMacAddress) {
        this.deviceName = deviceName;
        this.deviceMacAddress = deviceMacAddress;
    }

    @Nullable
    public String getDeviceName() {
        return deviceName;
    }

    @NonNull
    public String getDeviceMacAddress() {
        return deviceMacAddress;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BluetoothObject) {
            BluetoothObject currentBluetoothObj = (BluetoothObject) obj;
            return this.getDeviceMacAddress().equals(currentBluetoothObj.getDeviceMacAddress());
        } else {
            return false;
        }
    }
}
