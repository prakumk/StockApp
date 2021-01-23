package com.pradeep.stockapp.retrofit_api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitInterface {

    @GET("auto-complete?region=IN")
    Call<Response> getStockDetail(@Query("q") String search_text);
}