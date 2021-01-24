package com.pradeep.stockapp.retrofit_api;

import com.pradeep.stockapp.RetroAPIModels.TickerChart;
import com.pradeep.stockapp.RetroAPIModels.TickerDetailChart;
import com.pradeep.stockapp.RetroAPIModels.TickerDetails;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitInterface {

    @GET("auto-complete?region=IN")
    Call<APIResponse> getStockDetail(@Query("q") String search_text);


    @GET("stock/v2/get-chart?region=IN&lang=en")
    Single<TickerChart> fetchTickerChart(@Query("symbol") String tickerId,@Query("interval") String interval,@Query("range") String range);

    @GET("stock/v2/get-chart?region=IN&lang=en")
    Single<TickerDetailChart> fetchTickerChartDetails(@Query("symbol") String tickerId, @Query("interval") String interval, @Query("range") String range);


    @GET("stock/v2/get-statistics?region=IN&lang=en")
    Single<TickerDetails> fetchTickerDetails(@Query("symbol") String stock_name);

}