package com.example.googletts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ImageButton mImageButtonSpeaking;
    private ImageButton mImageButtonAnalysis;

    public static final int request_code = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageButtonSpeaking = findViewById(R.id.imgbtn_speaking);
        mImageButtonAnalysis = findViewById(R.id.imgbtn_analysis);

        Log.e("hi", "bye");

        if(!checkPermissionFromDevice()) {
            requestPermissionFromDevice();
        }

        mImageButtonSpeaking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SentenceActivity.class);
                startActivity(intent);
            }
        });


        mImageButtonAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                startActivity(intent);
            }
        });

    }

    private boolean checkPermissionFromDevice() {
        int recorder_permssion= ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO);
        return recorder_permssion == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionFromDevice() {
        ActivityCompat.requestPermissions(this,new String[] {
                        Manifest.permission.RECORD_AUDIO},
                request_code);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case request_code:
            {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                    Toast.makeText(getApplicationContext(),"permission granted...",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"permission denied...",Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

}

