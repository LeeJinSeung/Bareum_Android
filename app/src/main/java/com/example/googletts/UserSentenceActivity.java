package com.example.googletts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

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

public class UserSentenceActivity extends AppCompatActivity {

    private ListView mListView;
    private List<TestDTO> sentenceDTO;
    private ArrayList<String> items;
    private ArrayAdapter adapter;
    private List<WordBookDTO> wordbookDTO;
    private ImageButton imgbtnPrev;
    private ImageButton imgbtnNext;

    private int REQUEST_INSERT = 1;
    private int REQUEST_DELETE = 2;
    private int page = 0;

    private ResultDTO result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sentence);
        Intent intent = getIntent();
        mListView = findViewById(R.id.listView);
        sentenceDTO = (ArrayList<TestDTO>) intent.getSerializableExtra("sentence");
        items = new ArrayList<String>();
        adapter = new ArrayAdapter(this, R.layout.listview_item, items);
        imgbtnNext = findViewById(R.id.imgbtn_next);
        imgbtnPrev = findViewById(R.id.imgbtn_prev);

        mListView.setAdapter(adapter);
        createTextView();

        imgbtnPrev.setEnabled(false);
        if(page == (sentenceDTO.size() -1)/9) {
            imgbtnNext.setEnabled(false);
        }
        else {
            imgbtnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeTextView();
                    page = page + 1;
                    createTextView();
                    if (page == (sentenceDTO.size() - 1) / 9) {
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
                    if (page < (sentenceDTO.size() - 1) / 9) {
                        imgbtnNext.setEnabled(true);
                    }
                }
            });
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigationView);
        navigation.setSelectedItemId(R.id.sentenceItem);
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

                                Intent intentResult = new Intent(UserSentenceActivity.this, ResultActivity.class);
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
                        break;
                    case R.id.wordbookItem:
                        NetworkHelper networkHelper1 = new NetworkHelper();
                        Call<List<WordBookDTO>> call1 = networkHelper1.getApiService().requestWordBook();
                        call1.enqueue(new Callback<List<WordBookDTO>>() {
                            @Override
                            public void onResponse(Call<List<WordBookDTO>> call1, Response<List<WordBookDTO>> response) {
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
                                Log.e("Request Success: ", wordbookDTO.toString());
                                Intent intentWordBook = new Intent(UserSentenceActivity.this, WordbookActivity.class);
                                intentWordBook.putExtra("word", (Serializable) wordbookDTO);
                                startActivity(intentWordBook);
                                finish();

                            }

                            @Override
                            public void onFailure(Call<List<WordBookDTO>> call1, Throwable t) {
                                Log.e("Request : ", "fail " + t.getCause());
                            }
                        });

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
                // Toast.makeText(getApplicationContext(), "문장추가 버튼 클릭됨", Toast.LENGTH_LONG).show();
                Intent intent1 = new Intent(UserSentenceActivity.this, AddSentenceActivity.class);
                startActivityForResult(intent1, REQUEST_INSERT);
                return true;

            case R.id.action_settings2:
                // 문장 삭제 체크리스트 활성화
                // Toast.makeText(getApplicationContext(), "문장삭제 버튼 클릭됨", Toast.LENGTH_LONG).show();
                Intent intent2 = new Intent(UserSentenceActivity.this, DeleteSentenceActivity.class);
                intent2.putExtra("sentence", (Serializable) sentenceDTO);
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
                // Toast.makeText(UserSentenceActivity.this, "Result: " + data.toString(), Toast.LENGTH_SHORT).show();
                Log.e("result : ", data.toString());
                // Intent intent = getIntent();
                ArrayList<Integer> deleteSentence = (ArrayList<Integer>) data.getSerializableExtra("index");
                Log.e("idx", deleteSentence.toString());
                for(int i=0;i<deleteSentence.size();i++) {
                    Log.e("index", deleteSentence.get(i).toString());
                    items.remove(deleteSentence.get(i).intValue());
                    sentenceDTO.remove(deleteSentence.get(i).intValue());
                }
                Log.e("item remove?", Integer.toString(items.size()));
                for(int i=10-deleteSentence.size(); i<10 && i<sentenceDTO.size(); i++) {
                    items.add(sentenceDTO.get(i).getSentence());
                }

                adapter.notifyDataSetChanged();

            } else {   // RESULT_CANCEL
                Toast.makeText(UserSentenceActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == REQUEST_INSERT) {
            if (resultCode == RESULT_OK) {
                Log.e("insert sentence", "success");
                removeTextView();
                sentenceDTO.add((TestDTO) data.getSerializableExtra("sentence"));
                createTextView();
            }
        }
    }

//    public void requestSentence() {
//        NetworkHelper networkHelper = new NetworkHelper();
//        Call<List<TestDTO>> call = networkHelper.getApiService().requestUserSentence();
//        Log.e("Request : ", "sentence hihihihihii ");
//        call.enqueue(new Callback<List<TestDTO>>() {
//            @Override
//            public void onResponse(Call<List<TestDTO>> call, Response<List<TestDTO>> response) {
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
//                sentenceDTO = response.body();
//                Log.e("Request Success: ", sentenceDTO.toString());
//                createTextView();
//            }
//
//            @Override
//            public void onFailure(Call<List<TestDTO>> call, Throwable t) {
//                Log.e("Request : ", "fail " + t.getCause());
//            }
//        });
//
//    }

    public void createTextView() {
        for(int i=page*9; i<9*page + 9 && i<sentenceDTO.size(); i++) {
            items.add(sentenceDTO.get(i).getSentence());
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO: 발음평가로 이동
                Intent intent = new Intent(UserSentenceActivity.this, EvaluationActivity.class);
                intent.putExtra("sentence", sentenceDTO.get(page*9 + position));
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
        }
        adapter.notifyDataSetChanged();
    }


}
