package example.com.bus2.service;

import android.location.Location;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arkady G on 25/06/19.
 */

public class MeasurementContainer {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");

    private static float MIN_DISTANCE = 0.75f;

    //data
    private Map<String, Integer> devicesDiscovered = new HashMap<>();
    private Location location = null;

    private int maxValue = -200;


    //setters

    public void addDevice(String macAddress, int valueDB) {
        if (devicesDiscovered.containsKey(macAddress)) {//check if the BLE is in the list
            //if we already have this address, we shoud update it to a maximal measured db
            int oldDB = devicesDiscovered.get(macAddress);
            devicesDiscovered.put(macAddress, Math.max(oldDB, valueDB));
        } else {
            //devicesDiscovered.put(macAddress,String.valueOf(valueDB));
            devicesDiscovered.put(macAddress, valueDB);
        }

        //new functionality is added. beeping on a max device.
        //for mee, it looks logical to add here a max comperanse, instead of searching it each time max requested
        if (maxValue < valueDB){
            maxValue = valueDB;
        }
    }

    public void setLatLon(Location location) {
        this.location = location;
    }

    public boolean isEmptyLocation() {
        if (this.location == null) return true;
        return false;
    }

    public boolean isSameLocation(Location location) {
        if (this.location == null) return false;

        //distance is in meters
        if (this.location.distanceTo(location) > MIN_DISTANCE) return false;

        return true;
    }


    //sender

    public void send(DatabaseReference dbReference, String name) {

        String GPS_parmas;

        if (location == null) {
            GPS_parmas = "null;null";
        } else {
            GPS_parmas = location.getLatitude() + ";" + location.getLongitude();
        }

        Date cur_date = new Date();
        String format = simpleDateFormat.format(cur_date);
        format = format + ";" + GPS_parmas.replace(".", "_");

        String data = devicesDiscovered.toString();

        dbReference.child(name).child(format).setValue(data);
    }

    public String toString() {

        //acctualy, I want to pass a json, to be shown on the map

        // String macsList = devicesDiscovered.toString();

        JSONObject result = new JSONObject();
        JSONArray devices = new JSONArray();

        try {
            for (Map.Entry<String, Integer> pair : devicesDiscovered.entrySet()) {
                JSONObject dev = new JSONObject();
                dev.put("mac", pair.getKey());
                dev.put("rssi", pair.getValue());
                devices.put(dev);

            }

            result.put("bt", devices);

//        Log.i("ark","device discovered: "+macsList);

//        macsList = macsList.replace(",","\n");


            if (location != null) {
                result.put("lat", location.getLatitude());
                result.put("lon", location.getLongitude());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result.toString();

    }

    public int getMaxRssi_old(){

        int max = -120;

        for (Map.Entry<String, Integer> pair : devicesDiscovered.entrySet()) {
            if (pair.getValue() > max){
                max = pair.getValue();
            }
        }

        return max;
    }

    public int getMaxRssi(){
        return maxValue;
    }



}
