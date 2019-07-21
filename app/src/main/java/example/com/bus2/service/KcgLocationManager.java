package example.com.bus2.service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import static example.com.bus2.service.BleScanService.TAG;

/**
 * Created by Arkady G on 19/06/19.
 *
 * This class should take care about all the location data
 *
 * Looks like google advise to use fuzed location.
 * hopefully. it will consume less battery, than just trigering the gps every second
 */

public class KcgLocationManager {

    private int UPDATE_INTERVAL_IN_MILLISECONDS = 5 * 1000;
    private int FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 1000;

    private FusedLocationProviderClient mFusedLocationClient;

    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private Location mLocation;

    private Controller listener;

    public KcgLocationManager(Context ctx, Controller listener, int workingMode) {

        this.listener = listener;

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };

        setWorkingMode(ctx,workingMode);


//        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Log.e(BleScanService.TAG,"gps persmission problem");
//            return;
//        }
//        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
//                mLocationCallback,
//                null /* Looper */);
    }


    private void onNewLocation(Location location) {


        mLocation = location;

        listener.onLocationResults(location);

//        // Notify anyone listening for broadcasts about the new location.
//        Intent intent = new Intent(ACTION_BROADCAST);
//        intent.putExtra(EXTRA_LOCATION, location);
//        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
//
//        // Update notification content if running as a foreground service.
//        if (serviceIsRunningInForeground(this)) {
//            mNotificationManager.notify(NOTIFICATION_ID, getNotification());
//        }
    }

    public void stopLocationUpdate(){
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    public void setWorkingMode(Context ctx,int mode){
        //stop the old one
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);

        //build new location request
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(mode);

//        Log.i(TAG,"location mode set to: "+mode);

        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG,"gps persmission problem");
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);
    }
}

