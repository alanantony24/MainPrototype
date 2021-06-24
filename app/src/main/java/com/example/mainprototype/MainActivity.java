package com.example.mainprototype;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
            }
        });
        stopRun.setOnClickListener(new View.OnClickListener() {
            String action = Constants.ACTION_STOP_SERVICE;
            @Override
            public void onClick(View v) {
                sendCommandToService(action);
            }
        });
    }
    private void sendCommandToService(String action){
        Intent intent = new Intent(MainActivity.this, LocationService.class);
        intent.setAction(action);
        MainActivity.this.startService(intent);
    }

}