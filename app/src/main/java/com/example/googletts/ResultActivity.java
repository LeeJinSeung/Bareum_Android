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
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.googletts.Retrofit.DTO.ResultDTO;
import com.example.googletts.Retrofit.DTO.TestDTO;
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
    private List<TestDTO> sentence;



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
            TableRow tr = new TableRow(mtableLayout.getContext());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            tr.setLayoutParams(lp);
            TextView posTv = new TextView(tr.getContext());
            posTv.setText("위치");
            TextView phoTv = new TextView(tr.getContext());
            phoTv.setText("음소");
            tr.addView(posTv);
            tr.addView(phoTv);
            mtableLayout.addView(tr);


            for (int i = 0; i < result.getMostPhoneme().size(); i++) {
                TableRow newTr = new TableRow(mtableLayout.getContext());
                TableRow.LayoutParams newLp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                newTr.setLayoutParams(newLp);
                TextView newPosTv = new TextView(newTr.getContext());
                ArrayList<String> phoneme = result.getMostPhoneme().get(i);
                newPosTv.setText(phoneme.get(0));
                newTr.addView(newPosTv);
                TextView newPhoTv = new TextView(newTr.getContext());
                if (phoneme.get(1).contains("u")) {
                    newPhoTv.setText("자음");
                } else if (phoneme.get(1).contains("m")) {
                    newPhoTv.setText("모음");
                } else if (phoneme.get(1).contains("b")) {
                    newPhoTv.setText("받침");
                }

                newTr.addView(newPhoTv);
                mtableLayout.addView(newTr);
            }
        }
        else {
        }
    }
}

