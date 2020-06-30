package com.example.googletts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;


import com.example.googletts.Retrofit.DTO.InsertWordDTO;
import com.example.googletts.Retrofit.DTO.ResultDTO;
import com.example.googletts.Retrofit.DTO.TestDTO;
import com.example.googletts.Retrofit.DTO.WordBookDTO;
import com.example.googletts.Retrofit.NetworkHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WordbookActivity extends AppCompatActivity {

    private ListView mListView;
    private ArrayList<WordBookDTO> wordbookDTO;
    private ArrayList<String> items;
    private ArrayAdapter adapter;
    private InsertWordDTO newWord;
    private ImageButton imgbtnPrev;
    private ImageButton imgbtnNext;
    private List<TestDTO> userSentence;
    private List<TestDTO> reccomend;

    private ArrayList<WordBookDTO> words; // 원래 문장
    private int REQUEST_INSERT = 1;
    private int REQUEST_DELETE = 2;

    private ResultDTO result;
    private int page = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordbook);
        Intent intent = getIntent();
        mListView = findViewById(R.id.listView);
        imgbtnNext = findViewById(R.id.imgbtn_next);
        imgbtnPrev = findViewById(R.id.imgbtn_prev);

        words = new ArrayList();
        items = new ArrayList<String>();
        adapter = new ArrayAdapter(this, R.layout.listview_item, items);

        wordbookDTO = (ArrayList<WordBookDTO>) intent.getSerializableExtra("word");
        newWord = new InsertWordDTO();

        mListView.setAdapter((adapter));

        Log.e("this activity: ","WordBookActivity open");

        Log.e("WordBookDTO size : ",Integer.toString(wordbookDTO.size()));

        createTextView();
        imgbtnPrev.setEnabled(false);
        if(page == (wordbookDTO.size() -1)/9) {
            imgbtnNext.setEnabled(false);
        }
        else {
            imgbtnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeTextView();
                    page = page + 1;
                    createTextView();
                    if (page == (wordbookDTO.size() - 1) / 9) {
                        imgbtnNext.setEnabled(false);
                    }
                    if (page > 0) {
                        imgbtnPrev.setEnabled(true);
                    }
                }
            });

            imgbtnPrev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeTextView();
                    page = page - 1;
                    createTextView();
                    if (page == 0) {
                        imgbtnPrev.setEnabled(false);
                    }
                    if (page < (wordbookDTO.size() - 1) / 9) {
                        imgbtnNext.setEnabled(true);
                    }
                }
            });
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigationView);
        navigation.setSelectedItemId(R.id.wordbookItem);
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
                                finish();

                            }

                            @Override
                            public void onFailure(Call<ResultDTO> call, Throwable t) {
                                Log.e("Request : ", "fail " + t.getCause());
                            }
                        });
                        break;
                    case R.id.sentenceItem:
                        NetworkHelper networkHelper1 = new NetworkHelper();
                        Call<List<TestDTO>> call1 = networkHelper1.getApiService().requestUserSentence();
                        Log.e("Request : ", "sentence hihihihihii ");
                        call1.enqueue(new Callback<List<TestDTO>>() {
                            @Override
                            public void onResponse(Call<List<TestDTO>> call1, Response<List<TestDTO>> response) {
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

                                userSentence = response.body();
                                Log.e("Request Success: ", userSentence.toString());

                                Intent intentSentence = new Intent(WordbookActivity.this, UserSentenceActivity.class);
                                intentSentence.putExtra("sentence", (Serializable) userSentence);
                                startActivity(intentSentence);
                                finish();
                            }

                            @Override
                            public void onFailure(Call<List<TestDTO>> call1, Throwable t) {
                                Log.e("Request : ", "fail " + t.getCause());
                            }
                        });

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
        menuInflater.inflate(R.menu.menu_word, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_settings1:
                // 문장 추가 하는 텍스트
                // Toast.makeText(getApplicationContext(), "단어추가 버튼 클릭됨", Toast.LENGTH_LONG).show();
                Intent intent1 = new Intent(WordbookActivity.this, AddWordActivity.class);
                startActivityForResult(intent1, REQUEST_INSERT);
                return true;

            case R.id.action_settings2:
                // 문장 삭제 체크리스트 활성화
                // Toast.makeText(getApplicationContext(), "단어삭제 버튼 클릭됨", Toast.LENGTH_LONG).show();
                Intent intent2 = new Intent(WordbookActivity.this, DeleteWordActivity.class);
                intent2.putExtra("word", wordbookDTO);
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
                // Toast.makeText(WordbookActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == REQUEST_INSERT) {
            if (resultCode == RESULT_OK) {
                Log.e("insert word", "success");
                removeTextView();
                newWord = (InsertWordDTO) data.getSerializableExtra("newWord");
                wordbookDTO.add(newWord.getWord());
                createTextView();
            }
        }
    }

//    // 완료
//    public void requestWordBook() {
//        NetworkHelper networkHelper = new NetworkHelper();
//        Call<List<WordBookDTO>> call = networkHelper.getApiService().requestWordBook();
//        call.enqueue(new Callback<List<WordBookDTO>>() {
//            @Override
//            public void onResponse(Call<List<WordBookDTO>> call, Response<List<WordBookDTO>> response) {
//                Log.e("Request : ", "success " + response.isSuccessful());
//                Log.e("Request code", Integer.toString(response.code()));
//                if (!response.isSuccessful()) {
//                    try {
//                        Log.e("Request Message", response.errorBody().string());
//                    } catch (IOException e) {
//                        Log.e("Request IOException", "fuck");
//                    }
//                    return;
//                }
//
//                wordbookDTO = response.body();
//                words = (ArrayList<WordBookDTO>) wordbookDTO;
//                Log.e("Request Success: ", wordbookDTO.toString());
//                createTextView();
//            }
//
//            @Override
//            public void onFailure(Call<List<WordBookDTO>> call, Throwable t) {
//                Log.e("Request : ", "fail " + t.getCause());
//            }
//        });
//    }

    // 완료
    public void createTextView() {
        for(int i=page*9; i<page*9+9 && i<wordbookDTO.size(); i++) {
            items.add(wordbookDTO.get(i).getWordData());
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // To SentenceAcitivity

                NetworkHelper networkHelper1 = new NetworkHelper();
                Call<List<TestDTO>> call1 = networkHelper1.getApiService().requestRecommend(wordbookDTO.get(position).getWordData());
                Log.e("Request : ", "sentence hihihihihii ");
                call1.enqueue(new Callback<List<TestDTO>>() {
                    @Override
                    public void onResponse(Call<List<TestDTO>> call1, Response<List<TestDTO>> response) {
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

                        reccomend = response.body();
                        Log.e("Request Success: ", reccomend.toString());

                        Intent intentSentence = new Intent(WordbookActivity.this, SentenceActivity.class);
                        intentSentence.putExtra("sentence", (Serializable) reccomend);
                        startActivity(intentSentence);
                    }

                    @Override
                    public void onFailure(Call<List<TestDTO>> call1, Throwable t) {
                        Log.e("Request : ", "fail " + t.getCause());
                    }
                });
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
        }
        adapter.notifyDataSetChanged();
    }


}
