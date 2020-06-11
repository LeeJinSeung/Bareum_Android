package com.example.googletts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.googletts.Retrofit.DTO.TestDTO;
import com.example.googletts.Retrofit.DTO.messageDTO;
import com.example.googletts.Retrofit.NetworkHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddSentenceActivity extends AppCompatActivity {

    private EditText editText;
    private messageDTO insResult;
    private ArrayList<TestDTO> newSentence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sentence);

        editText = findViewById(R.id.edittext);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_ok, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_settings1:
                // 문장 추가 완료.

                String sentence = editText.getText().toString();

                NetworkHelper networkHelper = new NetworkHelper();
                Call<messageDTO> call = networkHelper.getApiService().insertSentaence(sentence);

                call.enqueue(new Callback<messageDTO>() {
                    @Override
                    public void onResponse(Call<messageDTO> call, Response<messageDTO> response) {
                        if (!response.isSuccessful()) {
                            try {
                                Log.e("Request Message", response.errorBody().string());
                            } catch (IOException e) {
                                Log.e("Request IOException", "fuck");
                            }
                            return;
                        }
                        insResult = response.body();
                        Log.e("response", "OK");

                        if(insResult.getMessage().contains("duplicate sentence")) {
                            //TODO: 문장 중복
                            Log.e("sentence", " duplicate");
                            Toast.makeText(getApplicationContext(), "이미 존재하는 문장입니다.", Toast.LENGTH_LONG).show();
                        }
                        else if(insResult.getMessage().contains("sentence cannot be converted")) {
                            //TODO: 표준 발음으로 변경할 수 없는 경우
                            Log.e("sentence", "cannot be converted");
                            Toast.makeText(getApplicationContext(), "표준발음으로 변환할 수 없는 문장입니다.", Toast.LENGTH_LONG).show();
                        }
                        else if(insResult.getMessage().contains("insSentenceControl success")) {
                            Log.e("insert", "success");
                            Toast.makeText(getApplicationContext(), "문장추가 완료", Toast.LENGTH_LONG).show();


                            Call<List<TestDTO>> call1 = networkHelper.getApiService().requestUserSentence();
                            call1.enqueue(new Callback<List<TestDTO>>() {
                                @Override
                                public void onResponse(Call<List<TestDTO>> call1, Response<List<TestDTO>> response1) {
                                    if (!response1.isSuccessful()) {
                                        try {
                                            Log.e("Request Message", response1.errorBody().string());
                                        } catch (IOException e) {
                                            Log.e("Request IOException", "fuck");
                                        }
                                        return;
                                    }
                                    Log.e("response", "OK");
                                    Log.e("body", response1.body().toString());

                                    newSentence = (ArrayList<TestDTO>) response1.body();

                                    Intent intent = new Intent(AddSentenceActivity.this, UserSentenceActivity.class);
                                    intent.putExtra("sentence", newSentence);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                    // startActivity(intent);
                                }

                                @Override
                                public void onFailure(Call<List<TestDTO>> call1, Throwable t) {
                                    Log.e("Request : ", "fail " + t.getCause());
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<messageDTO> call, Throwable t) {
                        Log.e("Request : ", "fail " + t.getCause());
                    }
                });

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
