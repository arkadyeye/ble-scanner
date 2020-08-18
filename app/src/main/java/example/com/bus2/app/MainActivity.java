package example.com.bus2.app;

import android.Manifest;
import android.app.AlertDialog;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.Date;

import example.com.bus2.R;
import example.com.bus2.service.BleScanService;
import example.com.bus2.service.BleTag;
import example.com.bus2.service.SettingsManager;
import example.com.bus2.service.TagsContainer;

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

    private static final float BT_MARKER_SIZE = 8.0f;

    Date start = new Date();

    private int titleClickCounter = 0;

    //---------------------
    private SharedPreferences sharedPreferences;

    // The BroadcastReceiver used to listen to broadcasts from the service.
    private MyReceiver myReceiver;

    private BleScanService mService = null;

    // Tracks the bound state of the service.
    private boolean mBound = false;

    //map holder
    private MapView map = null;
    private Marker meMarker = null;
    private FusedLocationProviderClient fusedLocationClient;

    private TagsContainer tags;
    private Marker[] tagsMarkers;


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

        //load tags with their location
        //note: ugly style. the same is loaded in the service too
        SettingsManager settings = new SettingsManager(this);
        settings.init();
        tags = settings.getBles();

        setContentView(R.layout.activity_main);

        //add title bar
        LayoutInflater inflater = LayoutInflater.from(this);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.custom_title, null, false);

        FrameLayout.LayoutParams layoutParams = null;
        layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.TOP;

        addContentView(layout, layoutParams);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        map.setMultiTouchControls(true);

        //center the map on predefined point. probably it should be my last known location ?
        //ewen better, we should add floating button, that will move me to my location
        IMapController mapController = map.getController();
        mapController.setZoom(18.0);
        GeoPoint startPoint = new GeoPoint(32.067916, 34.843414);//bar ilan 32.067916, 34.843414
        mapController.setCenter(startPoint);
        meMarker = new Marker(map);
        meMarker.setPosition(startPoint);
        meMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(meMarker);

        //add an array of markers coresponding to tags
        GeoPoint btPoint;
        BleTag tag;

        tagsMarkers = new Marker[tags.size()];
        for (int i=0;i<tagsMarkers.length;i++){
            tagsMarkers[i] = new Marker(map);
        }

        for (int i = 0; i < tags.size(); i++) {
            tag = tags.get(i);

//            Log.i("ark", i + " ) tag added : " + tag.lat + "," + tag.lon + "," + tag.alt);

            btPoint = new GeoPoint(tag.lat, tag.lon, tag.alt);

            tagsMarkers[i].setPosition(btPoint);
            tagsMarkers[i].setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            tagsMarkers[i].setTitle(tag.name);

            Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.bt_icon, null);
            Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
            Drawable dr = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, (int) (BT_MARKER_SIZE * getResources().getDisplayMetrics().density), (int) (BT_MARKER_SIZE * getResources().getDisplayMetrics().density), true));
            tagsMarkers[i].setIcon(dr);
            tagsMarkers[i].setAlpha(0.2f);//0..1f

            map.getOverlays().add(tagsMarkers[i]);
        }

        map.invalidate();


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


//        peripheralTextView = (TextView) findViewById(R.id.PeripheralTextView);
//        peripheralTextView.setMovementMethod(new ScrollingMovementMethod());

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

                if (titleClickCounter > 4) {
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
        map.onResume();


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
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        map.onPause();

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

    public void onLocateClick(View view) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            IMapController mapController = map.getController();
                            mapController.setZoom(18.0);
                            GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                            mapController.setCenter(startPoint);

                            map.getOverlays().remove(meMarker);

                            meMarker = new Marker(map);
                            meMarker.setPosition(startPoint);
                            meMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                            map.getOverlays().add(meMarker);
                        }
                    }
                });
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

            String lastScanStr = intent.getExtras().getString(BleScanService.EXTRA_LOCATION);

            Log.i("ark","lastScan: "+lastScanStr);

            hideAllMarkers();

            try {
                JSONObject lastScan = new JSONObject(lastScanStr);
                JSONArray devices = lastScan.getJSONArray("bt");
                for (int i=0;i<devices.length();i++){
                    JSONObject dev = devices.getJSONObject(i);
                    String mac = dev.getString("mac");
                    int rssi = dev.getInt("rssi");

                    int index = tags.getBleTagIndex(mac);
                    if (index != -1){
                        tagsMarkers[index].setAlpha(1);
                    }

                }

                map.invalidate();




            } catch (JSONException e) {
                e.printStackTrace();
            }

            //TODO: reformat the json, so it will be easier to read
//            peripheralTextView.setText(x+lastScan);
        }
    }




    //------------- tools -----------------
    private void hideAllMarkers(){
        for (int i=0;i<tagsMarkers.length;i++){
            tagsMarkers[i].setAlpha(0.2f);
        }

    }



}
