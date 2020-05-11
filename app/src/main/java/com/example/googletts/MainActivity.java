package com.example.googletts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    private ImageButton mImageButtonSpeaking;
    private ImageButton mImageButtonAnalysis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageButtonSpeaking = findViewById(R.id.imgbtn_speaking);
        mImageButtonAnalysis = findViewById(R.id.imgbtn_analysis);

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

}

