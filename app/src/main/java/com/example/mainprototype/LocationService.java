package com.example.mainprototype;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import static com.example.mainprototype.Constants.ACTION_START_OR_RESUME_SERVICE;

public class LocationService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            String action = intent.getAction();
            if(action != null){
                Log.e("Action", action);
                if (action.equals(ACTION_START_OR_RESUME_SERVICE)) {
                    Log.e("LOCATIONSERVICE", "Started or resumed service");
                }else if(action.equals(Constants.ACTION_PAUSE_SERVICE)){
                    Log.d("LOCATIONSERVICE", "Paused service");
                }
                else{
                    Log.d("LOCATIONSERVICE", "Stopped service");
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
