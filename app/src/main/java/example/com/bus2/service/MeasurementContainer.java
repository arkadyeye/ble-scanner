package example.com.bus2.service;

import android.location.Location;

import com.google.firebase.database.DatabaseReference;

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


    //setters

    public void addDevice(String macAddress, int valueDB){
        if (devicesDiscovered.containsKey(macAddress)) {//check if the BLE is in the list
            //if we already have this address, we shoud update it to a maximal measured db
            int oldDB = devicesDiscovered.get(macAddress);
            devicesDiscovered.put(macAddress, Math.max(oldDB,valueDB));
        }
        else{
            //devicesDiscovered.put(macAddress,String.valueOf(valueDB));
            devicesDiscovered.put(macAddress,valueDB);
        }
    }

    public void setLatLon(Location location){
        this.location = location;
    }

    public boolean isEmptyLocation(){
        if (this.location == null) return true;
        return false;
    }

    public boolean isSameLocation(Location location){
        if (this.location == null) return false;

        //distance is in meters
        if (this.location.distanceTo(location) > MIN_DISTANCE) return false;

        return true;
    }



    //sender

    public void send(DatabaseReference dbReference, String name){

        String GPS_parmas;

        if (location == null ){
            GPS_parmas = "null;null";
        }
        else{
            GPS_parmas = location.getLatitude()+";"+location.getLongitude();
        }

        Date cur_date = new Date();
        String format = simpleDateFormat.format(cur_date);
        format = format + ";" + GPS_parmas.replace(".","_");

        String data = devicesDiscovered.toString();

        dbReference.child(name).child(format).setValue(data);
    }

    public String toString(){

        String macsList = devicesDiscovered.toString();
        macsList = macsList.replace(",","\n");

        if (location == null){
            return "[ nowhere ]\n" + macsList;
        }
        else{
            return "[ "+location.getLatitude()+","+location.getLongitude()+" ]\n" + macsList;
        }


    }


}
