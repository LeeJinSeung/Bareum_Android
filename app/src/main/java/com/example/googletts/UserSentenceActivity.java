package com.example.googletts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

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

    private List<TestDTO> sentence;
    private int REQUEST_INSERT = 1;
    private int REQUEST_DELETE = 2;

    private ResultDTO result;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sentence);

        Intent intent = getIntent();
        mListView = findViewById(R.id.listView);
        sentenceDTO = new ArrayList();
        items = new ArrayList<String>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, items);

        sentence = (List<TestDTO>) intent.getSerializableExtra("sentence");

        mListView.setAdapter(adapter);

        createTextView();

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
                Toast.makeText(getApplicationContext(), "문장추가 버튼 클릭됨", Toast.LENGTH_LONG).show();
                Intent intent1 = new Intent(UserSentenceActivity.this, AddSentenceActivity.class);
                startActivityForResult(intent1, REQUEST_INSERT);
                return true;

            case R.id.action_settings2:
                // 문장 삭제 체크리스트 활성화
                Toast.makeText(getApplicationContext(), "문장삭제 버튼 클릭됨", Toast.LENGTH_LONG).show();
                Intent intent2 = new Intent(UserSentenceActivity.this, DeleteSentenceActivity.class);
                intent2.putExtra("sentence", (Serializable) sentence);
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
                sentenceDTO = (ArrayList<TestDTO>) data.getSerializableExtra("sentence");
                createTextView();
            }
        }
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
                sentence = (ArrayList<TestDTO>) sentenceDTO;
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
        for(int i=0; i<10 && i<sentenceDTO.size(); i++) {
            items.add(sentenceDTO.get(i).getSentence());
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO: 발음평가로 이동
                Intent intent = new Intent(UserSentenceActivity.this, EvaluationActivity.class);
                intent.putExtra("sentence", sentenceDTO.get(position));
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
            sentenceDTO.remove(0);
        }
        adapter.notifyDataSetChanged();
    }


}
