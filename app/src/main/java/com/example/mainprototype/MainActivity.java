package com.example.mainprototype;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager = null;
    private boolean running = false;
    private int totalSteps = 0;
    private int previousTotalSteps = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Button startRun = findViewById(R.id.startRun);
        Button stopRun = findViewById(R.id.stopRun);

        loadData();
        resetSteps();
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

    @Override
    protected void onResume() {
        super.onResume();
        running = true;
        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepSensor == null) {
            // This will give a toast message to the user if there is no sensor in the device
            Toast.makeText(this, "No sensor detected on this device", Toast.LENGTH_SHORT).show();
        } else {
            // Rate suitable for the user interface
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        TextView tv_stepsTaken = findViewById(R.id.tv_stepCounter);
        if(running){
            totalSteps = (int) event.values[0];

            int currentSteps = totalSteps - previousTotalSteps;
            tv_stepsTaken.setText(""+currentSteps);
        }
    }
    public void resetSteps(){
        TextView tv_stepsTaken = findViewById(R.id.tv_stepCounter);
        tv_stepsTaken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Long tap to reset", Toast.LENGTH_SHORT).show();
            }
        });
        tv_stepsTaken.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                previousTotalSteps = totalSteps;
                tv_stepsTaken.setText(""+0);
                saveData();
                return true;
            }
        });
    }
    public void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences("steps", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("key1", previousTotalSteps);
        editor.apply();
    }
    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("steps", Context.MODE_PRIVATE);
        int savedData = sharedPreferences.getInt("key1", 0);
        previousTotalSteps = savedData;
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}