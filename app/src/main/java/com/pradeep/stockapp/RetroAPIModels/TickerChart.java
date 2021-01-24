package com.pradeep.stockapp.RetroAPIModels;

import android.text.format.DateFormat;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.mikephil.charting.data.CandleEntry;
import com.google.gson.Gson;
import com.guannan.simulateddata.entity.KLineItem;
import com.pradeep.stockapp.graph.BaseResponse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import timber.log.Timber;

public class TickerChart extends BaseResponse {

    private Chart chart;

    ArrayList<String> xLabels = new ArrayList<>();
    ArrayList<CandleEntry> data = new ArrayList<>();


    public Pair<ArrayList<String>,ArrayList<CandleEntry>> getChartData(){

        if (chart.error == null) {
            if (data.size() == 0) {
                for (int i=0;i<chart.result.get(0).timestamp.size();i++){
                    xLabels.add(getDate(chart.result.get(0).timestamp.get(i)));

                    //High low open close
                    CandleEntry candleEntry = new CandleEntry(chart.result.get(0).timestamp.get(i),chart.result.get(0).indicators.quote.get(0).high.get(i),chart.result.get(0).indicators.quote.get(0).low.get(i),chart.result.get(0).indicators.quote.get(0).open.get(i),chart.result.get(0).indicators.quote.get(0).close.get(i));
                    data.add(candleEntry);
                }
            }
        }
        return new Pair<>(xLabels,data);
    }

    public String getDate(Long time){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("yyyy-MM-dd", cal).toString();
        return date;
    }

    class Chart {
        String error;
        List<TickerDetailChart.ResultItem> result;
    }

    class ResultItem {
        //        TickerChartMeta meta;
        List<Long> timestamp;
        TickerDetailChart.Indicators indicators;
    }
//
//    class TickerChartMeta {
//        String currency;
//    }

    class Indicators {
        List<TickerDetailChart.Quote> quote;
    }

    class Quote {
        List<Float> open;
        List<Float> high;
        List<Float> close;
        List<Long> volume;
        List<Float> low;
    }
}
