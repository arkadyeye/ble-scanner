package example.com.bus2.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static example.com.bus2.service.BleScanService.TAG;

/**
 * Created by Arkady G on 13/06/19.
 *
 * this class should scan BLE devices upon request
 */

public class BleManager {

    //data
    private BluetoothManager btManager;
    private BluetoothAdapter btAdapter;
    private BluetoothLeScanner btScanner;

    private ScanSettings settings;
    private List<ScanFilter> filters = new ArrayList<ScanFilter>();

    private Controller listener;

    //constructor

    public BleManager(Context ctx, Controller listener,boolean useBtFilter, JSONArray filterMacs){

        Log.i(TAG,"BleScanner recreated");

        this.listener = listener;

        btManager = (BluetoothManager)ctx.getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();



        settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                //.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .build();


        //create fillters
        if (filterMacs != null) {
            //build the filter
            ScanFilter filter;
            for (int i = 0; i < filterMacs.length(); i++) {
                try {
                    filter = new ScanFilter.Builder().setDeviceAddress(filterMacs.getString(i)).build();
                    filters.add(filter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        if (useBtFilter){
            //start scan
            startFilteredScanning();
        }
        else{
            startOverAllScanning();
        }
    }

    //functions
    public void enablePredefinedFilter(boolean isEnabled){

        stopScanning();

        if (isEnabled){
            startFilteredScanning();
        }
        else{
            startOverAllScanning();
        }

    }

    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            listener.onBleScanResults(result);
        }
    };

    public void startOverAllScanning() {
        Log.i(TAG,"starting over all scan");
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.startScan(leScanCallback);
            }
        });
    }

    public void startFilteredScanning() {
        Log.i(TAG,"starting filtered scan");
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.startScan(filters,settings,leScanCallback);
            }
        });
    }

    public void stopScanning() {
        Log.i(TAG,"stopping scan");
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.stopScan(leScanCallback);
            }
        });
    }




}
