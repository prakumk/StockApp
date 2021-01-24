package com.pradeep.stockapp;

import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.guannan.chartmodule.chart.KMasterChartView;
import com.guannan.chartmodule.chart.KSubChartView;
import com.guannan.chartmodule.chart.MarketFigureChart;
import com.guannan.chartmodule.data.ExtremeValue;
import com.guannan.chartmodule.data.KLineToDrawItem;
import com.guannan.chartmodule.data.SubChartData;
import com.guannan.chartmodule.helper.ChartDataSourceHelper;
import com.guannan.chartmodule.helper.TechParamType;
import com.guannan.chartmodule.inter.IChartDataCountListener;
import com.guannan.chartmodule.inter.IPressChangeListener;
import com.guannan.simulateddata.LocalUtils;
import com.guannan.simulateddata.entity.KLineItem;
import com.guannan.simulateddata.parser.KLineParser;
import com.pradeep.stockapp.RetroAPIModels.TickerDetailChart;
import com.pradeep.stockapp.common.AppUtils;
import com.pradeep.stockapp.retrofit_api.ApiClient;
import com.pradeep.stockapp.retrofit_api.RetrofitInterface;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class StockDetailsCharts extends AppCompatActivity
    implements IChartDataCountListener<List<KLineToDrawItem>>, IPressChangeListener,
    RadioGroup.OnCheckedChangeListener {

  private ChartDataSourceHelper mHelper;
  private KMasterChartView mKLineChartView;
  private KSubChartView mVolumeView;
  private MarketFigureChart mMarketFigureChart;
  private ProgressBar mProgressBar;

  private int MAX_COLUMNS = 160;
  private int MIN_COLUMNS = 20;
  private KSubChartView mMacdView;

  String stock_symbol;
  RetrofitInterface apiClient;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_stock_details_charts);
    stock_symbol  = getIntent().getStringExtra(AppUtils.STOCK_SYMBOL_EXTRA);
    setTitle("Details ("+stock_symbol+")");
    apiClient = ApiClient.getClient().create(RetrofitInterface.class);
    initViews();

    mMarketFigureChart = findViewById(R.id.chart_container);

    mKLineChartView = new KMasterChartView(this);
    mMarketFigureChart.addChildChart(mKLineChartView, 200);

    mVolumeView = new KSubChartView(this);
    mMarketFigureChart.addChildChart(mVolumeView, 100);

    mMacdView = new KSubChartView(this);
    mMarketFigureChart.addChildChart(mMacdView, 100);

    mMarketFigureChart.setPressChangeListener(this);
  }

//  private void initialData(final String json) {
//    mProgressBar.setVisibility(View.VISIBLE);
//    new Handler().postDelayed(new Runnable() {
//      @Override
//      public void run() {
//        initData(json);
//      }
//    }, 500);
//  }

  private void initViews() {

    mProgressBar = findViewById(R.id.progress_circular);
    RadioGroup radioGroup = findViewById(R.id.rbtn_group);
    radioGroup.setOnCheckedChangeListener(this);
    radioGroup.check(R.id.rbtn_15);
    getChartData("5m","1d");
  }

  public void getChartData(String interval,String range){
    mProgressBar.setVisibility(View.VISIBLE);
    Single<TickerDetailChart> chartSingle = apiClient.fetchTickerChartDetails(stock_symbol,interval,range);
    chartSingle.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new SingleObserver<TickerDetailChart>() {
              @Override
              public void onSubscribe(Disposable d) {
              }

              @Override
              public void onSuccess(TickerDetailChart tickerChart) {
                mProgressBar.setVisibility(View.GONE);
                if(tickerChart.getError() == null) {
                  initData(tickerChart.getChartData());
//                  setChart(chart, tickerChart);
                }
                else
                {
                  AppUtils.showToast(StockDetailsCharts.this,"Some Error occurred while loading data");
                }
              }

              @Override
              public void onError(Throwable e) {
                AppUtils.showToast(StockDetailsCharts.this,"Some Error occurred while loading data");
              }
            });
  }


  public void initData(ArrayList<KLineItem> json) {

    if (mHelper == null) {
      mHelper = new ChartDataSourceHelper(this);
    }
    mProgressBar.setVisibility(View.GONE);
    mHelper.initKDrawData(json, mKLineChartView, mVolumeView, mMacdView);
  }

  @Override
  public void onCheckedChanged(RadioGroup group, int checkedId) {
    switch (checkedId) {
      case R.id.rbtn_15:
//        initialData("slw_k.json");
        break;
      case R.id.rbtn_1h:
//        initialData("geli.json");
        break;
      case R.id.rbtn_4h:
//        initialData("maotai.json");
        break;
      case R.id.rbtn_1d:
//        initialData("pingan.json");
        break;
    }
  }

  public void initData(String json) {

    String kJson = LocalUtils.getFromAssets(this, json);

    KLineParser parser = new KLineParser(kJson);
    parser.parseKlineData();

    if (mHelper == null) {
      mHelper = new ChartDataSourceHelper(this);
    }
    mProgressBar.setVisibility(View.GONE);
    mHelper.initKDrawData(parser.klineList, mKLineChartView, mVolumeView, mMacdView);
  }


  @Override
  public void onReady(List<KLineToDrawItem> data, ExtremeValue extremeValue,
                      SubChartData subChartData) {
    mKLineChartView.initData(data, extremeValue,subChartData);
    mVolumeView.initData(data, extremeValue, TechParamType.VOLUME,subChartData);
    mMacdView.initData(data, extremeValue, TechParamType.MACD,subChartData);
  }


  @Override
  public void onChartTranslate(MotionEvent me, float dX) {
    if (mHelper != null) {
      mHelper.initKMoveDrawData(dX, ChartDataSourceHelper.SourceType.MOVE);
    }
  }


  @Override
  public void onChartFling(float distanceX) {
    if (mHelper != null) {
      mHelper.initKMoveDrawData(distanceX, ChartDataSourceHelper.SourceType.FLING);
    }
  }

  @Override
  public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
    ChartDataSourceHelper.K_D_COLUMNS = (int) (ChartDataSourceHelper.K_D_COLUMNS / scaleX);
    ChartDataSourceHelper.K_D_COLUMNS =
        Math.max(MIN_COLUMNS, Math.min(MAX_COLUMNS, ChartDataSourceHelper.K_D_COLUMNS));
    if (mHelper != null) {
      mHelper.initKMoveDrawData(0, ChartDataSourceHelper.SourceType.SCALE);
    }
  }
}
