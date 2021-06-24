package com.example.mainprototype;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startRun = findViewById(R.id.startRun);
        Button stopRun = findViewById(R.id.stopRun);
        startRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommandToService(Constants.ACTION_START_OR_RESUME_SERVICE);
                LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(receiver, new IntentFilter("Time"));
            }
        });
        stopRun.setOnClickListener(new View.OnClickListener() {
            String action = Constants.ACTION_STOP_SERVICE;
            @Override
            public void onClick(View v) {
                sendCommandToService(action);
                LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(receiver);
            }
        });
    }
    private void sendCommandToService(String action){
        Intent intent = new Intent(MainActivity.this, LocationService.class);
        intent.setAction(action);
        MainActivity.this.startService(intent);
    }
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String time = intent.getStringExtra("StopWatch");
            TextView tv = findViewById(R.id.timer);
            tv.setText(time);
        }
    };
}