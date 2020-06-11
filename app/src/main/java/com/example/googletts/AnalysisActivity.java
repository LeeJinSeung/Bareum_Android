package com.example.googletts;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.googletts.Retrofit.Config;
import com.example.googletts.Retrofit.DTO.AudioConfig;
import com.example.googletts.Retrofit.DTO.SynthesisInput;
import com.example.googletts.Retrofit.DTO.SynthesizeDTO;
import com.example.googletts.Retrofit.DTO.SynthesizeRequestDTO;
import com.example.googletts.Retrofit.DTO.messageDTO;
import com.example.googletts.Retrofit.DTO.VoiceSelectionParams;
import com.example.googletts.Retrofit.NetworkHelper;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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
    private View mLayoutWord;
    private ListView mListWord;
    private ArrayAdapter adapter;

    private Button mButtonFinish;
    private MediaPlayer mMediaPlayer;

    private String sentenceData;
    private String resultData;
    private String standard;
    private String status;
    private float score;
    private ArrayList<String> wordList;
    private ArrayList<Integer> WrongIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        NetworkHelper networkHelper = new NetworkHelper();

        Intent intent = getIntent();
        // API 콜을 통해 받은 데이터
        fileName = intent.getExtras().getString("fileName");
        sentenceData = intent.getExtras().getString("sentenceData");
        resultData = intent.getExtras().getString("resultData");
        standard = intent.getExtras().getString("standard");
        score = intent.getExtras().getFloat("score")*100;
        status = intent.getExtras().getString("status");

        final SpannableStringBuilder sp = new SpannableStringBuilder(resultData);

        ArrayList<String> items = new ArrayList<String>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, items);

        if(status.equals("success")){
            wordList = intent.getExtras().getStringArrayList("wordList");
            WrongIndex = intent.getExtras().getIntegerArrayList("WrongIndex");

            for(int i : WrongIndex){
                sp.setSpan(new ForegroundColorSpan(Color.RED), i, i+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            for(String w : wordList){
                adapter.add(w);
            }
        }

        mImageButtonSpeak = findViewById(R.id.imgbtn_speaker);
        mImageButtonSpeak2 = findViewById(R.id.imgbtn_speaker2);
        mButtonFinish = findViewById(R.id.finish);
        mTextSentenceData = findViewById(R.id.sentenceData);
        mTextStandard = findViewById(R.id.standard);
        mTextResult = findViewById(R.id.result);
        mTextScore = findViewById(R.id.score);
        mLayoutWord = findViewById(R.id.wordLayout);
        mListWord = findViewById(R.id.wordlistView);

        mListWord.setAdapter((adapter));

        mTextSentenceData.setText(sentenceData);
        mTextStandard.setText(standard);
        mTextResult.setText(sp);
        mTextScore.setText(Float.toString(score));

        if(status.equals("perfect"))
            mLayoutWord.setVisibility(View.GONE);

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

        mButtonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 녹음파일 삭제

                // 홈 화면으로 이동
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        mListWord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(AnalysisActivity.this);
                alert_confirm.setMessage("단어장에 추가하겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        })
                        .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 단어장에 단어 추가
                                RequestBody wordData = RequestBody.create(MediaType.parse("text/plain"), parent.getItemAtPosition(position).toString());

                                Call call = networkHelper.getApiService().insertWordBook(wordData);

                                call.enqueue(new Callback<messageDTO>() {
                                    @Override
                                    public void onResponse(Call<messageDTO> call, Response<messageDTO> response) {
                                        if (response.isSuccessful()) {
                                            messageDTO body = response.body();
                                            if (body != null) {
                                                String responseMessage = body.getMessage();

                                                if(responseMessage.equals("insWordBookControl Success"))
                                                    Toast.makeText(getApplicationContext(),"추가되었습니다.",Toast.LENGTH_SHORT).show();
                                                else if(responseMessage.equals("duplicate word"))
                                                    Toast.makeText(getApplicationContext(),"이미 단어장에 존재합니다.",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<messageDTO> call, Throwable t) {
                                        System.out.println("onFailure" + call);
                                        Log.e("Request", "Failure");
                                    }
                                });
                            }
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();
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