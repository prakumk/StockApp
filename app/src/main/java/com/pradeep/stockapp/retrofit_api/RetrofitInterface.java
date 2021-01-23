package com.pradeep.stockapp.retrofit_api;

import com.pradeep.stockapp.graph.TickerChart;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitInterface {

    @GET("auto-complete?region=IN")
    Call<APIResponse> getStockDetail(@Query("q") String search_text);


    @GET("stock/v2/get-chart?interval=1d&region=IN&lang=en&range=1mo")
    Single<TickerChart> fetchTickerChart(@Query("symbol") String tickerId);
}