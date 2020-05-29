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
import com.example.googletts.Retrofit.DTO.VoiceSelectionParams;
import com.example.googletts.Retrofit.NetworkHelper;
import com.example.googletts.Retrofit.TestDTO;
import com.example.googletts.Retrofit.analysisDTO;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import okhttp3.OkHttpClient;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        mText = findViewById(R.id.sentenceData);
        mTextSentenceData = findViewById(R.id.sentenceData);
        mTextStandard = findViewById(R.id.standard);
        mImageButtonSpeak = findViewById(R.id.imgbtn_speaker);
        mImageButtonMic = findViewById(R.id.imgbtn_mic);
        mButtonNext = findViewById(R.id.finish);

        mButtonNext.setEnabled(false);

        Intent intent = getIntent();

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
                    mRecorder.stop();
                    mImageButtonMic.setEnabled(true);
                    mButtonNext.setEnabled(false);
//                    Toast.makeText(getApplicationContext(),"녹음 완료",Toast.LENGTH_SHORT).show();

                    //STT API로 나온 음성 결과 서버로 전송
                    // TODO STT api 신뢰도 테스트 필요함(=말하는 그대로 나오는가??)
                    recognitionSpeech(FileName);

                    int sentenceId = 1;
                    String resultData = "날시가 참 막따";

                    OkHttpClient.Builder builder = new OkHttpClient.Builder();
                    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                    builder.addInterceptor(interceptor);

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://10.0.2.2:5000")
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(builder.build())
                            .build();
                    ApiService apiService = retrofit.create(ApiService.class);
                    HashMap<String, Object> input = new HashMap<>();
                    // TODO 전 액티비티에서 받은 sentenceId
                    input.put("sentenceId", sentenceId);
                    // TODO stt api를 돌려서 나온 문장
                    input.put("sentenceData", resultData);
                    apiService.requestResult(input).enqueue(new Callback<analysisDTO>() {
                        @Override
                        public void onResponse(Call<analysisDTO> call, Response<analysisDTO> response) {
                            if (response.isSuccessful()) {
                                analysisDTO body = response.body();
                                if (body != null) {
                                    Log.d("data", body + "");
                                    Log.d("data.getScore()", body.getScore() + "");
                                    Log.d("data.getRecommendWord()", body.getRecommendWord() + "");
                                    Log.d("data.getWrongIndex()", body.getWrongIndex() + "");
                                    Log.d("data.getRecoSentence()", body.getRecommendSentence() + "");
                                    Log.e("postData end", "======================================");

                                    String response_status = body.getStatus();

                                    Log.d("response status", response_status);

                                    if(response_status.equals("success")){
                                        Intent intent = new Intent(EvaluationActivity.this, AnalysisActivity.class);
                                        intent.putExtra("fileName", FileName).putExtra("resultData", resultData)
                                                .putExtra("sentenceData", sentenceData).putExtra("standard", standard)
                                                .putExtra("score", body.getScore()).putExtra("recommendSentence", body.getRecommendSentence())
                                                .putExtra("recommendWord", body.getRecommendWord())
                                                .putIntegerArrayListExtra("WrongIndex", (ArrayList<Integer>)body.getWrongIndex());
                                        startActivity(intent);
                                    }else if(response_status.equals("perfect")){

                                    }else if(response_status.equals("failure")) {
                                        Toast.makeText(getApplicationContext(),"다시 시도해 주세요.",Toast.LENGTH_SHORT).show();
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

    private boolean checkPermissionFromDevice() {
        int recorder_permssion=ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO);
        return recorder_permssion == PackageManager.PERMISSION_GRANTED;
    }

    // 1분 미만의 오디오 파일일때
    public static void recognitionSpeech(String filePath) {
        try {
            SpeechClient speech = SpeechClient.create(); // Client 생성

            // 오디오 파일에 대한 설정부분
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(16000)
                    .setLanguageCode("ko-KR")
                    .build();

            RecognitionAudio audio = getRecognitionAudio(filePath); // Audio 파일에 대한 RecognitionAudio 인스턴스 생성
            RecognizeResponse response = speech.recognize(config, audio); // 요청에 대한 응답
            List<SpeechRecognitionResult> results = response.getResultsList(); // 응답 결과들

            for (SpeechRecognitionResult result: results) {
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                System.out.printf("Transcription: %s%n", alternative.getTranscript());
            }

            speech.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Local 이나 Remote이거나 구분해서 RecognitionAudio 만들어 주는 부분
    public static RecognitionAudio getRecognitionAudio(String filePath) throws IOException {
        RecognitionAudio recognitionAudio;

        // 파일이 GCS에 있는 경우
        if (filePath.startsWith("gs://")) {
            recognitionAudio = RecognitionAudio.newBuilder()
                    .setUri(filePath)
                    .build();
        }
        else { // 파일이 로컬에 있는 경우
            Path path = Paths.get(filePath);
            byte[] data = Files.readAllBytes(path);
            ByteString audioBytes = ByteString.copyFrom(data);

            recognitionAudio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();
        }

        return recognitionAudio;
    }
}
