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

    //temporary predefined macs address list
//    private String[] macs = {"30:45:11:5D:C6:31",
//            "40:BD:32:6A:A2:C8",
//            "40:BD:32:B7:63:BF",
//            "12:3B:6A:1B:3F:02",
//            "30:45:11:5D:86:61",
//            "30:45:11:5D:53:3C",
//            "40:BD:32:B7:5F:B3",
//            "40:BD:32:B7:63:A0",
//            "30:45:11:5D:53:69",
//            "12:3B:6A:1B:3F:79",
//            "12:3B:6A:1B:3E:8B",
//            "40:BD:32:B7:5C:14",
//            "40:BD:32:6A:A5:92",
//            "40:BD:32:B7:59:69",
//            "40:BD:32:6A:A2:EB",
//            "30:45:11:5D:A7:C2",
//            "40:BD:32:B7:5C:35",
//            "30:45:11:5D:58:08",
//            "40:BD:32:B7:63:84",
//            "30:45:11:5D:53:2E",
//            "40:BD:32:B7:5C:23",
//            "40:BD:32:B7:5C:0F",
//            "30:45:11:5D:BA:26",
//            "40:BD:32:B7:63:DE",
//            "30:45:11:5D:B4:B3",
//            "30:45:11:5D:A7:DA",
//            "30:45:11:5D:AB:EF",
//            "30:45:11:5D:C6:47",
//            "30:45:11:5D:C6:4E",
//            "30:45:11:5D:58:1B",
//            "30:45:11:5D:BA:67",
//            "30:45:11:5D:AD:EB",
//            "40:BD:32:B7:5C:61",
//            "40:BD:32:B7:5C:63",
//            "30:45:11:5D:BA:2D",
//            "30:45:11:5D:BA:75",
//            "30:45:11:5D:C6:05",
//            "40:BD:32:B7:63:98",
//            "30:45:11:5D:A7:E3",
//            "30:45:11:5D:BA:5F",
//            "30:45:11:5D:58:25",
//            "30:45:11:5D:53:53"
//    };

    //constructor

    public BleManager(Context ctx, Controller listener, JSONArray filterMacs){

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

        if (filterMacs != null){
            //build the filter
            ScanFilter filter;
            for (int i=0;i<filterMacs.length();i++){
                try {
                    filter = new ScanFilter.Builder().setDeviceAddress(filterMacs.getString(i)).build();
                    filters.add(filter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //start scan
            startFilteredScanning();
        }
        else{
            startOverAllScanning();
        }


//        if (btAdapter != null && !btAdapter.isEnabled()) {
//            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
//        }

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

            //peripheralTextView.append("Device Name: " + result.getDevice().getName() + " rssi: " + result.getRssi() + "\n");

            //stopScanning();

//            // auto scroll for text view
//            final int scrollAmount = peripheralTextView.getLayout().getLineTop(peripheralTextView.getLineCount()) - peripheralTextView.getHeight();
//            // if there is no need to scroll, scrollAmount will be <=0
//            if (scrollAmount > 0)
//                peripheralTextView.scrollTo(0, scrollAmount);
        }
    };

    public void startOverAllScanning() {
//        peripheralTextView.setText("");
//        startScanningButton.setVisibility(View.INVISIBLE);
//        stopScanningButton.setVisibility(View.VISIBLE);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.startScan(leScanCallback);
            }
        });
    }

    public void startFilteredScanning() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.startScan(filters,settings,leScanCallback);
            }
        });
    }

    public void stopScanning() {
//        peripheralTextView.append("Stopped Scanning");
//        startScanningButton.setVisibility(View.VISIBLE);
//        stopScanningButton.setVisibility(View.INVISIBLE);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.stopScan(leScanCallback);
            }
        });
    }




}
