package com.example.googletts;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.googletts.Retrofit.NetworkHelper;
import com.example.googletts.Retrofit.SentenceDTO;
import com.example.googletts.Retrofit.TestDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SentenceActivity extends AppCompatActivity {
    private ScrollView mScrollView;
    private LinearLayout mLinearLayout;
    private Button mButton;
    private List<TestDTO> sentenceDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentence);

        mScrollView = findViewById(R.id.scrollView);
        mLinearLayout = findViewById(R.id.linearLayout);
        mButton = findViewById(R.id.button);
        sentenceDTO = new ArrayList();

        Log.e("this activity: ","SentenceActivity open");

        requestSentence();

        Log.e("sentenceDTO size : ",Integer.toString(sentenceDTO.size()));

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("sentenceDTO size : ",Integer.toString(sentenceDTO.size()));
                if(sentenceDTO.size() == 0) {
                    requestSentence();
                }

                for(int i=0; i<5; i++) {
                    if(sentenceDTO.size() == 0) break;

                    mLinearLayout.addView(createTextView());
                }

            }
        });
    }

    public void requestSentence() {
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
                sentenceDTO = response.body();

                for(int i=0; i<5; i++) {
                    if(sentenceDTO.size() == 0) break;

                    mLinearLayout.addView(createTextView());
                }

                Log.e("Request Success: ", sentenceDTO.toString());
            }

            @Override
            public void onFailure(Call<List<TestDTO>> call, Throwable t) {
                Log.e("Request : ", "fail " + t.getCause());
            }
        });

    }

    public TextView createTextView() {
        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView newTextView = new TextView(this);
        newTextView.setTextSize(25);
        newTextView.setPadding(0,12,0,12);
        newTextView.setId(sentenceDTO.get(0).getSid());
        newTextView.setLayoutParams(lparams);
        newTextView.setText(sentenceDTO.get(0).getSentence());
        sentenceDTO.remove(0);
        return newTextView;
    }
}
