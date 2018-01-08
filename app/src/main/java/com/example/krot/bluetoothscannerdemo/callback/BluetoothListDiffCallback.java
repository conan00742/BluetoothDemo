package com.example.krot.bluetoothscannerdemo.callback;

import android.support.v7.util.DiffUtil;

import com.example.krot.bluetoothscannerdemo.model.BluetoothObject;

import java.util.List;

/**
 * Created by Krot on 1/8/18.
 */

public class BluetoothListDiffCallback extends DiffUtil.Callback {

    private List<BluetoothObject> mOldList;
    private List<BluetoothObject> mNewList;

    public BluetoothListDiffCallback(List<BluetoothObject> mOldList, List<BluetoothObject> mNewList) {
        this.mOldList = mOldList;
        this.mNewList = mNewList;
    }

    @Override
    public int getOldListSize() {
        return mOldList != null ? mOldList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return mNewList != null ? mNewList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldList.get(oldItemPosition).equals(mNewList.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldList.get(oldItemPosition).getDeviceName().equals(mNewList.get(newItemPosition).getDeviceName());
    }
}
