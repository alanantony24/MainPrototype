package com.example.mainprototype;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import java.util.List;

import static com.example.mainprototype.Constants.ACTION_START_OR_RESUME_SERVICE;
import static com.example.mainprototype.Constants.NOTIFICATION_CHANNEL_ID;
import static com.example.mainprototype.Constants.NOTIFICATION_CHANNEL_NAME;
import static com.example.mainprototype.Constants.NOTIFICATION_ID;

public class LocationService extends Service {

    boolean isFirstRun = true;
    FusedLocationProviderClient fusedLocationProviderClient;

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

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationProviderClient = new FusedLocationProviderClient(this);
        updateLocationTracking();
    }

    @SuppressLint("MissingPermission")
    private void updateLocationTracking(){
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }
    Location location;
    LocationCallback locationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            List<Location> locationList = locationResult.getLocations();
            for (int i = 0; i < locationList.size(); i++) {
                location = locationList.get(i);
                Log.e("Location", location.getLatitude() + " , " + location.getLongitude());
                NotificationCompat.Builder builder = new NotificationCompat.Builder(LocationService.this, NOTIFICATION_CHANNEL_ID)
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setSmallIcon(R.drawable.common_google_signin_btn_icon_disabled)
                        .setContentTitle("Live coordinates of current location")
                        .setContentText(location.getLatitude() + ", " + location.getLongitude())
                        .setContentIntent(getMainActivityPendingIntent());
                startForeground(NOTIFICATION_ID, builder.build());
            }
        }
    };

    private void startForegroundService(){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager);
        }
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
