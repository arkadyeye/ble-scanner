package example.com.bus2.service;

import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import example.com.bus2.BuildConfig;
import example.com.bus2.app.ConfActivity;

import static example.com.bus2.service.BleScanService.ACTION_BROADCAST;
import static example.com.bus2.service.BleScanService.EXTRA_LOCATION;
import static example.com.bus2.service.BleScanService.TAG;


/**
 * Created by Arkady G on 13/06/19.
 *
 * this class controls dataflow from ble scanner to data storage
 *
 *
 * Few modules shoub be controlled by this calss
 *
 * 1) BT scanner
 * 2) GPS accurate location
 * 3) GeoFence (?) location, to enable GPS only at bar-ilan/ariel teretory
 * 4) upload data to firebase
 *
 */

public class Controller implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    //data
    private Context ctx;
    private BleManager bleScanner;
    private KcgLocationManager locationManager = null;

    private SettingsManager settings;

    private Handler mHandler = new Handler();
    private int updatePeriodMS = 13*1000;// 13 sec
    private boolean continueUpdating = true;


//    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private DatabaseHelper databaseHelper;
    private ArrayList<MeasurementContainer> measuredData = new ArrayList<>();

    private SharedPreferences preferences;
    private String name = "no name yet";

    //public static final boolean DEBUG = true;

    //controller
    public Controller(Context ctx){

        this.ctx = ctx;

        preferences = PreferenceManager.getDefaultSharedPreferences(ctx);

        boolean useBtFilter = preferences.getBoolean("use_bt_filter",false);

        int locationMode = Integer.parseInt(preferences.getString("location_mode","102"));

        updatePeriodMS = 1000*Integer.parseInt(preferences.getString("update_period","13"));


//        String preffName = preferences.getString("name","na");
        name = getDeviceName();
        preferences.edit().putString("name",name).apply();


//        String preffName = preferences.getString("name","na");
//        if (preffName.equals("na") == false){
//            //meaning we already have a name
//            name = preffName;
//        }
//        else{
//            //we don't have a name, as google ads for it
//            //new GetGAIDTask().execute();
//            name = getDeviceName();
//            preferences.edit().putString("name",name).apply();
//        }

        if (BuildConfig.DEBUG) {
            Log.i(TAG,"acctual name: "+name);
            Log.i(TAG,"useBtFilter: "+useBtFilter);
            Log.i(TAG,"location_mode: "+locationMode);
            Log.i(TAG,"update_perioud: "+updatePeriodMS);
        }




        settings = new SettingsManager(ctx);
        int err = settings.init();
        if (err != 0){
            Log.e(TAG," error "+err+" in settings file ");
        }

        bleScanner = new BleManager(ctx,this,settings.getMacs());

        locationManager = new KcgLocationManager(ctx,this,locationMode);

        preferences.registerOnSharedPreferenceChangeListener(this);

        //auth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null){
            mAuth.signInAnonymously()
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Anon", "signInAnonymously:success");
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("Anon", "signInAnonymously:failure", task.getException());
                            }
                        }
                    });

        }
        //currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        scheduleUpdate();

//        MyGeoFenceManager myGeoFenceManager = MyGeoFenceManager.getInstance(ctx);
//        myGeoFenceManager.setGeofencesList(settings.getGeofences());
//        myGeoFenceManager.scheduleLongTimerEvent(3000);

//        FunctionEveryHour scheduler = new FunctionEveryHour();
//
//        ctx.registerReceiver(scheduler , new IntentFilter(WAKE_UP_AFTER_ONE_HOUR));
    }



//    // broadcastreceiver to handle your work
//    class FunctionEveryHour extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            Log.i(TAG,"got long event broadcast");
//
//            // if phone is lock use PowerManager to acquire lock
//
//            // your code to handle operations every one hour...
//
//            // after that call again your method to schedule again
//            // if you have boolean if the user doesnt want to continue
//            // create a Preference or store it and retrieve it here like
//
//            //boolean mContinue = getUserPreference(USER_CONTINUE_OR_NOT);//
//
//            MyGeoFenceManager myGeoFenceManager = MyGeoFenceManager.getInstance(ctx);
//            myGeoFenceManager.scheduleLongTimerEvent(3000);
//        }
//    }


    //functions

    public void finish(){

        continueUpdating = false;

        bleScanner.stopScanning();

        if (locationManager != null){
            locationManager.stopLocationUpdate();
            locationManager = null;
        }

        preferences.unregisterOnSharedPreferenceChangeListener(this);
    }


    public void onBleScanResults(ScanResult result){
        if (measuredData.size() == 0){
            //means the list is empty
            measuredData.add(new MeasurementContainer());
        }

        measuredData.get(measuredData.size()-1).addDevice(result.getDevice().getAddress(),result.getRssi());

        if (BuildConfig.DEBUG)Log.i(TAG,"Device Address: " + result.getDevice().getAddress() + " rssi: " + result.getRssi() + "\n");

    }

    public void onLocationResults(Location location){

        if (BuildConfig.DEBUG)Log.i(TAG, "New location: " + location.getLatitude() +" , "+location.getLongitude());

        //if we have empty list, add container and return;
        if (measuredData.size() == 0){
            //means the list is empty
            measuredData.add(new MeasurementContainer());
            measuredData.get(measuredData.size()-1).setLatLon(location);
            return;
        }

        //maybe the bt data exist, but location is empty, so add it
        MeasurementContainer tmp = measuredData.get(measuredData.size()-1);
        if (tmp.isEmptyLocation()){
            tmp.setLatLon(location);
            return;
        }

        //check if we have a different location
        if (tmp.isSameLocation(location) == false){
            measuredData.add(new MeasurementContainer());
            measuredData.get(measuredData.size()-1).setLatLon(location);
        }

        //if we are here, we got (more or less) the same location, as in previous case, leave it as is.
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals("use_bt_filter")){
            boolean useBtFilter = sharedPreferences.getBoolean("use_bt_filter",false);
            bleScanner.enablePredefinedFilter(useBtFilter);
        }

        if (key.equals("location_mode")){
            int locationMode = Integer.parseInt(preferences.getString("location_mode","102"));
            locationManager.setWorkingMode(ctx,locationMode);
        }

        if (key.equals("update_period")){
            updatePeriodMS = 1000*Integer.parseInt(preferences.getString("update_period","13"));
        }

        if (key.equals("name")){
            name = preferences.getString("name","na");
            if (BuildConfig.DEBUG) Log.i("BleService","acctual name updated: "+name);
        }


    }

    private String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    // res1.append(Integer.toHexString(b & 0xFF) + ":");
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            //handle exception
            Log.e("BleActivity"," can't get mac address of the device");
            System.exit(0);
        }
        return "";
    }

    private String getDeviceName(){
        String mac = getMacAddr();

        //String hash = sha1.getHash(mac);
        String hash = GFG.getHash(mac);
        return hash;



    }


    //---------- update timer -----------

    public void scheduleUpdate() {

        if (BuildConfig.DEBUG)Log.i(TAG,"update sheduled every "+updatePeriodMS);

        String extraData = "";

        for (MeasurementContainer data : measuredData){

            //send data to firebase
            data.send(mDatabase,name);

            //prepare data
            extraData += data.toString();
        }

        Intent intent = new Intent(ACTION_BROADCAST);
        intent.putExtra(EXTRA_LOCATION, extraData);

        LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);

        measuredData.clear();

        if (continueUpdating){
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scheduleUpdate();
                }
            }, updatePeriodMS);
        }
    }

    //getting ad ID

    private class GetGAIDTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            AdvertisingIdClient.Info adInfo;
            adInfo = null;
            try {
                adInfo = AdvertisingIdClient.getAdvertisingIdInfo(Controller.this.ctx);
                if (adInfo.isLimitAdTrackingEnabled()) // check if user has opted out of tracking
                    return "did not found GAID... sorry";
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            }
            return adInfo.getId();
        }

        @Override
        protected void onPostExecute(String s) {

            if (BuildConfig.DEBUG)Log.i(TAG,"got google ads id "+s);

            name = sha1.getHash(s);
            preferences.edit().putString("name",name).apply();

        }
    }



}
