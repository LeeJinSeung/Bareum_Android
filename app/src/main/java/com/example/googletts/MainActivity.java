package com.example.googletts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.googletts.Retrofit.DTO.ResultDTO;
import com.example.googletts.Retrofit.DTO.TestDTO;
import com.example.googletts.Retrofit.NetworkHelper;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Result;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ImageButton mImageButtonSpeaking;
    private ImageButton mImageButtonAnalysis;
    private ResultDTO result;
    private List<TestDTO> sentence;

    public static final int request_code = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageButtonSpeaking = findViewById(R.id.imgbtn_speaking);
        mImageButtonAnalysis = findViewById(R.id.imgbtn_analysis);
        result = new ResultDTO();
        sentence = new ArrayList<>();

        Log.e("hi", "bye");

        if(!checkPermissionFromDevice()) {
            requestPermissionFromDevice();
        }


        mImageButtonSpeaking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: sentence 넘기기
                NetworkHelper networkHelper = new NetworkHelper();
                Call<List<TestDTO>> call = networkHelper.getApiService().requestSentence();
                Log.e("Request : ", "sentence hihihihihii ");
                call.enqueue(new Callback<List<TestDTO>>() {
                    @Override
                    public void onResponse(Call<List<TestDTO>> call, Response<List<TestDTO>> response) {
                        Log.e("Request : ", "success " + response.isSuccessful());
                        Log.e("Request code", Integer.toString(response.code()));
                        if (!response.isSuccessful()) {
                            try {
                                Log.e("Request Message", response.errorBody().string());
                            } catch (IOException e) {
                                Log.e("Request IOException", "fuck");
                            }
                            return;
                        }
                        sentence = response.body();
                        Log.e("Request Success: ", sentence.toString());

                        Intent intent = new Intent(MainActivity.this, SentenceActivity.class);
                        intent.putExtra("sentence", (Serializable) sentence);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<List<TestDTO>> call, Throwable t) {
                        Log.e("Request : ", "fail " + t.getCause());
                    }
                });

            }
        });


        mImageButtonAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: result 넘기기
                NetworkHelper networkHelper = new NetworkHelper();
                Call<ResultDTO> call = networkHelper.getApiService().requestTotal();
                call.enqueue(new Callback<ResultDTO>() {
                    @Override
                    public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                        Log.e("Request : ", "success " + response.isSuccessful());
                        Log.e("Request code", Integer.toString(response.code()));
                        if (!response.isSuccessful()) {
                            try {
                                Log.e("Request Message", response.errorBody().string());
                            } catch (IOException e) {
                                Log.e("Request IOException", "fuck");
                            }
                            return;
                        }
                        result = response.body();

                        Log.e("Request phoneme: ", result.getMostPhoneme().toString());
                        Log.e("Request score: ", result.getScore().toString());

                         Intent intent = new Intent(MainActivity.this, UserSentenceActivity.class);
//                         intent.putExtra("result", result);
                         startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<ResultDTO> call, Throwable t) {
                        Log.e("Request : ", "fail " + t.getCause());
                    }
                });
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

