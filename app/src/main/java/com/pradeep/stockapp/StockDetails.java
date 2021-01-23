package com.pradeep.stockapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.pradeep.stockapp.common.AppUtils;

public class StockDetails extends AppCompatActivity {
    String stock_symbol;
    LineChart chart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);
        stock_symbol  = getIntent().getStringExtra(AppUtils.STOCK_SYMBOL_EXTRA);
        initView();
    }

    private void initView() {
        chart = findViewById(R.id.chart);

    }
}
