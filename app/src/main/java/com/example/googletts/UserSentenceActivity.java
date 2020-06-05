package com.example.googletts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


import com.example.googletts.Retrofit.DTO.TestDTO;
import com.example.googletts.Retrofit.NetworkHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserSentenceActivity extends AppCompatActivity {

    private ListView mListView;
    private Button mButton;
    private List<TestDTO> sentenceDTO;
    private ArrayList<String> items;
    private ArrayAdapter adapter;
    private String delResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sentence);

        mListView = findViewById(R.id.listView);
        mButton = findViewById(R.id.button);
        sentenceDTO = new ArrayList();
        items = new ArrayList<String>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, items);

        mListView.setAdapter((adapter));

        Log.e("this activity: ","SentenceActivity open");

        requestSentence();
        Log.e("sentenceDTO size : ",Integer.toString(sentenceDTO.size()));

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeTextView();
                Log.e("sentenceDTO size : ",Integer.toString(sentenceDTO.size()));
                if(sentenceDTO.size() == 0) {
                    requestSentence();
                }
                createTextView();
            }
        });
    }

    public void requestSentence() {
        NetworkHelper networkHelper = new NetworkHelper();
        Call<List<TestDTO>> call = networkHelper.getApiService().requestUserSentence();
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
                Log.e("Request Success: ", sentenceDTO.toString());
                createTextView();
            }

            @Override
            public void onFailure(Call<List<TestDTO>> call, Throwable t) {
                Log.e("Request : ", "fail " + t.getCause());
            }
        });

    }

    public void createTextView() {
        for(int i=0; i<5 && i<sentenceDTO.size(); i++) {
            items.add(sentenceDTO.get(i).getSentence());
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NetworkHelper networkHelper = new NetworkHelper();
                int sid = sentenceDTO.get(position).getSid();
                ArrayList<Integer> arrayList = new ArrayList<>();
                // arrayList.add(sentenceDTO.get(position).getSid());
                arrayList.add(38);
                arrayList.add(39);
                Log.e("arraylist", arrayList.toString());
                Call<String> call = networkHelper.getApiService().postDeleteSentaence(arrayList);
                Log.e("sid", Integer.toString(sid));
                Log.e("Request : ", "sentence hihihihihii ");
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (!response.isSuccessful()) {
                            try {
                                Log.e("Request Message", response.errorBody().string());
                            } catch (IOException e) {
                                Log.e("Request IOException", "fuck");
                            }
                            return;
                        }
                        delResult = response.body();
                        Log.e("response", "OK");

                        if(delResult.contains("delSentenceControl success")) {
                            // success
                            Log.e("delete", "success");
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e("Request : ", "fail " + t.getCause());
                    }
                });
            }
        });

        adapter.notifyDataSetChanged();
    }

    public void removeTextView() {
        int count;
        count = adapter.getCount();

        for(int i=0;i<count;i++) {
            items.remove(0);
            sentenceDTO.remove(0);
        }
        adapter.notifyDataSetChanged();
    }


}
