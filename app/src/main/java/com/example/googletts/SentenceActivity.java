package com.example.googletts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.googletts.Retrofit.DTO.TestDTO;

import java.util.ArrayList;
import java.util.List;



public class SentenceActivity extends AppCompatActivity {
    private ListView mListView;
    private ImageButton imgbtnPrev;
    private ImageButton imgbtnNext;
    private ArrayList<TestDTO> sentence;
    private ArrayList<String> items;
    private ArrayAdapter adapter;
    private int page = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentence);
        Intent intent = getIntent();
        mListView = findViewById(R.id.listView);
        sentence = (ArrayList<TestDTO>) intent.getSerializableExtra("sentence");
        items = new ArrayList<String>();
        imgbtnNext = findViewById(R.id.imgbtn_next);
        imgbtnPrev = findViewById(R.id.imgbtn_prev);
//        imgbtnPrev.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, items);

        mListView.setAdapter(adapter);

        Log.e("this activity: ","SentenceActivity open");
        Log.e("sentence ", sentence.toString());
        Log.e("sentence size ", Integer.toString(sentence.size()));

        createTextView();
        page = page + 1;
        imgbtnPrev.setEnabled(false);
        if(page == (sentence.size() -1)/10 + 1) {
            imgbtnNext.setEnabled(false);
        }
        else {
            // Log.e("sentenceDTO size : ",Integer.toString(sentence.size()));

            imgbtnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeTextView();
                    page = page + 1;
                    createTextView();
                    if (page == (sentence.size() - 1) / 10) {
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
                    if (page < (sentence.size() - 1) / 10) {
                        imgbtnNext.setEnabled(true);
                    }
                }
            });
        }
    }

    public void createTextView() {
        for(int i = page * 10; i< page * 10 + 10 && i<sentence.size(); i++) {
            items.add(sentence.get(i).getSentence());
            Log.e("add : ", sentence.get(i).getSentence());
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
        }

        adapter.notifyDataSetChanged();
    }
}
