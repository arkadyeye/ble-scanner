package example.com.bus2.app;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

import example.com.bus2.R;
import example.com.bus2.service.ErrorReporter;

import static example.com.bus2.service.BleScanService.TAG;

public class ConfActivity extends AppCompatActivity {

    private final int PERMISSION_ALL = 1;

    private String[] PERMISSIONS = {
            Manifest.permission.INTERNET,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };



    CheckBox research;
    boolean exist=false;
    Button con;
    //EditText pref;
    String mac="";
    Button link;
    private final static int REQUEST_ENABLE_BT = 1;
//    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
//    private DatabaseReference child = mDatabase.child("participants");

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(example.com.bus2.R.layout.activity_conf);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean researchParticipant = sharedPreferences.getBoolean("research_participant",false);
        boolean dataSharing = sharedPreferences.getBoolean("data_sharing",false);



        if (researchParticipant == false){
            showLicenseAgreement();
            return;
        }

        if (dataSharing == false){
            showDataSharingAgreement();
            return;
        }

        boolean isErrorReport = ErrorReporter.getInstance().CheckErrorAndSendMail(this);
        if (isErrorReport){
            return;
        }

        moveOn();

    }



    private void moveOn(){

        if(!hasPermissions(PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            return;
        }

        if(!BluetoothAdapter.getDefaultAdapter().isEnabled()){
            String tmp2 = this.getString(R.string.ble_en);
            Toast.makeText(this,tmp2,Toast.LENGTH_LONG).show();
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            return;
        }
//        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            String tmp3 = this.getString(R.string.co_en);
//            Toast.makeText(this,tmp3,Toast.LENGTH_LONG).show();
//            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//            return;
//        }

        //TODO: this should be called only of location mode is not passive ?
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // Check if enabled and if not send user to the GPS settings
        if (!enabled) {
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 2);
            return;
        }

        //check for old crashesh ?


        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);



        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        moveOn();
    }

//    private void Survey() {
//        mDatabase.child("participants").child(mac).setValue(0);
//       // String s="http://blept.limequery.org/715559?APPID="+mac+"&amp;newtest=Y&amp;lang=he";
//        String s="https://biumngmnt.limequery.org/715559? APPID="+mac+"&newtest=Y&lang=he";
//        Uri uri = Uri.parse(s); // missing 'http://' will cause crashed
//        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//        startActivity(intent);
//    }



    public void showLicenseAgreement() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle(R.string.license_title)
                .setMessage(R.string.license_content)
                .setPositiveButton(R.string.license_accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        sharedPreferences.edit().putBoolean("research_participant", true).apply();
                        //moveOn();
                        showDataSharingAgreement();
                    }
                })
                .setNegativeButton(R.string.license_reject, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //do nothing and exit
                        finish();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public void showDataSharingAgreement() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle(R.string.Sharing_title)
                .setMessage(R.string.Sharing_content)
                .setPositiveButton(R.string.license_accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        sharedPreferences.edit().putBoolean("data_sharing", true).apply();
                        moveOn();
                    }
                })
                .setNegativeButton(R.string.license_reject, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //do nothing and exit
                        finish();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }


    //------------- permissions ---------

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch(requestCode) {
            case PERMISSION_ALL: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    moveOn();
                } else {
                    finish();
                }
            }
        }
    }

    public boolean hasPermissions( String... permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

}
