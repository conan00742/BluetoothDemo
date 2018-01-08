package com.example.krot.bluetoothscannerdemo.adapter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.krot.bluetoothscannerdemo.R;
import com.example.krot.bluetoothscannerdemo.callback.BluetoothListDiffCallback;
import com.example.krot.bluetoothscannerdemo.model.BluetoothObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Krot on 1/7/18.
 */

public class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.BluetoothViewHolder> {

    @Nullable
    private List<BluetoothObject> deviceList;

    private LayoutInflater inflater;

    public BluetoothDeviceAdapter() {

    }

    @Override
    public BluetoothViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item, parent, false);
        return new BluetoothViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BluetoothViewHolder holder, int position) {
        BluetoothObject currentDevice = getItemAt(position);
        if (currentDevice == null) {
            Log.i("WTF", "currentDevice: object = " + currentDevice);
        } else {
            holder.bindData(currentDevice);
        }
    }

    @Nullable
    public List<BluetoothObject> getDeviceList() {
        return deviceList;
    }

    @MainThread
    public void updateBluetoothDeviceList (List<BluetoothObject> newList) {
        final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new BluetoothListDiffCallback(deviceList, newList), false);
        deviceList = newList;
        result.dispatchUpdatesTo(BluetoothDeviceAdapter.this);
    }

    public void setDeviceList(@Nullable List<BluetoothObject> deviceList) {
        this.deviceList = deviceList;
        notifyDataSetChanged();

    }


    @Override
    public int getItemCount() {
        return (deviceList == null ? 0 : deviceList.size());
    }

    public BluetoothObject getItemAt(int position) {
        return (deviceList == null ? null : deviceList.get(position));
    }

    class BluetoothViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.device_name)
        TextView deviceName;
        @BindView(R.id.device_mac_address)
        TextView deviceMacAddress;

        public BluetoothViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        public void bindData(BluetoothObject currentDevice) {
            deviceName.setText(currentDevice.getDeviceName());
            deviceMacAddress.setText(currentDevice.getDeviceMacAddress());
        }
    }
}
