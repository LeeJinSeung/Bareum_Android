package com.example.googletts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import com.example.googletts.Retrofit.DTO.ResultDTO;
import com.example.googletts.Retrofit.DTO.TestDTO;
import com.example.googletts.Retrofit.DTO.WordBookDTO;
import com.example.googletts.Retrofit.NetworkHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WordbookActivity extends AppCompatActivity {

    private ListView mListView;
    private Button mButton;
    private Button mButton2;
    private List<WordBookDTO> wordbookDTO;
    private ArrayList<String> items;
    private ArrayAdapter adapter;

    private ArrayList<WordBookDTO> words;
    private int REQUEST_INSERT = 1;
    private int REQUEST_DELETE = 2;

    private ResultDTO result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordbook);

        mListView = findViewById(R.id.listView);
        mButton = findViewById(R.id.nextWord);
        wordbookDTO = new ArrayList();
        items = new ArrayList<String>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, items);

        words = new ArrayList<>();
        mButton2 = findViewById(R.id.button3);

        mListView.setAdapter((adapter));

        Log.e("this activity: ","WordBookActivity open");

        requestWordBook();
        Log.e("WordBookDTO size : ",Integer.toString(wordbookDTO.size()));

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeTextView();
                Log.e("WordBookDTO size : ",Integer.toString(wordbookDTO.size()));
                if(wordbookDTO.size() == 0) {
                    requestWordBook();
                }
                createTextView();
            }
        });

        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkHelper networkHelper = new NetworkHelper();
                Call<ResultDTO> call = networkHelper.getApiService().requestTotal();
                call.enqueue(new Callback<ResultDTO>() {
                    @Override
                    public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
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
                        result = response.body();

                        Log.e("Request phoneme: ", result.getMostPhoneme().toString());
                        Log.e("Request score: ", result.getScore().toString());

                        Intent intent = new Intent(WordbookActivity.this, ResultActivity.class);
                        intent.putExtra("result", result);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<ResultDTO> call, Throwable t) {
                        Log.e("Request : ", "fail " + t.getCause());
                    }
                });
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigationView);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.resultItem:
                        NetworkHelper networkHelper = new NetworkHelper();
                        Call<ResultDTO> call = networkHelper.getApiService().requestTotal();
                        call.enqueue(new Callback<ResultDTO>() {
                            @Override
                            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
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
                                result = response.body();

                                Log.e("Request phoneme: ", result.getMostPhoneme().toString());
                                Log.e("Request score: ", result.getScore().toString());

                                Intent intentResult = new Intent(WordbookActivity.this, ResultActivity.class);
                                intentResult.putExtra("result", result);
                                startActivity(intentResult);
                            }

                            @Override
                            public void onFailure(Call<ResultDTO> call, Throwable t) {
                                Log.e("Request : ", "fail " + t.getCause());
                            }
                        });
                        break;
                    case R.id.sentenceItem:
                        Intent intentSentence = new Intent(WordbookActivity.this, UserSentenceActivity.class);
                        startActivity(intentSentence);
                        break;
                    case R.id.wordbookItem:
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_settings1:
                // 문장 추가 하는 텍스트
                Toast.makeText(getApplicationContext(), "단어추가 버튼 클릭됨", Toast.LENGTH_LONG).show();
                Intent intent1 = new Intent(WordbookActivity.this, AddWordActivity.class);
                startActivityForResult(intent1, REQUEST_INSERT);
                return true;

            case R.id.action_settings2:
                // 문장 삭제 체크리스트 활성화
                Toast.makeText(getApplicationContext(), "단어삭제 버튼 클릭됨", Toast.LENGTH_LONG).show();
                Intent intent2 = new Intent(WordbookActivity.this, DeleteWordActivity.class);
                intent2.putExtra("word", words);
                startActivityForResult(intent2, REQUEST_DELETE);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_DELETE) {
            if (resultCode == RESULT_OK) {
                Log.e("result : ", data.toString());
                ArrayList<Integer> deleteWord = (ArrayList<Integer>) data.getSerializableExtra("index");
                Log.e("idx", deleteWord.toString());
                for(int i=0;i<deleteWord.size();i++) {
                    Log.e("index", deleteWord.get(i).toString());
                    items.remove(deleteWord.get(i).intValue());
                    wordbookDTO.remove(deleteWord.get(i).intValue());
                }
                Log.e("item remove?", Integer.toString(items.size()));
                for(int i=10-deleteWord.size(); i<10 && i<wordbookDTO.size(); i++) {
                    items.add(wordbookDTO.get(i).getWordData());
                }

                adapter.notifyDataSetChanged();

            } else {   // RESULT_CANCEL
                Toast.makeText(WordbookActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == REQUEST_INSERT) {
            if (resultCode == RESULT_OK) {
                Log.e("insert word", "success");
                removeTextView();
                wordbookDTO = (ArrayList<WordBookDTO>) data.getSerializableExtra("word");
                createTextView();
            }
        }
    }

    // 완료
    public void requestWordBook() {
        NetworkHelper networkHelper = new NetworkHelper();
        Call<List<WordBookDTO>> call = networkHelper.getApiService().requestWordBook();
        call.enqueue(new Callback<List<WordBookDTO>>() {
            @Override
            public void onResponse(Call<List<WordBookDTO>> call, Response<List<WordBookDTO>> response) {
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

                wordbookDTO = response.body();
                words = (ArrayList<WordBookDTO>) wordbookDTO;
                Log.e("Request Success: ", wordbookDTO.toString());
                createTextView();
            }

            @Override
            public void onFailure(Call<List<WordBookDTO>> call, Throwable t) {
                Log.e("Request : ", "fail " + t.getCause());
            }
        });
    }

    // 완료
    public void createTextView() {
        for(int i=0; i<10 && i<wordbookDTO.size(); i++) {
            items.add(wordbookDTO.get(i).getWordData());
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // To SentenceAcitivity
                Intent intent = new Intent(WordbookActivity.this, SentenceActivity.class);
                //TODO 단어가 포함되어 있는 문장 리스트 (key 확인)
                intent.putExtra("setenceList", (ArrayList)wordbookDTO.get(position).getRecommend());
                startActivity(intent);
            }
        });

        adapter.notifyDataSetChanged();
    }

    // 완료
    public void removeTextView() {
        int count;
        count = adapter.getCount();

        for(int i=0;i<count;i++) {
            items.remove(0);
            wordbookDTO.remove(0);
        }
        adapter.notifyDataSetChanged();
    }


}
