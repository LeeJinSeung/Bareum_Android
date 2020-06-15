package com.example.googletts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.googletts.Retrofit.DTO.ResultDTO;
import com.example.googletts.Retrofit.DTO.TestDTO;
import com.example.googletts.Retrofit.DTO.WordBookDTO;
import com.example.googletts.Retrofit.NetworkHelper;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ResultActivity extends AppCompatActivity {

    private LineChart chart;
    private ResultDTO result;
    private TableLayout mtableLayout;
    private List<TestDTO> userSentence;
    private List<WordBookDTO> wordbookDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        chart = findViewById(R.id.chart);
        mtableLayout = findViewById(R.id.tableLayout);

        Intent intent = getIntent();
        result = (ResultDTO) intent.getSerializableExtra("result");

        if (result.getScore().size() < 5) {
            Log.e("not 5 ", "score");
        } else {

            ArrayList<Entry> values = new ArrayList<>();
            Log.e("score", result.getScore().toString());
            for (int i = 0; i < 5; i++) {
                float val = result.getScore().get(i);
                values.add(new Entry(i + 1, val));
            }

            LineDataSet set1;
            set1 = new LineDataSet(values, "최근 발음 평가 점수");

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the data sets

            // create a data object with the data sets
            LineData data = new LineData(dataSets);

            XAxis xAxis = chart.getXAxis();

            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

            xAxis.setLabelCount(1);
            // xAxis.setDrawLabels(true);


            YAxis yAxisLeft = chart.getAxisLeft();
            yAxisLeft.setAxisMinimum(0);
            yAxisLeft.setAxisMaximum(1);

            YAxis yAxisRight = chart.getAxisRight();
            yAxisRight.setDrawLabels(false);
            yAxisRight.setDrawGridLines(false);

            // black lines and points
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);

            chart.getDescription().setEnabled(false);
            // set data
            chart.setData(data);
        }

        if (result.getMostPhoneme().size() > 0) {
            Typeface face = ResourcesCompat.getFont(mtableLayout.getContext(), R.font.ridibatang);

            TableRow tr = new TableRow(mtableLayout.getContext());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            tr.setLayoutParams(lp);
            TextView posTv = new TextView(tr.getContext());
            posTv.setGravity(Gravity.CENTER);
            posTv.setText("위치");
            posTv.setTypeface(face);
            TextView phoTv = new TextView(tr.getContext());
            phoTv.setGravity(Gravity.CENTER);
            phoTv.setTypeface(face);
            phoTv.setText("음소");

            posTv.setTypeface(posTv.getTypeface(), Typeface.BOLD);
            phoTv.setTypeface(phoTv.getTypeface(), Typeface.BOLD);
            posTv.setTextSize(22);
            phoTv.setTextSize(22);
            posTv.setTextColor(Color.BLACK);
            phoTv.setTextColor(Color.BLACK);
            posTv.setPadding(0,10,0,10);
            phoTv.setPadding(0,10,0,10);
            tr.addView(posTv);
            tr.addView(phoTv);
            mtableLayout.addView(tr);


            for (int i = 0; i < result.getMostPhoneme().size(); i++) {
                TableRow newTr = new TableRow(mtableLayout.getContext());
                TableRow.LayoutParams newLp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                newTr.setLayoutParams(newLp);
                TextView newPosTv = new TextView(newTr.getContext());
                newPosTv.setGravity(Gravity.CENTER);
                ArrayList<String> phoneme = result.getMostPhoneme().get(i);
                newPosTv.setText(phoneme.get(0));
                TextView newPhoTv = new TextView(newTr.getContext());
                newPhoTv.setGravity(Gravity.CENTER);
                if (phoneme.get(1).contains("u")) {
                    newPhoTv.setText("자음");
                } else if (phoneme.get(1).contains("m")) {
                    newPhoTv.setText("모음");
                } else if (phoneme.get(1).contains("b")) {
                    newPhoTv.setText("받침");
                }

                newPosTv.setTypeface(face);
                newPhoTv.setTypeface(face);
                newPosTv.setTextSize(18);
                newPhoTv.setTextSize(18);
                newPhoTv.setPadding(0,10,0,10);
                newPosTv.setPadding(0,10,0,10);
                newPosTv.setTextColor(Color.BLACK);
                newPhoTv.setTextColor(Color.BLACK);

                newTr.addView(newPosTv);
                newTr.addView(newPhoTv);
                mtableLayout.addView(newTr);
            }
        }
        else {
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigationView);
        navigation.setSelectedItemId(R.id.resultItem);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.resultItem:
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

                                Intent intentSentence = new Intent(ResultActivity.this, UserSentenceActivity.class);
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
                                Log.e("Request Success: ", wordbookDTO.toString());
                                Intent intentWordBook = new Intent(ResultActivity.this, WordbookActivity.class);
                                intentWordBook.putExtra("word", (Serializable) wordbookDTO);
                                startActivity(intentWordBook);
                                finish();

                            }

                            @Override
                            public void onFailure(Call<List<WordBookDTO>> call, Throwable t) {
                                Log.e("Request : ", "fail " + t.getCause());
                            }
                        });


                        break;
                }
                return false;
            }
        });
    }
}

