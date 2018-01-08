package com.example.krot.bluetoothscannerdemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ToggleButton;

import com.example.krot.bluetoothscannerdemo.adapter.BluetoothDeviceAdapter;
import com.example.krot.bluetoothscannerdemo.model.BluetoothObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    //enable bluetooth
    private static final int ENABLE_BLUETOOTH_REQUEST_CODE = 1;

    //discoverable mode
    private static final int BLUETOOTH_DISCOVERABLE_REQUEST_CODE = 2;

    //extra time
    private static final int EXTRA_DISCOVERABLE_DURATION = 20;


    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDeviceAdapter adapter;
    private IntentFilter bluetoothStateIntentFilter = new IntentFilter();
    private IntentFilter scanningBluetoothIntentFilter = new IntentFilter();
    private LinkedHashMap<String, String> devicesLikedHashMap;

    //Bluetooth LE
    private boolean mScanning;


    @BindView(R.id.btn_re_scan)
    Button btnReScan;
    @BindView(R.id.devices_list)
    RecyclerView deviceList;
    @BindView(R.id.loading_devices_progress_bar)
    ProgressBar loadingDevicesProgressBar;


    //check bluetooth state receiver
    BroadcastReceiver bluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String stateAction = intent.getAction();
            Log.i("WTF", "" + stateAction);
            if (stateAction != null) {
                if (stateAction.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    Log.i("WTF", "state = " + state);
                    //STATE_OFF = 10
                    //STATE_TURNING_ON = 11
                    //STATE_ON = 12
                    //STATE_TURNING_OFF = 13
                    switch (state) {
                        case BluetoothAdapter.STATE_OFF:
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            clearData();
                            break;
                        case BluetoothAdapter.STATE_ON:
                            doDiscoverableMode();
                            break;
                        case BluetoothAdapter.STATE_TURNING_ON:
                            break;
                    }
                }
            } else {
                Log.i("WTF", "stateAction = null");
            }

        }
    };

    //scan devices receiver
    BroadcastReceiver scanRemoteDeviceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String scanAction = intent.getAction();
            if (scanAction != null) {
                if (scanAction.equals(BluetoothDevice.ACTION_FOUND)) {
                    //TODO: sử dụng LinkedHashMap<macAddress, deviceName>.
                    //TODO: check xem macAddress có chưa (contains)
                    //TODO: có thì add vào List<BluetoothObject>
                    Log.i("WTF", "ACTION_FOUND");
                    BluetoothDevice currentDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.i("WTF", "currentDevice: name = " + currentDevice.getName() + " - address = " + currentDevice.getAddress() + " - UUID = " + currentDevice.getUuids());
                    BluetoothObject currentObj = new BluetoothObject(currentDevice.getName(), currentDevice.getAddress());
                    devicesLikedHashMap = new LinkedHashMap<>();
                    List<BluetoothObject> mOldList = adapter.getDeviceList();
                    if (mOldList == null) {
                        mOldList = new ArrayList<>();
                    }
                    if (!devicesLikedHashMap.containsKey(currentObj.getDeviceMacAddress())) {
                        devicesLikedHashMap.put(currentObj.getDeviceMacAddress(), currentObj.getDeviceName());
                        mOldList.add(currentObj);
                        adapter.setDeviceList(mOldList);
                    }

                }

                else if (scanAction.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                    Log.i("WTF", "ACTION_DISCOVERY_STARTED");
                    btnReScan.setEnabled(false);
                    loadingDevicesProgressBar.setVisibility(View.VISIBLE);
                }

                else if (scanAction.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                    Log.i("WTF", "ACTION_DISCOVERY_FINISHED");
                    btnReScan.setEnabled(true);
                    loadingDevicesProgressBar.setVisibility(View.INVISIBLE);
                    devicesLikedHashMap.clear();
                }

//                } else if (scanAction.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
//                    final int status = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);
//                    Log.i("WTF", "SCAN_MODE_CHANGED: status = " + status);
//                    switch (status) {
//                        case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
//                            loadingDevicesProgressBar.setVisibility(View.INVISIBLE);
//                            btnReScan.setEnabled(true);
//                            break;
//                        case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
//                            break;
//                        case BluetoothAdapter.SCAN_MODE_NONE:
//                            break;
//                    }
//
//                }
            } else {
                Log.i("WTF", "scanAction = null");
            }
        }
    };


//    private ScanCallback scanCallback = new ScanCallback() {
//        @Override
//        public void onScanResult(int callbackType, ScanResult result) {
//            Log.i("WTF", "callbackType = " + callbackType);
////            Log.i("WTF", "result = " + result);
//            BluetoothDevice currentDevice = result.getDevice();
//            if (currentDevice != null) {
//                Log.i("WTF", "deviceName = " + currentDevice.getName() + " - macAddress = " + currentDevice.getAddress());
//                bluetoothDeviceList = adapter.getDeviceList();
//                if (bluetoothDeviceList == null) {
//                    bluetoothDeviceList = new ArrayList<>();
//                }
//
//                bluetoothDeviceList.add(currentDevice);
//                adapter.setDeviceList(bluetoothDeviceList);
//            } else {
//                Log.i("WTF", "currentDevice: status = " + currentDevice);
//            }
//        }
//
//        @Override
//        public void onBatchScanResults(List<ScanResult> results) {
//            Log.i("WTF", "onBatchScanResults: size = " + results.size());
//        }
//
//        @Override
//        public void onScanFailed(int errorCode) {
//            Log.i("WTF", "onScanFailed: errorCode = " + errorCode);
//        }
//    };



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);



//        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//            Log.i("WTF", "not supported BLE");
//        } else {
//            final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//            mBluetoothAdapter = bluetoothManager.getAdapter();
//        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.i("WTF", "not support bluetooth on this device");
            finish();
        } else {
            //register broadcast receiver for bluetooth state change
            bluetoothStateIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(bluetoothStateReceiver, bluetoothStateIntentFilter);
            //setup adapter to display list of nearby devices
            setUpBluetoothDeviceAdapter();
            btnReScan.setEnabled(false);
            if (mBluetoothAdapter.isEnabled()) {
                doDiscoverableMode();
            } else {
                Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetoothIntent, ENABLE_BLUETOOTH_REQUEST_CODE);
            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (mBluetoothAdapter.isDiscovering()) {
//            loadingDevicesProgressBar.setVisibility(View.VISIBLE);
//        } else {
//            loadingDevicesProgressBar.setVisibility(View.GONE);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothStateReceiver);
        unregisterReceiver(scanRemoteDeviceReceiver);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ENABLE_BLUETOOTH_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
//                scanLeDevice(mBluetoothAdapter.isEnabled());
            }
            else {
                return;
            }
        } else if (requestCode == BLUETOOTH_DISCOVERABLE_REQUEST_CODE) {
            Log.i("WTF", "resultCode = " + resultCode);
            if (resultCode == RESULT_CANCELED) {
                return;
            } else {
                scanningBluetoothIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);
//                scanningBluetoothIntentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
                scanningBluetoothIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                scanningBluetoothIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                registerReceiver(scanRemoteDeviceReceiver, scanningBluetoothIntentFilter);
                mBluetoothAdapter.startDiscovery();
            }

//            if (resultCode == EXTRA_DISCOVERABLE_DURATION) {
//                scanningBluetoothIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);
////                scanningBluetoothIntentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
//                scanningBluetoothIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
//                scanningBluetoothIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//                registerReceiver(scanRemoteDeviceReceiver, scanningBluetoothIntentFilter);
//                mBluetoothAdapter.startDiscovery();
//            } else {
//                Log.i("WTF", "CANCELED");
//            }

        }
    }


    @OnClick(R.id.btn_re_scan)
    public void doReScan() {
        clearData();
        mBluetoothAdapter.startDiscovery();
    }


    public void doDiscoverableMode() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, EXTRA_DISCOVERABLE_DURATION);
        startActivityForResult(discoverableIntent, BLUETOOTH_DISCOVERABLE_REQUEST_CODE);
    }


    public void setUpBluetoothDeviceAdapter() {
        adapter = new BluetoothDeviceAdapter();
        deviceList.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        deviceList.setLayoutManager(manager);
        deviceList.setItemAnimator(new DefaultItemAnimator());
    }

    public void clearData() {
        adapter.setDeviceList(new ArrayList<BluetoothObject>());
    }


//    private void scanLeDevice(final boolean enable) {
//        final BluetoothLeScanner bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
//        if (enable) {
//            // Stops scanning after a pre-defined scan period.
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mScanning = false;
//                    bluetoothLeScanner.stopScan(scanCallback);
//                }
//            }, SCAN_PERIOD);
//
//            mScanning = true;
//            bluetoothLeScanner.startScan(scanCallback);
//        } else {
//            mScanning = false;
//            bluetoothLeScanner.stopScan(scanCallback);
//        }
//    }
}
