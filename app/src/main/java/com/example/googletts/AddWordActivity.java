package com.example.googletts;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.googletts.Retrofit.DTO.TestDTO;
import com.example.googletts.Retrofit.DTO.WordBookDTO;
import com.example.googletts.Retrofit.DTO.messageDTO;
import com.example.googletts.Retrofit.NetworkHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddWordActivity extends AppCompatActivity {

    private EditText editText;
    private messageDTO insResult;
    private ArrayList<WordBookDTO> newWord;

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
                Call<messageDTO> call = networkHelper.getApiService().insertWordBook(sentence);

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

                        if(insResult.getMessage().contains("duplicate word")) {
                            //TODO: 문장 중복
                            Log.e("sentence", " duplicate word");
                            Toast.makeText(getApplicationContext(), "이미 존재하는 단어입니다.", Toast.LENGTH_LONG).show();
                        }
                        else if(insResult.getMessage().contains("insWordBookControl Success")) {
                            Log.e("insert", "insWordBookControl Success");
                            Toast.makeText(getApplicationContext(), "단어추가 완료", Toast.LENGTH_LONG).show();


                            Call<List<WordBookDTO>> call1 = networkHelper.getApiService().requestWordBook();
                            call1.enqueue(new Callback<List<WordBookDTO>>() {
                                @Override
                                public void onResponse(Call<List<WordBookDTO>> call1, Response<List<WordBookDTO>> response1) {
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

                                    newWord = (ArrayList<WordBookDTO>) response1.body();

                                    Intent intent = new Intent(AddWordActivity.this, WordbookActivity.class);
                                    intent.putExtra("word", newWord);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                    // startActivity(intent);
                                }

                                @Override
                                public void onFailure(Call<List<WordBookDTO>> call1, Throwable t) {
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
