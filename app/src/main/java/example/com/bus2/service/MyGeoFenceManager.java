package example.com.bus2.service;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import example.com.bus2.BuildConfig;

import static android.content.Context.ALARM_SERVICE;
import static example.com.bus2.service.BleScanService.EXTRA_STARTED_FROM_GEOFENCE;
import static example.com.bus2.service.BroadcastReceiverGlobal.WAKE_UP_AFTER_ONE_HOUR;
import static example.com.bus2.service.BleScanService.TAG;

/**
 * Created by Arkady G on 26/06/19.
 *
 * As normal google geofence does not work well, and it's not clear why
 * I have to implement this by my own.
 *
 * hopefully one day, we will learn how to use the original google mechanism.
 *
 * for now, the idea is like this.
 *
 * assume you have only one geofence
 * once a hour, check how far are you from the geofence.
 *
 * if you far more than 100 km, check it next hour
 * if you away about 60 km, check it 60 min
 * if you are 50 km, check 50 min, and so on,
 * the time to check is same as your distance.
 *
 * the minimal frequncy is 5 minut, don't check more that that.
 *
 * if you do inside your geofence, just start the service as a foreground service
 * (hopefully it's posible).
 *
 * if you exit geofence, turn the bt service off
 *
 * in my opinion, using coarse location is good enouth for the task, and saves the battery.
 *
 *
 */

public class MyGeoFenceManager {

    //data
    private static MyGeoFenceManager myGeoFenceManager;
    private static Context myCtx;
    private static final int MINUTE = 60*1000;

    private Location bar_ilan = new Location("");
    //32.068426, 34.843152

    private MyGeoFence[] mygeofences = null;

    //constructor
    public MyGeoFenceManager() {
        bar_ilan.setLatitude(32.068426);
        bar_ilan.setLongitude(34.843152);

    }


    //functions
    public static MyGeoFenceManager getInstance(Context ctx) {
        if (myGeoFenceManager == null) {
            myGeoFenceManager = new MyGeoFenceManager();
        }

        myCtx = ctx;

        return myGeoFenceManager;
    }


    public void setGeofencesList(JSONArray geofences) {
        mygeofences = new MyGeoFence[geofences.length()];

        JSONObject obj;

        for (int i=0;i<geofences.length();i++){
            mygeofences[i] = new MyGeoFence();

            try {
                obj = (JSONObject)geofences.get(i);

                mygeofences[i].name = obj.getString("name");
                mygeofences[i].loc.setLatitude(obj.getDouble("lat"));
                mygeofences[i].loc.setLongitude(obj.getDouble("lon"));
                mygeofences[i].radius = obj.getInt("radius");

            } catch (JSONException e) {
                e.printStackTrace();
                mygeofences = null;
                return;
            }
        }
    }

    //timer - calendar calls

    public void scheduleLongTimerEvent(int delayTimeMS) {

        if (myCtx == null) {
            return;
        }

        Intent intent = new Intent(myCtx, BroadcastReceiverGlobal.class);
        intent.setAction(WAKE_UP_AFTER_ONE_HOUR);

//        PendingIntent pendingIntent = PendingIntent.getBroadcast(myCtx, 0,
//                new Intent(WAKE_UP_AFTER_ONE_HOUR),
//                PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(myCtx, 0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // wake up time every 1 hour
        Calendar wakeUpTime = Calendar.getInstance();
        Log.i(TAG, "scheduling next system report after: " + delayTimeMS);
        wakeUpTime.add(Calendar.MILLISECOND, delayTimeMS);

        AlarmManager aMgr = (AlarmManager) myCtx.getSystemService(ALARM_SERVICE);
        aMgr.set(AlarmManager.RTC_WAKEUP,
                wakeUpTime.getTimeInMillis(),
                pendingIntent);
    }

    public void onLongTimerEvent() {
//        here we should grab system status, and update web-server
//                ideally, once a hour

        Location loc = getLastKnownLocation();
        if (BuildConfig.DEBUG){
            Log.i(TAG, "last know location "+loc);
        }


        if (loc == null){
            //we don't know our position, don't start any event
            //check in 5 minutes
            Log.i(TAG, " next check set to:  "+5*MINUTE);
            scheduleLongTimerEvent(5*MINUTE);
            return;
        }

        //here we are checking if we are closed enough to some geopoint

        double distMin = 60*1000;
        double dist = 0;

        if (mygeofences != null){

            for (int i=0;i<mygeofences.length;i++){
                //calc min dist
                dist = loc.distanceTo(mygeofences[i].getLocation());

                if (dist < mygeofences[i].radius){
                    //we are inside geofence, start
                    Intent intent = new Intent(myCtx, BleScanService.class);
                    intent.putExtra(EXTRA_STARTED_FROM_GEOFENCE, true);
                    //myCtx.startService(intent);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        myCtx.startForegroundService(intent);
                    } else {
                        myCtx.startService(intent);
                    }


                    //reschedule event for next time
                    Log.i(TAG, " next check set to:  "+5*MINUTE);
                    scheduleLongTimerEvent(5*MINUTE);

                    //stop the foor loop
                    return;
                }

                //if we are not in the geofence, determine min distance
                if (dist < distMin){
                    distMin = dist;
                }
            }

        }
        else{
            //a fallback case, till we know settings works well
            dist = loc.distanceTo(bar_ilan);
            if (dist < 1000){
            //we are near bar ilan, less than 1km, start the service

            Intent i = new Intent(myCtx, BleScanService.class);
            i.putExtra(EXTRA_STARTED_FROM_GEOFENCE, true);
            myCtx.startService(i);

            //reschedule event for next time
            Log.i(TAG, " next check set to:  "+5*MINUTE);
            scheduleLongTimerEvent(5*MINUTE);

            return;
            }
        }



        //-----------

        if (dist > 60*1000){
            //more than 60 km
            Log.i(TAG, " next check set to:  "+60*MINUTE);
            scheduleLongTimerEvent(60*MINUTE);
            return;
        }

        if (dist > 5000 && dist < 60*1000){
            //between 5 km and 60 km
            Log.i(TAG, " next check set to:  "+(int)dist/1000*MINUTE);
            scheduleLongTimerEvent((int)dist/1000*MINUTE);
            return;
        }

//        if (dist < 1000){
//            //we are near bar ilan, less than 1km, start the service
//
//            Intent i = new Intent(myCtx, BleScanService.class);
//            i.putExtra(EXTRA_STARTED_FROM_GEOFENCE, true);
//            myCtx.startService(i);
//
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////            myCtx.startForegroundService(i);
////        } else {
////            myCtx.startService(i);
////        }
//
//            //reschedule event for next hour
//            Log.i(TAG, " next check set to:  "+5*MINUTE);
//            scheduleLongTimerEvent(5*MINUTE);
//
//            return;
//        }

        //if for some case we got here, check again in 3 minutes
        Log.i(TAG, " next check set to:  "+3*MINUTE);
        scheduleLongTimerEvent(3*MINUTE);





    }

    public void stopSheduling() {
        //do we really have a usecase for stop ??
        //maybe only for forced
    }

    public Location getLastKnownLocation() {
        LocationManager locationManager = (LocationManager) myCtx.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(myCtx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(myCtx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocationGPS != null) {
                return lastKnownLocationGPS;
            }

            Location lastKnownLocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (lastKnownLocationNetwork != null) {
                return lastKnownLocationNetwork;
            }

            Location lastKnownLocationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (lastKnownLocationPassive != null) {
                return lastKnownLocationPassive;
            }
        }
        return null;
    }

    private class MyGeoFence{

        String name;

        Location loc = new Location("");
//        double lat;
//        double lon;
        int radius;

        public Location getLocation(){
            return loc;
        }

    }

}
