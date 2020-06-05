package com.example.googletts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.googletts.Retrofit.ApiService;
import com.example.googletts.Retrofit.Config;
import com.example.googletts.Retrofit.DTO.AudioConfig;
import com.example.googletts.Retrofit.DTO.SynthesisInput;
import com.example.googletts.Retrofit.DTO.SynthesizeDTO;
import com.example.googletts.Retrofit.DTO.SynthesizeRequestDTO;
import com.example.googletts.Retrofit.DTO.TestDTO;
import com.example.googletts.Retrofit.DTO.VoiceSelectionParams;
import com.example.googletts.Retrofit.NetworkHelper;
import com.example.googletts.Retrofit.DTO.analysisDTO;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EvaluationActivity extends AppCompatActivity {

    private TextView mText;
    private TextView mTextSentenceData;
    private TextView mTextStandard;
    private ImageButton mImageButtonSpeak;
    private ImageButton mImageButtonMic;
    private Button mButtonNext;
    private MediaPlayer mMediaPlayer;

    private MediaRecorder mRecorder;
    private String FileName = "";
    public static final int request_code = 1000;

    //TODO: sentence, standard로 변경
    // private String sentence;
    // private String standard;
    // private int sentenceId;
    private TestDTO sentence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        Intent intent = getIntent();

        sentence = (TestDTO) intent.getSerializableExtra("sentence");

        Log.e("Get Sentence",sentence.getStandard()+"");

        mText = findViewById(R.id.text);
        mTextStandard = findViewById(R.id.standard);
        mTextSentenceData = findViewById(R.id.sentenceData);
        mImageButtonSpeak = findViewById(R.id.imgbtn_speaker);
        mImageButtonMic = findViewById(R.id.imgbtn_mic);
        mButtonNext = findViewById(R.id.finish);

        mTextSentenceData.setText(sentence.getSentence());
        mTextStandard.setText(sentence.getStandard());

        mButtonNext.setEnabled(false);

        TestDTO receiveSentence = (TestDTO)intent.getSerializableExtra("sentence");

        // TODO 전 액티비티에서 받아오기 + sentenceId
        String sentenceData = receiveSentence.getSentence();
        String standard = receiveSentence.getStandard();

        mTextSentenceData.setText(sentenceData);
        mTextStandard.setText(standard);

        mImageButtonSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // tts 결과 출력
                NetworkHelper networkHelper = new NetworkHelper();
                SynthesisInput input = new SynthesisInput();
                VoiceSelectionParams voice = new VoiceSelectionParams();
                AudioConfig audio = new AudioConfig();

                input.setText(mTextSentenceData.getText().toString());

                Call<SynthesizeDTO> call = networkHelper.getApiService().TTS(Config.API_KEY, new SynthesizeRequestDTO(input, voice, audio));
                call.enqueue(new Callback<SynthesizeDTO>() {
                    @Override
                    public void onResponse(Call<SynthesizeDTO> call, Response<SynthesizeDTO> response) {
                        Log.e("TTS : ", "success " + response.isSuccessful());
                        Log.e("TTS code", Integer.toString(response.code()));
                        if(!response.isSuccessful()) {
                            try {
                                Log.e("TTS Message", response.errorBody().string());
                            }
                            catch (IOException e) {
                                Log.e("TTS IOException", "fuck");
                            }
                            return;
                        }
                        SynthesizeDTO synthesizeDTO = response.body();
                        Log.e("TTS Success: ", synthesizeDTO.getAudioContent());
                        playAudio(synthesizeDTO.getAudioContent());
                    }

                    @Override
                    public void onFailure(Call<SynthesizeDTO> call, Throwable t) {
                        Log.e("TTS : ", "fail");

                    }
                });

            }
        });

        //----------------------------

        if(checkPermissionFromDevice()) {
            mImageButtonMic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: 음성 파일 만들기

                    FileName= getApplicationContext().getCacheDir().getAbsolutePath()+"/"+
                            UUID.randomUUID()+"AudioFile.wav";

                    SetupMediaRecorder();

                    try {
                        mRecorder.prepare();
                        mRecorder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mImageButtonMic.setEnabled(false);
                    mButtonNext.setEnabled(true);

                    Toast.makeText(getApplicationContext(),"녹음 시작",Toast.LENGTH_SHORT).show();
                }
            });
            mButtonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NetworkHelper networkHelper = new NetworkHelper();
                    int sentenceId = sentence.getSid();

                    mRecorder.stop();
                    mImageButtonMic.setEnabled(true);
                    mButtonNext.setEnabled(false);

                    Log.e("SoundFile path", FileName);

                    OkHttpClient.Builder builder = new OkHttpClient.Builder();
                    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                    builder.addInterceptor(interceptor);

                    File file = new File(FileName);
                    Log.e("FileName", file.getName());
                    RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
                    MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("receiveFile", file.getName(), requestBody);
                    RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
                    RequestBody sid = RequestBody.create(MediaType.parse("text/plain"), Integer.toString(sentenceId));

                    Call call = networkHelper.getApiService().requestResult(fileToUpload, filename, sid);

                    call.enqueue(new Callback<analysisDTO>() {
                        @Override
                        public void onResponse(Call<analysisDTO> call, Response<analysisDTO> response) {
                            if (response.isSuccessful()) {
                                analysisDTO body = response.body();
                                if (body != null) {
                                    Log.d("data", body + "");

                                    String response_status = body.getStatus();

                                    Log.d("response status", response_status);

                                    if(response_status.equals("failure")) {
                                        Toast.makeText(getApplicationContext(),"다시 시도해 주세요.",Toast.LENGTH_SHORT).show();
                                    }else if(response_status.equals("perfect")){
                                        Intent intent = new Intent(EvaluationActivity.this, AnalysisActivity.class);
                                        intent.putExtra("fileName", FileName).putExtra("resultData", body.getResultData())
                                                .putExtra("score", body.getScore()).putExtra("status", body.getStatus())
                                                .putExtra("sentenceData", sentenceData).putExtra("standard", standard);
                                        startActivity(intent);
                                    }else{
                                        Intent intent = new Intent(EvaluationActivity.this, AnalysisActivity.class);
                                        intent.putExtra("fileName", FileName).putExtra("resultData", body.getResultData())
                                                .putExtra("sentenceData", sentenceData).putExtra("standard", standard)
                                                .putExtra("score", body.getScore()).putExtra("status", body.getStatus())
                                                .putStringArrayListExtra("wordList", (ArrayList<String>)body.getWordList())
                                                .putIntegerArrayListExtra("WrongIndex", (ArrayList<Integer>)body.getWrongIndex());
                                        startActivity(intent);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<analysisDTO> call, Throwable t) {
                            System.out.println("onFailure"+call);
                            Log.e("Request", "Failure");
                        }
                    });
                }
            });
        }
        else {
            requestPermissionFromDevice();
        }
    }

    // -------------------------

    private void playAudio(String base64EncodedString) {
        try {
            stopAudio();

            String url = "data:audio/mp3;base64," + base64EncodedString;
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException IoEx) {
        }
    }

    public void stopAudio() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        }
    }

    private void SetupMediaRecorder() {
        mRecorder=new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mRecorder.setOutputFile(FileName);
    }

    private boolean checkPermissionFromDevice() {
        int recorder_permssion=ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO);
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
