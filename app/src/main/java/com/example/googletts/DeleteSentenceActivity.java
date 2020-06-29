package com.example.googletts;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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

public class DeleteSentenceActivity extends AppCompatActivity {

    private ListView listview;
    private ArrayAdapter<String> adapter;
    private List<String> list;
    private ArrayList<TestDTO> sentence;
    private messageDTO delResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_sentence);

        listview = (ListView)findViewById(R.id.listview);

        //데이터를 저장하게 되는 리스트
        list = new ArrayList<>();

        //리스트뷰와 리스트를 연결하기 위해 사용되는 어댑터
        adapter = new ArrayAdapter<>(this, R.layout.listview_item_select, list);
        // simple_list_item_multiple_choice

        //리스트뷰의 어댑터를 지정해준다.
        listview.setAdapter(adapter);

        Intent intent = getIntent();
        sentence = (ArrayList<TestDTO>) intent.getExtras().getSerializable("sentence");


        //리스트뷰에 보여질 아이템을 추가
        for(int i=0;i<sentence.size();i++) {
            list.add(sentence.get(i).getSentence());
        }
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
                // 문장 추가 삭제 완료.
                Toast.makeText(getApplicationContext(), "문장이 삭제되었습니디.", Toast.LENGTH_LONG).show();

                ArrayList<Integer> selectedItems = new ArrayList<>();
                ArrayList<Integer> delIdx = new ArrayList<>();

                //리스트뷰에서 선택된 아이템의 목록을 가져온다.
                SparseBooleanArray checkedItemPositions = listview.getCheckedItemPositions();
                for( int i=0; i<checkedItemPositions.size(); i++){
                    int pos = checkedItemPositions.keyAt(i);

                    if (checkedItemPositions.valueAt(i))
                    {
                        selectedItems.add(sentence.get(pos).getSid());
                        delIdx.add(0, pos);
                        Log.e("sentence : ",sentence.get(pos).getSentence());
                    }
                }
                Log.e("select", selectedItems.toString());
                Log.e("idx", delIdx.toString());

                NetworkHelper networkHelper = new NetworkHelper();
                Call<messageDTO> call = networkHelper.getApiService().deleteSentaence(selectedItems);

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
                        delResult = response.body();
                        Log.e("response", "OK");

                        if(delResult.getMessage().contains("delSentenceControl success")) {
                            // success
                            Log.e("delete", "success");
                            Intent intent = new Intent(DeleteSentenceActivity.this, UserSentenceActivity.class);
                            intent.putExtra("index", delIdx);
                            setResult(RESULT_OK, intent);
                            finish();
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

    @Override
    public void finish() {
        super.finish();
        // overridePendingTransition(R.anim.anim_not_move, R.anim.anim_not_move);
    }
}
