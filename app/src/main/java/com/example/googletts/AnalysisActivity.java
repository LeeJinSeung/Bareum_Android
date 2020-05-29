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
import com.example.googletts.Retrofit.TestDTO;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnalysisActivity extends AppCompatActivity {

    private String fileName;
    private ImageButton mImageButtonSpeak;
    private ImageButton mImageButtonSpeak2;
    private TextView mTextSentenceData;
    private TextView mTextStandard;
    private TextView mTextResult;
    private TextView mTextScore;
    private TextView mTextRecoWord;
    private TextView mTextRecoSentence;

    private Button mButtonFinish;
    private MediaPlayer mMediaPlayer;

    private String sentenceData;
    private String resultData;
    private String standard;
    private float score;
    private String recommendWord;
    private ArrayList<Integer> WrongIndex;
    private TestDTO recommendSentence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        Intent intent = getIntent();
        // API 콜을 통해 받은 데이터
        fileName = intent.getExtras().getString("fileName");
        sentenceData = intent.getExtras().getString("sentenceData");
        resultData = intent.getExtras().getString("resultData");
        standard = intent.getExtras().getString("standard");
        score = intent.getExtras().getFloat("score")*100;
        recommendWord = intent.getExtras().getString("recommendWord");
        WrongIndex = intent.getExtras().getIntegerArrayList("WrongIndex");
        recommendSentence = (TestDTO)intent.getSerializableExtra("recommendSentence");

        mImageButtonSpeak = findViewById(R.id.imgbtn_speaker);
        mImageButtonSpeak2 = findViewById(R.id.imgbtn_speaker2);
        mButtonFinish = findViewById(R.id.finish);
        mTextSentenceData = findViewById(R.id.sentenceData);
        mTextStandard = findViewById(R.id.standard);
        mTextResult = findViewById(R.id.result);
        mTextScore = findViewById(R.id.score);
        mTextRecoWord = findViewById(R.id.recommendWord);
        mTextRecoSentence = findViewById(R.id.recommendSentence);

        mTextSentenceData.setText(sentenceData);
        mTextStandard.setText(standard);
        mTextResult.setText(resultData);
        mTextScore.setText(Float.toString(score));
        mTextRecoWord.setText(recommendWord);
        mTextRecoSentence.setText(recommendSentence.getSentence());

        mImageButtonSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: tts 결과 출력
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

        mTextRecoSentence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("recommend Click","Call");
//                Intent intent = new Intent(AnalysisActivity.this, EvaluationActivity.class);
//                intent.putExtra("recommendSentence", recommendSentence);
//                startActivity(intent);
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
