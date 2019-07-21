package example.com.bus2.app;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import example.com.bus2.R;
import example.com.bus2.service.BleScanService;

import static example.com.bus2.service.BleScanService.EXTRA_STARTED_FROM_ACTIVITY_OFF;


public class MainActivity extends AppCompatActivity {



    String name = "";
    TextView peripheralTextView;
    private final static int REQUEST_ENABLE_BT = 1;
    private final static int NOTIFICATION_ID = 100;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_FINE_LOCATION = 2;
    private static final int SCAN_HALT = 3000;
    private static final long SCAN_PERIOD = 10000;
    private static final long MILS_TO_SEC = 1000;
    Date start = new Date();

    private int titleClickCounter = 0;

    //---------------------
    private SharedPreferences sharedPreferences;

    // The BroadcastReceiver used to listen to broadcasts from the service.
    private MyReceiver myReceiver;

    private BleScanService mService = null;

    // Tracks the bound state of the service.
    private boolean mBound = false;

    // Monitors the state of the connection to the service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BleScanService.LocalBinder binder = (BleScanService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };

    //-----------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myReceiver = new MyReceiver();

        setContentView(R.layout.activity_main);

        //add title bar
        LayoutInflater inflater = LayoutInflater.from(this);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.custom_title, null, false);

        FrameLayout.LayoutParams layoutParams = null;
        layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.TOP;

        addContentView(layout, layoutParams);

        //-------------------------------

        Bundle extras = getIntent().getExtras();
        //name = extras.getString("Mac");
//        research = extras.getBoolean("Research");
//        if(!research){//end the app
//            //this.Exit_app();
//            System.exit(0);
//        }
//        gps = extras.getBoolean("GPS");
//        ble = extras.getBoolean("BLE");
//        pref = extras.getString("Prefix");
//        if (pref==null){
//            pref="";
//        }



        //create notification

        //notification




        peripheralTextView = (TextView) findViewById(R.id.PeripheralTextView);
        peripheralTextView.setMovementMethod(new ScrollingMovementMethod());

    }



    @Override
    protected void onStart() {
        super.onStart();




        //allocate listener for the settings button
        //ImageButton settings = findViewById(R.id.setting_button);

        //Dr. Yuval Hadas asked to hide the settings button
        TextView title = findViewById(R.id.title_text);

        title.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                titleClickCounter++;

                if (titleClickCounter > 4){
                    titleClickCounter = 0;
                    Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                    i.putExtra("FROM_PREVIEW", true);
                    startActivity(i);
                }


            }
        });

        startService(new Intent(this, BleScanService.class));

        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
        bindService(new Intent(this, BleScanService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onResume() {
        super.onResume();

        //the first thing to check is a license agreement
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


//

        //ask for permissions if needed.
//        if(!hasPermissions(PERMISSIONS)){
//            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
//        }

//        //check permissions and enable what is needed
//        if(!BluetoothAdapter.getDefaultAdapter().isEnabled()){
//            String tmp2 = this.getString(R.string.ble_en);
//            Toast.makeText(this,tmp2,Toast.LENGTH_LONG).show();
//            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
//            return;
//        }
//        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            String tmp3 = this.getString(R.string.co_en);
//            Toast.makeText(this,tmp3,Toast.LENGTH_LONG).show();
//            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//            return;
//        }
//
//        //TODO: this should be called only of location mode is not passive ?
//        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
//        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
//
//        // Check if enabled and if not send user to the GPS settings
//        if (!enabled) {
//            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            startActivity(intent);
//        }


        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                new IntentFilter(BleScanService.ACTION_BROADCAST));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(mServiceConnection);
            mBound = false;
        }

        super.onStop();
    }

    public void onExitClick(View view) {

        Intent intent = new Intent(this, BleScanService.class);
        intent.putExtra(EXTRA_STARTED_FROM_ACTIVITY_OFF, true);
        startService(intent);
        finish();

    }

    public void showLicenseAgreement(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle(R.string.license_title)
                .setMessage(R.string.license_content)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }


    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            long seconds=(new Date().getTime()-start.getTime())/MILS_TO_SEC;
            String x="Time from start: "+Long.toString(seconds)+" seconds.\n";

            String lastScan = intent.getExtras().getString(BleScanService.EXTRA_LOCATION);

            peripheralTextView.setText(x+lastScan);
        }
    }




    //------------- tools -----------------



}

/*

 //scan without GPS feature
    private ScanCallback leScanCallback_noGPS = new ScanCallback() {
        public void onScanResult(int callbackType, ScanResult result) {
            String ad = result.getDevice().getAddress();
            if (!ad.startsWith(pref))//check for desired prefix
                    return;

            if (!devicesDiscovered.containsKey(ad)) {//check if the BLE is in the list
                    devicesDiscovered.put(ad,String.valueOf(result.getRssi()));
            }
        }
    };


    //terminate when checked
    public void terminate() {

        btScanning = false;

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.stopScan(condition);
            }
        });
        this.Exit_app();
    }


    private void DataDelivey(){
        //timestamp
        //peripheralTextView.setText("");//erase screen for next data
        Date cur_date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
        String format = simpleDateFormat.format(cur_date);
        String s =  devicesDiscovered.toString();
        String GPS_parmas="";
        if(this.gps) {
            GPS_parmas = longitude+";"+latitude;
            format = format + ";" + GPS_parmas.replace(".","_");
        }
        //check for network availability
        if (!isNetworkAvailable()){
            //Use sqlite
            databaseHelper.addParticipant(format,name,s);
        }
        else{
            ArrayList<String> people = databaseHelper.getAllParticipants();
            for(String p: people){
                String[] args=p.split(",");
                //send the data to firebase database
                mDatabase.child(args[0]).child(args[1]).setValue(args[2]);
            }
            SQLiteDatabase db =databaseHelper.getWritableDatabase();
            //delete the data
            databaseHelper.onUpgrade(db,1,2);
            //add new data
            mDatabase.child(name).child(format).setValue(s);


            //some printout
            long seconds=(cur_date.getTime()-start.getTime())/MILS_TO_SEC;
            String x="Time from start: "+Long.toString(seconds)+" seconds.\n";
            peripheralTextView.setText(x);
            try {
                x = people.get(-1);
                peripheralTextView.append(x);//print out last row
            }
            catch(Exception e){
                peripheralTextView.append(format+","+s);//print last scan
            }

        }
    }
    //check if internet online
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
    private void Exit_app(){
        //toast for end
        Toast.makeText(getApplicationContext(),
                R.string.end, Toast.LENGTH_LONG).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager2!=null)
                notificationManager2.cancel(NOTIFICATION_ID);
        }else {
            if (notificationManger!=null)
                notificationManger.cancel(NOTIFICATION_ID);
        }
        finish();
        System.exit(0);
    }

        @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

     // Device scan callback.
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            String ad = result.getDevice().getAddress();
            if (!ad.startsWith(pref))//check for desired prefix
                    return;

            if (!devicesDiscovered.containsKey(ad)) {//check if the BLE is in the list
                //add location
                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    devicesDiscovered.put(ad,String.valueOf(result.getRssi())+";null;null");
                }
                else{
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    try {
                        String longitude = String.valueOf(location.getLongitude());
                        String latitude = String.valueOf(location.getLatitude());
                        devicesDiscovered.put(ad,String.valueOf(result.getRssi())+";"+longitude+";"+latitude);
                    }
                    catch(NullPointerException e){
                        devicesDiscovered.put(ad,String.valueOf(result.getRssi())+";null;null");
                    }
                }

            }
        }
    };

    //        Intent intent = new Intent(this, ConfActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 01, intent, PendingIntent.FLAG_ONE_SHOT);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = "try";
//            String description = "notifications";
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
//            channel.setDescription(description);
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                    .setSmallIcon(R.mipmap.bus)
//                    .setContentTitle(getString(R.string.bus))
//                    .setContentText(getString(R.string.expl))
//                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//            notificationManager2 = NotificationManagerCompat.from(this);
//
//            // notificationId is a unique int for each notification that you must define
//            notificationManager2.notify(NOTIFICATION_ID, mBuilder.build());
//        } else {
//            Notification.Builder builder = new Notification.Builder(getApplicationContext());
//            builder.setContentTitle(getString(R.string.bus));
//            builder.setContentText(getString(R.string.expl));
//            builder.setContentIntent(pendingIntent);
//            //builder.setTicker("Fancy Notification");
//            builder.setSmallIcon(R.mipmap.bus);
//            builder.setAutoCancel(true);
//            builder.setPriority(Notification.PRIORITY_DEFAULT);
//            Notification notification = builder.build();
//            notificationManger =
//                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManger.notify(NOTIFICATION_ID, notification);
//        }

 //auth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null){
            mAuth.signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Anon", "signInAnonymously:success");
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("Anon", "signInAnonymously:failure", task.getException());

                            }

                            // ...
                        }
                    });

        }
        currentUser = mAuth.getCurrentUser();


 */
