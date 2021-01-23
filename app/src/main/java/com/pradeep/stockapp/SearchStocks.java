package com.pradeep.stockapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;


import com.pradeep.stockapp.common.AppUtils;
import com.pradeep.stockapp.retrofit_api.ApiClient;
import com.pradeep.stockapp.retrofit_api.APIResponse;
import com.pradeep.stockapp.retrofit_api.RetroStockModel;
import com.pradeep.stockapp.retrofit_api.RetrofitInterface;
import com.pradeep.stockapp.view.StockAdapter;

import java.io.Console;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


public class SearchStocks extends AppCompatActivity {
    private static final String TAG = "SearchStocks";


    private SearchView searchView_new;
    RetrofitInterface apiClient;


    private RecyclerView recyclerView;
    private StockAdapter stockAdapter;
    private LinearLayout no_stock;
    private LinearLayout stocks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_stocks);
        apiClient = ApiClient.getClient().create(RetrofitInterface.class);
        initView();
    }

    private void initView() {
        searchView_new = findViewById(R.id.search_view_next);
        no_stock = findViewById(R.id.no_stock);
        no_stock = findViewById(R.id.no_stock);

        recyclerView = findViewById(R.id.recycler_view);

        searchView_new.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.length()>3)
                {
                    callAPI(query);
//                    stockAdapter.getFilter().filter(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length()>3)
                {
                    callAPI(newText);
//                    stockAdapter.getFilter().fil ter(query);
                }
                return false;
            }
        });
    }

    private void callAPI(String query)
    {
        Call<APIResponse> call = apiClient.getStockDetail(query);
        call.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> apiResponseResponse) {

                if (apiResponseResponse.body()!=null) {
                    List<RetroStockModel> stockModels = apiResponseResponse.body().stockModels;
                    Timber.i("Found " + stockModels.size());
                    if (stockModels.size()>0)
                    {
                        no_stock.setVisibility(View.GONE);

                    }
                    else
                    {
                        no_stock.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                AppUtils.showToast(SearchStocks.this,"Error calling API");
            }
        });
    }
}
