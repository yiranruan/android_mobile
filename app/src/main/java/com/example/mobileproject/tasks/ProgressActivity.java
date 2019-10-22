package com.example.mobileproject.tasks;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.mobileproject.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class ProgressActivity extends AppCompatActivity {

    float testdata[] = {0.3f, 0.3f, 0.3f, 0.6f, 0.2f};

    String users[] = {"user1", "user2", "user3", "user4", "user5"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        setupPieChart();
    }

    private void setupPieChart() {
        // Populating a list of pieEntries:
        List<PieEntry> pieEntryList = new ArrayList<>();

        for (int i = 0; i < testdata.length; i++){
            pieEntryList.add(new PieEntry(testdata[i],users[i]));

        }

        PieDataSet dataSet = new PieDataSet(pieEntryList, "Users");
        PieData data = new PieData(dataSet);

        PieChart chart = (PieChart) findViewById(R.id.PieChart);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS );
        chart.setData(data);
        chart.animateY(1000);
        chart.invalidate();
    }
}
