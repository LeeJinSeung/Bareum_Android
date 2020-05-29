package com.example.googletts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.googletts.Retrofit.Config;
import com.example.googletts.Retrofit.DTO.AudioConfig;
import com.example.googletts.Retrofit.DTO.SynthesisInput;
import com.example.googletts.Retrofit.DTO.SynthesizeDTO;
import com.example.googletts.Retrofit.DTO.SynthesizeRequestDTO;
import com.example.googletts.Retrofit.DTO.TestDTO;
import com.example.googletts.Retrofit.DTO.VoiceSelectionParams;
import com.example.googletts.Retrofit.NetworkHelper;

import java.io.IOException;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EvaluationActivity extends AppCompatActivity {

    private TextView mText;
    private TextView mTextSentence;
    private ImageButton mImageButtonSpeak;
    private ImageButton mImageButtonMic;
    private Button mButtonNext;
    private MediaPlayer mMediaPlayer;

    private MediaRecorder mRecorder;
    private String FileName = "";

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
        // sentenceId = intent.getExtras().getInt("sentenceId");
        // sentence = intent.getExtras().getString("sentence");
        // standard = intent.getExtras().getString("standard");
        sentence = (TestDTO) intent.getSerializableExtra("sentence");

        mText = findViewById(R.id.text);
        mTextSentence = findViewById(R.id.sentence);
        mImageButtonSpeak = findViewById(R.id.imgbtn_speaker);
        mImageButtonMic = findViewById(R.id.imgbtn_mic);
        mButtonNext = findViewById(R.id.btn_next);

        mText.setText(sentence.getSentence());
        mTextSentence.setText(sentence.getStandard());

        mButtonNext.setEnabled(false);

        mImageButtonSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: tts 결과 출력
                NetworkHelper networkHelper = new NetworkHelper();
                SynthesisInput input = new SynthesisInput();
                VoiceSelectionParams voice = new VoiceSelectionParams();
                AudioConfig audio = new AudioConfig();


                input.setText(mTextSentence.getText().toString());


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
                mRecorder.stop();
                mImageButtonMic.setEnabled(true);
                mButtonNext.setEnabled(false);
                Toast.makeText(getApplicationContext(),"녹음 완료",Toast.LENGTH_SHORT).show();

                //TODO: STT API 추가

                Intent intent = new Intent(EvaluationActivity.this, AnalysisActivity.class);
                intent.putExtra("fileName", FileName);
                startActivity(intent);
            }
        });
    }


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
        mRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(FileName);
    }


}
