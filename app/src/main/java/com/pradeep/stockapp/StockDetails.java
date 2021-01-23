package com.pradeep.stockapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.pradeep.stockapp.common.AppUtils;
import com.pradeep.stockapp.graph.FactoryMPAndroidChart;
import com.pradeep.stockapp.graph.TickerChart;
import com.pradeep.stockapp.retrofit_api.APIResponse;
import com.pradeep.stockapp.retrofit_api.ApiClient;
import com.pradeep.stockapp.retrofit_api.RetrofitInterface;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

public class StockDetails extends AppCompatActivity {
    String stock_symbol;
    LineChart chart;

    RetrofitInterface apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);
        stock_symbol  = getIntent().getStringExtra(AppUtils.STOCK_SYMBOL_EXTRA);
        apiClient = ApiClient.getClient().create(RetrofitInterface.class);
        initView();

    }

    private void initView() {
        chart = findViewById(R.id.chart);

        Single<TickerChart> chartSingle = apiClient.fetchTickerChart(stock_symbol);
        chartSingle.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<TickerChart>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(TickerChart tickerChart) {
                        setChart(chart,tickerChart);
                    }

                    @Override
                    public void onError(Throwable e) {
                        AppUtils.showToast(StockDetails.this,"Some Error occurred while loading data");
                    }
                });
    }



    private void setChart(@NonNull LineChart chart, @NonNull TickerChart ticker){
        chart.setTouchEnabled(false); // disable interactions
        chart.getLegend().setEnabled(false); // hide legend
        chart.setDescription(null);

        LineDataSet lineDataSet = new LineDataSet(FactoryMPAndroidChart.getChartDataSetStepOne(ticker), stock_symbol);
        lineDataSet.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
        lineDataSet.setLineWidth(2f);
        // hide circles, use dots
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setCircleColor(ContextCompat.getColor(this, R.color.colorPrimary));
        lineDataSet.setCircleRadius(1f);

        chart.getXAxis().setEnabled(false);
        chart.getAxisLeft().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.setMaxVisibleValueCount(0); // hide labels of points

        LineData data = new LineData(lineDataSet);
        chart.setData(data);
        chart.invalidate();
    }
}
