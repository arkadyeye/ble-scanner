package example.com.bus2.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import static example.com.bus2.service.BleScanService.EXTRA_STARTED_FROM_ON_BOOT;
import static example.com.bus2.service.BleScanService.TAG;

/**
 * Created by Arkady Gorodischer on 17/07/18. Updated on 30/12/19
 */

public class BroadcastReceiverGlobal extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG,"got intent: "+intent.getAction());

        if (intent == null){ return;}

        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {

            Intent startIntent = new Intent(context, BleScanService.class);
            startIntent.putExtra(EXTRA_STARTED_FROM_ON_BOOT,true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.i(TAG,"starting foreground service");
                context.startForegroundService(startIntent);
            } else {
                Log.i(TAG,"starting NON-foreground service");
                context.startService(startIntent);
            }

//            Intent serviceIntent=new Intent(context,BleScanService.class);
//            context.startService(serviceIntent);
        }

    }
}
