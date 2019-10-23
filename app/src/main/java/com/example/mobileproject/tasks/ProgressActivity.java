package com.example.mobileproject.tasks;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.mobileproject.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProgressActivity extends AppCompatActivity {

    HashMap<String, Integer> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        Intent intent = getIntent();
        Log.d("intent data, ","onCreate: " + intent.getIntExtra("todo",0));
        data = new HashMap<>();
        data.put("todo", intent.getIntExtra("todo",0));
        data.put("doing", intent.getIntExtra("doing",0));
        data.put("done", intent.getIntExtra("done",0));
        setupPieChart();
    }

    private void setupPieChart() {
        // Populating a list of pieEntries:
        List<PieEntry> pieEntryList = new ArrayList<>();

        float todo = data.get("todo").floatValue();
        float doing =data.get("doing").floatValue();
        float done = data.get("done").floatValue();

        if(todo != 0) {
            pieEntryList.add(new PieEntry(data.get("todo").floatValue(), "Todo"));
        }
        if(doing != 0) {
            pieEntryList.add(new PieEntry(data.get("doing").floatValue(), "Doing"));
        }
        if(done != 0) {
            pieEntryList.add(new PieEntry(data.get("done").floatValue(), "Done"));
        }

        PieDataSet dataSet = new PieDataSet(pieEntryList, "Work loads");
        PieData data = new PieData(dataSet);

        PieChart chart = (PieChart) findViewById(R.id.PieChart);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS );
        chart.setData(data);
        chart.animateY(1000);
        chart.invalidate();
    }
}
