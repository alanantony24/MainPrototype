package com.example.mainprototype;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import static com.example.mainprototype.Constants.ACTION_START_OR_RESUME_SERVICE;
import static com.example.mainprototype.Constants.NOTIFICATION_CHANNEL_ID;
import static com.example.mainprototype.Constants.NOTIFICATION_CHANNEL_NAME;
import static com.example.mainprototype.Constants.NOTIFICATION_ID;

public class LocationService extends Service {

    boolean isFirstRun = true;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            String action = intent.getAction();
            if(action != null){
                Log.e("Action", action);
                if (action.equals(ACTION_START_OR_RESUME_SERVICE)) {
                    if(isFirstRun){
                        startForegroundService();
                        isFirstRun = false;
                        Log.e("LOCATIONSERVICE", "Started or resumed service....");
                    }
                    else{
                        Log.d("Resume", "Resuming");
                    }
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

    private void startForegroundService(){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(LocationService.this, NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_disabled)
                .setContentTitle("Running App")
                .setContentText("00:00:00")
                .setContentIntent(getMainActivityPendingIntent());
        startForeground(NOTIFICATION_ID, builder.build());
    }

    private PendingIntent getMainActivityPendingIntent(){
        Intent intent = new Intent(LocationService.this, MainActivity.class);
        intent.setAction(Constants.ACTION_SHOW_TRACKING_FRAGMENT);
        PendingIntent pendingIntent = PendingIntent.getActivity(LocationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(NotificationManager notificationManager){
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
        notificationManager.createNotificationChannel(notificationChannel);
        Log.e("Notification", "Nope");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
