package com.pradeep.stockapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.pradeep.stockapp.RetroAPIModels.TickerDetails;
import com.pradeep.stockapp.common.AppUtils;
import com.pradeep.stockapp.graph.FactoryMPAndroidChart;
import com.pradeep.stockapp.RetroAPIModels.TickerChart;
import com.pradeep.stockapp.retrofit_api.ApiClient;
import com.pradeep.stockapp.retrofit_api.RetrofitInterface;
import com.pradeep.stockapp.room_db.StockModel;
import com.pradeep.stockapp.room_db.StockRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class StockDetails extends AppCompatActivity {
    String stock_symbol;
    CandleStickChart candleStickChart;

    RetrofitInterface apiClient;
    LinearLayout graph_progress;
    LinearLayout graph_progress_internal;
    StockRepository stockRepository ;


    String chartDescription = "Monthly Data in BTC value";
    ArrayList<CandleEntry> entries = new ArrayList<>();
    ArrayList<String> xLabel = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);
        stock_symbol  = getIntent().getStringExtra(AppUtils.STOCK_SYMBOL_EXTRA);
        setTitle("Details ("+stock_symbol+")");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        apiClient = ApiClient.getClient().create(RetrofitInterface.class);
        stockRepository = new StockRepository(this);
        initView();

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void initView() {
        candleStickChart = findViewById(R.id.candle_stick_chart);
        graph_progress = findViewById(R.id.graph_progress);
        graph_progress_internal = findViewById(R.id.graph_progress_internal);

        entries.add(new CandleEntry(0, 4.62f, 2.02f, 2.70f, 4.13f));
        entries.add(new CandleEntry(1, 6.25f, 3.02f, 4.13f, 4.02f));
        entries.add(new CandleEntry(2, 7.25f, 3.52f, 4.02f, 5.50f));
        entries.add(new CandleEntry(3, 8.25f, 4.33f, 5.50f, 6.50f));
        entries.add(new CandleEntry(4, 9.25f, 4.92f, 6.50f, 7.50f));

        xLabel.add("first");
        xLabel.add("second");
        xLabel.add("third");
        xLabel.add("fourth");
        xLabel.add("fifth");

        setData(xLabel,entries);

        getChartData("1d","1mo");
    }

    public void setData(ArrayList<String> xLabel,ArrayList<CandleEntry> entries)
    {

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
    }

    public void getChartData(String interval,String range){
        graph_progress_internal.setVisibility(View.VISIBLE);
        Single<TickerChart> chartSingle = apiClient.fetchTickerChart(stock_symbol,interval,range);
        chartSingle.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<TickerChart>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(TickerChart tickerChart) {
                        graph_progress.setVisibility(View.GONE);
                        graph_progress_internal.setVisibility(View.GONE);
                        if(tickerChart.getError() == null) {
                            Pair<ArrayList<String>,ArrayList<CandleEntry>> data = tickerChart.getChartData();
                            setData(data.first,data.second);
                        }
                        else
                        {
                            AppUtils.showToast(StockDetails.this,"Some Error occurred while loading data");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        AppUtils.showToast(StockDetails.this,"Some Error occurred while loading data");
                    }
        });
    }



//    private void setChart(@NonNull LineChart chart, @NonNull TickerChart ticker){
//        chart.setTouchEnabled(true); // disable interactions
//        chart.getLegend().setEnabled(true); // hide legend
//        // show desc
//        Description description = new Description();
//        description.setText(String.format("Days/%s", ticker.getCurrency()));
//        chart.setDescription(description);
//
//        final List<Long> listDates = FactoryMPAndroidChart.getChartDataTimeDays(ticker);
//        LineDataSet lineDataSet = new LineDataSet(FactoryMPAndroidChart.getChartDataSetStepOne(ticker), stock_symbol);
//        lineDataSet.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
//        lineDataSet.setLineWidth(3f);
//        // hide circles, use dots
//        lineDataSet.setDrawCircleHole(false);
//        lineDataSet.setCircleColor(ContextCompat.getColor(this, R.color.colorPrimary));
//        lineDataSet.setCircleRadius(1.5f);
//
//        XAxis xAxis = chart.getXAxis();
//        xAxis.setDrawGridLines(false);
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//
//        ValueFormatter formatter = new ValueFormatter() {
//            @Override
//            public String getAxisLabel(float value, AxisBase axis) {
//                long timestamp = listDates.get((int) value);
//                Timber.d("timestamp = %d", timestamp);
//                Calendar cal = Calendar.getInstance();
//                cal.setTimeInMillis(timestamp * 1000);
//                return String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
//            }
//        };
//        xAxis.setGranularity(1f);
//        xAxis.setValueFormatter(formatter);
//
//        chart.getAxisLeft().setEnabled(false);
//        chart.getAxisRight().setDrawGridLines(false);
//        chart.setMaxVisibleValueCount(0); // hide labels of points
//
//        LineData data = new LineData(lineDataSet);
//        chart.setData(data);
//        chart.invalidate();
//    }

//    public void one_day(View view) {
//        getChartData("5m","1d");
//    }
//
//    public void five_days(View view) {
//        getChartData("60m","5d");
//    }
//
//    public void six_month(View view) {
//        getChartData("1d","6mo");
//    }
//
//    public void one_yr(View view) {
//        getChartData("1d","1y");
//    }
//
//    public void five_yr(View view) {
//        getChartData("1d","5y");
//    }

    public void watchlist(View view) {
        AppUtils.showToast(StockDetails.this,"Adding to watchlist");
        Single<TickerDetails> chartSingle = apiClient.fetchTickerDetails(stock_symbol);
        chartSingle.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<TickerDetails>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(TickerDetails tickerDetails) {
                        addToWatchList(tickerDetails);
                    }

                    @Override
                    public void onError(Throwable e) {
                        AppUtils.showToast(StockDetails.this,"Some Error occurred while loading data");
                    }
        });

    }

    public void addToWatchList(TickerDetails tickerDetails)
    {
        if(tickerDetails.getError() == null) {
//                            (String name, String type, String symbol, double rate, double curr_rate)
            LiveData<StockModel> d = stockRepository.getStock(stock_symbol);
            if(!d.hasActiveObservers())
                d.observe(this, new Observer<StockModel>() {
                    @Override
                    public void onChanged(@Nullable StockModel stockModel){
                        if(stockModel == null) {
                            stockRepository.insertStock(tickerDetails.getLongName(),tickerDetails.getExchange(),tickerDetails.getSymbol(),tickerDetails.getPreviousClose(),tickerDetails.getCurrentPrice());
                        } else {
                            stockModel.setRate(tickerDetails.getPreviousClose());
                            stockModel.setCurr_rate(tickerDetails.getCurrentPrice());
                            stockRepository.updateStock(stockModel);
                        }
                        AppUtils.updateStocks(StockDetails.this);
                        AppUtils.showToast(StockDetails.this,"Successfully Added to watchlist");
                        d.removeObserver(this);
                    }
                });
        }
        else
        {
            AppUtils.showToast(StockDetails.this,"Unable to mark to watchlist");
        }
    }


}
