package com.pradeep.stockapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.pradeep.stockapp.common.AppUtils;
import com.pradeep.stockapp.graph.FactoryMPAndroidChart;
import com.pradeep.stockapp.graph.TickerChart;
import com.pradeep.stockapp.retrofit_api.APIResponse;
import com.pradeep.stockapp.retrofit_api.ApiClient;
import com.pradeep.stockapp.retrofit_api.RetrofitInterface;

import java.util.Calendar;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

public class StockDetails extends AppCompatActivity {
    String stock_symbol;
    LineChart chart;

    RetrofitInterface apiClient;
    LinearLayout graph_progress;

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
        graph_progress = findViewById(R.id.graph_progress);

        Single<TickerChart> chartSingle = apiClient.fetchTickerChart(stock_symbol);
        chartSingle.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<TickerChart>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(TickerChart tickerChart) {
                        graph_progress.setVisibility(View.GONE);
                        if(tickerChart.getError() == null) {
                            setChart(chart, tickerChart);
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



    private void setChart(@NonNull LineChart chart, @NonNull TickerChart ticker){
        chart.setTouchEnabled(false); // disable interactions
        chart.getLegend().setEnabled(false); // hide legend
        // show desc
        Description description = new Description();
        description.setText(String.format("Days/%s", ticker.getCurrency()));
        chart.setDescription(description);

        final List<Long> listDates = FactoryMPAndroidChart.getChartDataTimeDays(ticker);
        LineDataSet lineDataSet = new LineDataSet(FactoryMPAndroidChart.getChartDataSetStepOne(ticker), stock_symbol);
        lineDataSet.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
        lineDataSet.setLineWidth(3f);
        // hide circles, use dots
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setCircleColor(ContextCompat.getColor(this, R.color.colorPrimary));
        lineDataSet.setCircleRadius(1.5f);

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                long timestamp = listDates.get((int) value);
                Timber.d("timestamp = %d", timestamp);
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(timestamp * 1000);
                return String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            }
        };
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatter);

        chart.getAxisLeft().setEnabled(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.setMaxVisibleValueCount(0); // hide labels of points

        LineData data = new LineData(lineDataSet);
        chart.setData(data);
        chart.invalidate();
    }
}
