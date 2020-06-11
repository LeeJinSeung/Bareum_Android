package com.example.googletts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
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
import com.example.googletts.Retrofit.DTO.analysisDTO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EvaluationActivity extends AppCompatActivity {

    private TextView mText;
    private TextView mTextSentenceData;
    private TextView mTextStandard;
    private ImageButton mImageButtonSpeak;
    private ImageButton mImageButtonMic;
    private Button mButtonNext;
    private MediaPlayer mMediaPlayer;

    private static final int RECORDER_BPP = 16;
    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
    private static final int RECORDER_SAMPLERATE = 44100;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    short[] audioData;

    private AudioRecord recorder = null;
    private int bufferSize = 0;
    private Thread recordingThread = null;
    private boolean isRecording = false;

    private String FileName = "";
    private String RandomId;
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

                    RandomId = UUID.randomUUID().toString();
                    FileName = getApplicationContext().getCacheDir().getAbsolutePath() + "/" + RandomId +
                            AUDIO_RECORDER_FOLDER + AUDIO_RECORDER_FILE_EXT_WAV;

                    bufferSize = AudioRecord.getMinBufferSize
                            (RECORDER_SAMPLERATE,RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING)*3;
                    audioData = new short [bufferSize];
                    startRecording();

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

                    stopRecording();

                    mImageButtonMic.setEnabled(true);
                    mButtonNext.setEnabled(false);

                    Log.e("SoundFile path", FileName);

                    OkHttpClient.Builder builder = new OkHttpClient.Builder();
                    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                    builder.addInterceptor(interceptor);

                    File file = new File(FileName);
                    Log.e("FileName", file.getName());
                    Log.e("FileExist", String.valueOf(file.exists()));

                    RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
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

                                    Log.e("response status", response_status);
                                    Log.e("resultData", body.getResultData());

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

    private String getFilename(){
        String filepath = getApplicationContext().getCacheDir().getAbsolutePath();

        try {
            File soundFile = File.createTempFile(RandomId + AUDIO_RECORDER_FOLDER, AUDIO_RECORDER_FILE_EXT_WAV, new File(filepath));

            Log.e("temp_file_exist", String.valueOf(soundFile.exists()));

        }catch (IOException e){
            e.printStackTrace();
        }

        return (filepath + "/" + RandomId + AUDIO_RECORDER_FOLDER + AUDIO_RECORDER_FILE_EXT_WAV);
    }

    private String getTempFilename() {
        String filepath = getApplicationContext().getCacheDir().getAbsolutePath();

        try {
            File tempFile = File.createTempFile(RandomId + AUDIO_RECORDER_TEMP_FILE, ".raw", new File(filepath));

            Log.e("temp_file_exist", String.valueOf(tempFile.exists()));

        }catch (IOException e){
            e.printStackTrace();
        }

        return (filepath + "/" + RandomId + AUDIO_RECORDER_TEMP_FILE + ".raw");
    }

    private void startRecording() {
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE,
                RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING,
                bufferSize);
        int i = recorder.getState();
        if (i==1)
            recorder.startRecording();

        isRecording = true;

        recordingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");

        recordingThread.start();
    }

    private void writeAudioDataToFile() {
        byte data[] = new byte[bufferSize];
        String filename = getTempFilename();
        FileOutputStream os = null;

        File f = new File(Environment.getExternalStorageDirectory().getPath());

        try {
            os = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int read = 0;
        if (null != os) {
            while(isRecording) {
                read = recorder.read(data, 0, bufferSize);
                if (read > 0){
                }

                if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                    try {
                        os.write(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopRecording() {
        if (null != recorder){
            isRecording = false;

            int i = recorder.getState();
            if (i==1)
                recorder.stop();
            recorder.release();

            recorder = null;
            recordingThread = null;
        }

        copyWaveFile(getTempFilename(),getFilename());
        deleteTempFile();
    }

    private void deleteTempFile() {
        File file = new File(getTempFilename());
        file.delete();
    }

    private void copyWaveFile(String inFilename,String outFilename){
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = RECORDER_SAMPLERATE;
        int channels = 2;
        long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels/8;

        byte[] data = new byte[bufferSize];

        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;

            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);

            while(in.read(data) != -1) {
                out.write(data);
            }

            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void WriteWaveFileHeader(
            FileOutputStream out, long totalAudioLen,
            long totalDataLen, long longSampleRate, int channels,
            long byteRate) throws IOException
    {
        byte[] header = new byte[44];

        header[0] = 'R';  // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';  // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;  // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8);  // block align
        header[33] = 0;
        header[34] = RECORDER_BPP;  // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, 44);
    }
}
