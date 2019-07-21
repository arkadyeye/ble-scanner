package example.com.bus2.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import example.com.bus2.service.MyGeoFenceManager;

import static example.com.bus2.service.BleScanService.TAG;

/**
 * Created by Arkady Gorodischer on 17/07/18.
 */

public class BroadcastReceiverGlobal extends BroadcastReceiver {

    private static final String BleApp = "BleAppUpdate";
    public static final String WAKE_UP_AFTER_ONE_HOUR = "wakeUpLongTimer";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG,"got intent: "+intent.getAction());

        if (intent == null){ return;}

        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {

            MyGeoFenceManager.getInstance(context).onLongTimerEvent();

//            GeofenceManager.SetGeofence(context);

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(new Intent(context, BleScanService.class));
//            } else {
//                context.startService(new Intent(context, BleScanService.class));
//            }

//            Intent serviceIntent=new Intent(context,BleScanService.class);
//            context.startService(serviceIntent);
        }

//        if (intent.getAction().equalsIgnoreCase(TECH_DEMO)) {
//           Controller.getInstance().onTechDemoBroadcast(intent);
//        }

        if (intent.getAction().equalsIgnoreCase(WAKE_UP_AFTER_ONE_HOUR)) {
            MyGeoFenceManager.getInstance(context).onLongTimerEvent();
        }

        //TODO on network state changed
    }
}
