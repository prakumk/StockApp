package com.pradeep.stockapp.service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.pradeep.stockapp.RetroAPIModels.TickerDetails;
import com.pradeep.stockapp.StockDetailsCharts;
import com.pradeep.stockapp.common.AppUtils;
import com.pradeep.stockapp.retrofit_api.ApiClient;
import com.pradeep.stockapp.retrofit_api.RetrofitInterface;
import com.pradeep.stockapp.room_db.StockModel;
import com.pradeep.stockapp.room_db.StockRepository;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MyJobService extends JobService {

    private static final String TAG = "MyJobService";

    public static void startMyJobService(Context context)
    {
        FirebaseJobDispatcher mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        mDispatcher.cancelAll();
        Job myJob = mDispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag(TAG)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(5, 30))
                .setLifetime(Lifetime.FOREVER)
                .setReplaceCurrent(false)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .build();
        mDispatcher.mustSchedule(myJob);
    }

    public static void stopMyJobService(Context context)
    {
        FirebaseJobDispatcher mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        mDispatcher.cancelAll();
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        final StockRepository stockRepository = new StockRepository(this);
        stockRepository.getStocks().observeForever(new Observer<List<StockModel>>() {
            @Override
            public void onChanged(List<StockModel> stockModels) {
                refresh(stockRepository,stockModels);
            }
        });

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        Log.d(TAG, "Job cancelled!");
        return false;
    }

    private void refresh(StockRepository stockRepository,List<StockModel> stockModels){

        final RetrofitInterface apiClient = ApiClient.getClient().create(RetrofitInterface.class);
        for(StockModel m : stockModels)
            getStockDetails(stockRepository,apiClient,m);
    }


    public void getStockDetails(StockRepository stockRepository,RetrofitInterface apiClient,StockModel stockModel){
        Single<TickerDetails> chartSingle = apiClient.fetchTickerDetails(stockModel.getSymbol());
        chartSingle.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<TickerDetails>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(TickerDetails tickerDetails) {
                        Timber.i("Updated Stock Details for "+tickerDetails.getSymbol());
                        stockModel.setRate(tickerDetails.getPreviousClose());
                        stockModel.setCurr_rate(tickerDetails.getCurrentPrice());
                        stockRepository.updateStock(stockModel);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }
}
