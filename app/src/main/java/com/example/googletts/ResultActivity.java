package com.example.googletts;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.widget.Button;

public class ResultActivity extends AppCompatActivity {

    private Button mButtonPlay;
    private Button mButtonRecord;
    private Button mButtonStop;
    private MediaRecorder mRecorder;
    private MediaPlayer mediaPlayer;
    private String FileName = "";

    public static final int request_code = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

    }
}
