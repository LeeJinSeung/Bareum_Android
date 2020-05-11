package com.example.googletts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
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
import com.example.googletts.Retrofit.DTO.VoiceSelectionParams;
import com.example.googletts.Retrofit.NetworkHelper;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnalysisActivity extends AppCompatActivity {

    private String fileName;
    private ImageButton mImageButtonSpeak;
    private ImageButton mImageButtonSpeak2;
    private TextView mTextSentence;
    private Button mButtonFinish;
    private MediaPlayer mMediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        Intent intent = getIntent();
        fileName = intent.getExtras().getString("fileName");

        mImageButtonSpeak = findViewById(R.id.imgbtn_speaker);
        mImageButtonSpeak2 = findViewById(R.id.imgbtn_speaker2);
        mButtonFinish = findViewById(R.id.finish);
        mTextSentence = findViewById(R.id.sentence);


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

        mImageButtonSpeak2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer=new MediaPlayer();
                try {
                    mMediaPlayer.setDataSource(fileName);
                    mMediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mMediaPlayer.start();
                Toast.makeText(getApplicationContext(),"playing..",Toast.LENGTH_SHORT).show();
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
}
