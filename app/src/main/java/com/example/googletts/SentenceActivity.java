package com.example.googletts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.googletts.Retrofit.NetworkHelper;
import com.example.googletts.Retrofit.DTO.TestDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SentenceActivity extends AppCompatActivity {
    // private ScrollView mScrollView;
    // private LinearLayout mLinearLayout;
    private ListView mListView;
    private Button mButton;
    private List<TestDTO> sentence;
    private ArrayList<String> items;
    private ArrayAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentence);

        // mScrollView = findViewById(R.id.scrollView);
        // mLinearLayout = findViewById(R.id.linearLayout);
        Intent intent = getIntent();
        mListView = findViewById(R.id.listView);
        sentence = (List<TestDTO>) intent.getSerializableExtra("sentence");
        items = new ArrayList<>();
        mButton = findViewById(R.id.nextWord);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, items);

        mListView.setAdapter((adapter));

        Log.e("this activity: ","SentenceActivity open");

        createTextView();
        // Log.e("sentenceDTO size : ",Integer.toString(sentence.size()));

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeTextView();
                Log.e("sentenceDTO size : ",Integer.toString(sentence.size()));
                if(sentence.size() == 0) {
                    requestSentence();
                }
                createTextView();
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
                sentence = response.body();
                Log.e("Request Success: ", sentence.toString());
                createTextView();
            }

            @Override
            public void onFailure(Call<List<TestDTO>> call, Throwable t) {
                Log.e("Request : ", "fail " + t.getCause());
            }
        });

    }

    public void createTextView() {
        for(int i=0; i<10 && i<sentence.size(); i++) {
            items.add(sentence.get(i).getSentence());
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SentenceActivity.this, EvaluationActivity.class);
                intent.putExtra("sentence", sentence.get(position));
                startActivity(intent);
            }
        });

        adapter.notifyDataSetChanged();
    }

    public void removeTextView() {
        int count;
        count = adapter.getCount();

        for(int i=0;i<count;i++) {
            items.remove(0);
            sentence.remove(0);
        }
        adapter.notifyDataSetChanged();
    }
}
