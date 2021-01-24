package com.pradeep.stockapp.RetroAPIModels;


import android.text.format.DateFormat;

import com.guannan.simulateddata.entity.KLineItem;
import com.pradeep.stockapp.graph.BaseResponse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class TickerDetailChart extends BaseResponse {

    private Chart chart;
//    private TreeMap<Long, Float> points = new TreeMap<>();
//
//    @Nullable
//    public String getCurrency(){
//        try {
//            return chart.result.get(0).meta.currency;
//        } catch (NullPointerException e){
//            Timber.e(e);
//            return null;
//        }
//    }
//
//    @NonNull
//    public TreeMap<Long, Float> getPoints(){
//
//        if (!points.isEmpty())
//            return points;
//
//        List<Long> timestamps = getTimestampList();
//        if (timestamps == null) return points;
//        List<Float> values = getValueList();
//        if (values == null) return points;
//
//        for (int i = 0; i < timestamps.size(); i++)
//            points.put(timestamps.get(i), values.get(i));
//
//        return points;
//    }
//
//    @Nullable
//    private List<Long> getTimestampList(){
//        try {
//            return chart.result.get(0).timestamp;
//        } catch (NullPointerException e){
//            Timber.e(e);
//            return null;
//        }
//    }
//
//    @Nullable
//    private List<Float> getValueList(){
//        try {
//            return chart.result.get(0).indicators.adjclose.get(0).adjclose;
//        } catch (NullPointerException e){
//            Timber.e(e);
//            return null;
//        }
//    }
//
//
//    @NonNull
//    public TreeMap<Long, Float> getChartData(){
//        Timber.d("getChartData = %s", new Gson().toJson(new TreeMap<>(Collections.unmodifiableMap(getPoints()))));
//        return new TreeMap<>(Collections.unmodifiableMap(getPoints()));
//    }
//

    ArrayList<KLineItem> data = new ArrayList<>();


    public ArrayList<KLineItem> getChartData(){
        if (chart.error == null) {
            if (data.size() == 0) {
                for (int i=0;i<chart.result.get(0).timestamp.size();i++){
                    KLineItem kLineItem = new KLineItem();
                    kLineItem.day = getDate(chart.result.get(0).timestamp.get(i));
                    kLineItem.close = chart.result.get(0).indicators.quote.get(0).close.get(i);
                    kLineItem.open = chart.result.get(0).indicators.quote.get(0).open.get(i);
                    kLineItem.high = chart.result.get(0).indicators.quote.get(0).high.get(i);
                    kLineItem.low = chart.result.get(0).indicators.quote.get(0).low.get(i);
                    kLineItem.volume = chart.result.get(0).indicators.quote.get(0).volume.get(i);
                    data.add(kLineItem);
                }
            }
        }
        return data;
    }

    public String getDate(Long time){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("yyyy-MM-dd", cal).toString();
        return date;
    }

    class Chart {
        String error;
        List<ResultItem> result;
    }

    class ResultItem {
//        TickerChartMeta meta;
        List<Long> timestamp;
        Indicators indicators;
    }
//
//    class TickerChartMeta {
//        String currency;
//    }

    class Indicators {
        List<Quote> quote;
    }

    class Quote {
        List<Float> open;
        List<Float> high;
        List<Float> close;
        List<Long> volume;
        List<Float> low;
    }
}
