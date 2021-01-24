package com.pradeep.stockapp;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.pradeep.stockapp.RetroAPIModels.TickerDetails;
import com.pradeep.stockapp.common.AppUtils;
import com.pradeep.stockapp.retrofit_api.ApiClient;
import com.pradeep.stockapp.retrofit_api.RetrofitInterface;
import com.pradeep.stockapp.room_db.StockModel;
import com.pradeep.stockapp.room_db.StockRepository;

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
  private FloatingActionButton fav_stock;

  private int MAX_COLUMNS = 160;
  private int MIN_COLUMNS = 20;
  private KSubChartView mMacdView;

  String stock_symbol;
  RetrofitInterface apiClient;
  TickerDetails tickerDetails;

  private TextView tv_price;
  private TextView high_low;
  private TextView high;
  private TextView low;
  private TextView volume;
  StockRepository stockRepository ;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_stock_details_charts);
    stock_symbol  = getIntent().getStringExtra(AppUtils.STOCK_SYMBOL_EXTRA);
    setTitle("Details ("+stock_symbol+")");
    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    apiClient = ApiClient.getClient().create(RetrofitInterface.class);
    stockRepository = new StockRepository(this);
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



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    tv_price = findViewById(R.id.tv_price);
    high_low = findViewById(R.id.high_low);
    high = findViewById(R.id.high);
    low = findViewById(R.id.low);
    volume = findViewById(R.id.volume);
    fav_stock = findViewById(R.id.fav_stock);
    getStockDetails();

    getChartData("5m","1d");
  }

  public void getStockDetails(){
      Single<TickerDetails> chartSingle = apiClient.fetchTickerDetails(stock_symbol);
      chartSingle.subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(new SingleObserver<TickerDetails>() {
                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onSuccess(TickerDetails tickerDetails) {
                    initUpperView(tickerDetails);
                }

                @Override
                public void onError(Throwable e) {
                  AppUtils.showToast(StockDetailsCharts.this,"Some Error occurred while loading data");
                }
              });
  }

  private void initUpperView(TickerDetails tickerDetails) {
        this.tickerDetails = tickerDetails;
        tv_price.setText(tickerDetails.getCurrencySymbol()+String.format("%.2f", tickerDetails.getCurrentPrice()));


        if (tickerDetails.getCurrentPrice()>tickerDetails.getPreviousClose()){
          tv_price.setTextColor(Color.parseColor("#73BD73"));
        }
        else if(tickerDetails.getCurrentPrice()==tickerDetails.getPreviousClose()){
          tv_price.setTextColor(Color.parseColor("#F7D438"));
        }
        else
        {
          tv_price.setTextColor(Color.parseColor("#E35E59"));
        }


        double diff = tickerDetails.getCurrentPrice()-tickerDetails.getPreviousClose();
        double per = 0;
        try {
          per = diff * 100 / tickerDetails.getPreviousClose();
        }
        catch (Exception e){

        }
        if(diff>=0){
          String text = "+"+String.format("%.2f", diff)+" ("+String.format("%.2f", Math.abs(per))+"%)";
          high_low.setText(text);
        }
        else
        {
          String text = ""+String.format("%.2f", diff)+" ("+String.format("%.2f", Math.abs(per))+"%)";
          high_low.setText(text);
        }
        high.setText("High : "+String.format("%.2f", tickerDetails.getDayHigh()));
        low.setText("Low : "+String.format("%.2f", tickerDetails.getDayLow()));
        volume.setText("Vol : "+tickerDetails.getVolume());
        fav_stock.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            addToWatchList();
          }
        });
  }


  public void addToWatchList()
  {
    if(tickerDetails!=null && tickerDetails.getError() == null) {
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
            AppUtils.updateStocks(StockDetailsCharts.this);
            AppUtils.showToast(StockDetailsCharts.this,"Successfully Added to watchlist");
            d.removeObserver(this);
          }
        });
    }
    else
    {
      AppUtils.showToast(StockDetailsCharts.this,"Unable to mark to watchlist");
    }
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
        getChartData("5m","1d");
        break;
      case R.id.rbtn_1h:
        getChartData("5m","5d");
        break;
      case R.id.rbtn_4h:
        getChartData("1d","6mo");
        break;
      case R.id.rbtn_1d:
        getChartData("1d","1y");
      case R.id.rbtn_5y:
        getChartData("1d","5y");
        break;
    }
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
//    ChartDataSourceHelper.K_D_COLUMNS = (int) (ChartDataSourceHelper.K_D_COLUMNS / scaleX);
//    ChartDataSourceHelper.K_D_COLUMNS =
//        Math.max(MIN_COLUMNS, Math.min(MAX_COLUMNS, ChartDataSourceHelper.K_D_COLUMNS));
//    if (mHelper != null) {
//      mHelper.initKMoveDrawData(0, ChartDataSourceHelper.SourceType.SCALE);
//    }
  }
}
