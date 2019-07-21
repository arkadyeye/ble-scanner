package example.com.bus2.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import example.com.bus2.R;
import example.com.bus2.app.ConfActivity;
import example.com.bus2.app.MainActivity;

/**
 * Created by Arkady G on 13/06/19.
 *
 * The main intent of this service, is to scan BLE devices in background
 * and from time to time send the data to some remove server
 */

public class BleScanService extends Service {

    //data
    public static String TAG = "BleService";

    public static final String SETTINGS_FOLDER = "/ble_scan/";

    public static final String ACTION_BROADCAST = TAG + ".broadcast";
    public static final String EXTRA_LOCATION = TAG + ".extra_data";

    private final IBinder mBinder = new LocalBinder();

    private boolean mChangingConfiguration = false;

    private NotificationManager mNotificationManager;
    private static final String CHANNEL_ID = "channel_01";
    private static final int NOTIFICATION_ID = 12345678;

    private static final String EXTRA_STARTED_FROM_NOTIFICATION = TAG +
            ".started_from_notification";

    static final String EXTRA_STARTED_FROM_GEOFENCE = TAG +
            ".started_from_geofence";

    static final String EXTRA_STARTED_FROM_GEOFENCE_OFF = TAG +
            ".started_from_geofence_off";

    public static final String EXTRA_STARTED_FROM_ACTIVITY_OFF = TAG +
            ".started_from_activity_off";

    private Handler mServiceHandler;



    private ErrorReporter errorReporter;
    private Controller cont;

    private boolean isBound = false; //if bound - no foreground



//    public IBinder onBind(Intent intent) {
//        Log.i(TAG,"service binds");
//        return mBinder;
//    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof ErrorReporter)) {
            errorReporter = ErrorReporter.getInstance();
            errorReporter.Init(this);
        }

        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }

        cont = new Controller(this);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //init controller with context


        Log.i(TAG, "Service started");
        boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION,
                false);

        boolean startedFromGeofenceOFF = intent.getBooleanExtra(EXTRA_STARTED_FROM_GEOFENCE_OFF,
                false);

        boolean startedFromActivityOff = intent.getBooleanExtra(EXTRA_STARTED_FROM_ACTIVITY_OFF,false);

        // We got here because the user decided to remove location updates from the notification.
        // of we exit the geofence that we are interested in
        if (startedFromNotification || startedFromGeofenceOFF || startedFromActivityOff) {
            cont.finish();
            stopSelf();

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.exit(0);
        }

        boolean startedFromGeofence = intent.getBooleanExtra(EXTRA_STARTED_FROM_GEOFENCE,
                false);

        if (startedFromGeofence){

            //this should be called only if the service is not running at all, how to know this ?
            if (isBound == false && serviceIsRunningInForeground(this) == false) {
                Log.i(TAG, "Starting foreground service");
                cont = new Controller(this);
                startForeground(NOTIFICATION_ID, getNotification());
            }
        }




        // Tells the system to not try to recreate the service after it has been killed.
        return START_NOT_STICKY;
        //return  START_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) comes to the foreground
        // and binds with this service. The service should cease to be a foreground service
        // when that happens.
        Log.i(TAG, "in onBind()");
        stopForeground(true);
        mChangingConfiguration = false;
        isBound = true;
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) returns to the foreground
        // and binds once again with this service. The service should cease to be a foreground
        // service when that happens.
        Log.i(TAG, "in onRebind()");
        stopForeground(true);
        mChangingConfiguration = false;
        isBound = true;
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Last client unbound from service");

        // Called when the last client (MainActivity in case of this sample) unbinds from this
        // service. If this method is called due to a configuration change in MainActivity, we
        // do nothing. Otherwise, we make this service a foreground service.
        if (!mChangingConfiguration) {
            Log.i(TAG, "Starting foreground service");
            startForeground(NOTIFICATION_ID, getNotification());
            isBound = false;
        }
        return true; // Ensures onRebind() is called when a client re-binds.
    }

    @Override
    public void onDestroy() {
        cont.finish();
        Log.i(TAG,"service destroy");
    }

    private Notification getNotification() {
        Intent intent = new Intent(this, BleScanService.class);

        //CharSequence text = Utils.getLocationText(mLocation);

        // Extra to help us figure out if we arrived in onStartCommand via the notification or not.
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);

        // The PendingIntent that leads to a call to onStartCommand() in this service.
        PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // The PendingIntent to launch activity.
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, ConfActivity.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
//                .addAction(R.drawable.ic_launch, getString(R.string.launch_activity),
//                        activityPendingIntent)
//                .addAction(R.drawable.ic_cancel, getString(R.string.remove_location_updates),
//                        servicePendingIntent)


//                .addAction(R.drawable.ic_launcher_foreground, "Conf Activity",
//                        activityPendingIntent)
//                .addAction(R.drawable.ic_launcher_background, "STOP",
//                        servicePendingIntent)
                .setContentText(getString(R.string.expl))
                .setContentTitle(getString(R.string.bus))
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.bus)
                .setTicker("sticker text")
                .setWhen(System.currentTimeMillis());

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }

        return builder.build();
    }

    public class LocalBinder extends Binder {
        public BleScanService getService() {
            return BleScanService.this;
        }
    }

    public String getLastScan(){
        return "bla bla bla...";
    }

    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (getClass().getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean serviceIsRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {

            if (getClass().getName().equals(service.service.getClassName())) {
                //if (service.foreground) {
                    return true;
                //}
            }
        }
        return false;
    }

}
