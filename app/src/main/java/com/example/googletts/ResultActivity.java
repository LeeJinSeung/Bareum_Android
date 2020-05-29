package com.example.googletts;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.example.googletts.Retrofit.DTO.ResultDTO;
import com.example.googletts.Retrofit.DTO.TestDTO;
import com.example.googletts.Retrofit.NetworkHelper;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ResultActivity extends AppCompatActivity {

    private LineChart chart;
    private ResultDTO result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        chart = findViewById(R.id.chart);

        NetworkHelper networkHelper = new NetworkHelper();
        Call<ResultDTO> call = networkHelper.getApiService().requestTotal();
        Log.e("Request : ", "result hihihihihii ");
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

                if(result.getScore().size() < 5) {
                    Log.e("not 5 ", "score");
                }

                ArrayList<Entry> values = new ArrayList<>();

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

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                Log.e("Request : ", "fail " + t.getCause());
            }
        });



    }
}

