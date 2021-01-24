package com.pradeep.stockapp.graph;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.data.Entry;
import com.pradeep.stockapp.RetroAPIModels.TickerChart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FactoryMPAndroidChart {

    @NonNull
    public static List<Entry> getChartDataSetStepOne(@NonNull TickerChart ticker){
        List<Entry> result = new ArrayList<>();
        int i = 0;
//        for (Map.Entry<Long, Float> entry : ticker.getChartData().entrySet()) {
//            result.add(new Entry(i, entry.getValue()));
//            ++i;
//        }
        return result;
    }

    @NonNull
    public static List<Long> getChartDataTimeDays(@NonNull TickerChart ticker) {
        List<Long> result = new ArrayList<>();
//        for (Map.Entry<Long, Float> entry : ticker.getChartData().entrySet())
//            result.add(entry.getKey());
        return result;
    }
}
