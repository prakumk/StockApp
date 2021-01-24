package com.pradeep.stockapp.chart;


import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

public class CandleChartFactory {
    String chartDescription = "Monthly Data in BTC value";
    ArrayList<CandleEntry> entries = new ArrayList<>();
    ArrayList<String> xLabel = new ArrayList<>();

    // constructor: test hard-coded data
    public CandleChartFactory(){
        // CandleEntry(time, HIGH, LOW, OPEN, CLOSE)
        entries.add(new CandleEntry(0, 4.62f, 2.02f, 2.70f, 4.13f));
        entries.add(new CandleEntry(1, 6.25f, 3.02f, 4.13f, 4.02f));
        entries.add(new CandleEntry(2, 7.25f, 3.52f, 4.02f, 5.50f));
        entries.add(new CandleEntry(3, 8.25f, 4.33f, 5.50f, 6.50f));
        entries.add(new CandleEntry(4, 9.25f, 4.92f, 6.50f, 7.50f));
    }

//    public CandleChartFactory(ArrayList<ChartInfo> items) {
//        for (int i = 0; i < items.size(); i++) {
//            ChartInfo data = items.get(i);
//            entries.add(new CandleEntry(i, data.getHigh(), data.getLow(), data.getOpen(), data.getClose()));
//            xLabel.add(data.getCloseTime());
//        }
//    }


    // Sets up chart
    public CandleStickChart getChart(View chartView){
        CandleStickChart candleStickChart= (CandleStickChart) chartView;

        CandleDataSet dataset = new CandleDataSet(entries, "Monthly Data");

        XAxis xAxis = candleStickChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabel));
        xAxis.setTextColor(Color.WHITE);
        YAxis leftAxis = candleStickChart.getAxisLeft();
        YAxis rightAxis = candleStickChart.getAxisRight();
        leftAxis.setTextColor(Color.WHITE);
        rightAxis.setTextColor(Color.WHITE);

        dataset.setColor(Color.rgb(80, 80, 80));
        dataset.setShadowColorSameAsCandle(true);
        dataset.setShadowWidth(1.0f);
        dataset.setDecreasingColor(Color.RED);
        dataset.setDecreasingPaintStyle(Paint.Style.FILL);
        dataset.setIncreasingColor(Color.rgb(122, 242, 84));
        dataset.setIncreasingPaintStyle(Paint.Style.STROKE);
        dataset.setNeutralColor(Color.BLUE);
        dataset.setValueTextColor(Color.WHITE);
        dataset.setFormLineWidth(2.0f);
        CandleData cd = new CandleData(dataset);
        candleStickChart.setData(cd);

        Description description = new Description();
        description.setText(chartDescription);
        description.setTextColor(Color.WHITE);

        candleStickChart.setDescription(description);
        candleStickChart.getLegend().setEnabled(false);
        candleStickChart.invalidate();

        return candleStickChart;

    }


    // Set chart data methods

}