package com.pradeep.stockapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.pradeep.stockapp.common.AppUtils;
import com.pradeep.stockapp.custom_components.RoomItemClickListner;
import com.pradeep.stockapp.retrofit_api.ApiClient;
import com.pradeep.stockapp.retrofit_api.APIResponse;
import com.pradeep.stockapp.retrofit_api.RetroStockModel;
import com.pradeep.stockapp.retrofit_api.RetrofitInterface;
import com.pradeep.stockapp.room_db.StockModel;
import com.pradeep.stockapp.view.RetroStockAdapter;
import com.pradeep.stockapp.view.StockAdapter;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


public class SearchStocks extends AppCompatActivity implements RoomItemClickListner {
    private static final String TAG = "SearchStocks";


    private SearchView searchView_new;
    RetrofitInterface apiClient;


    private RecyclerView recyclerView;
    private RetroStockAdapter stockAdapter;
    private LinearLayout no_stock;
    private LinearLayout progress_bar;

    private List<RetroStockModel> all_stocks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_stocks);
        setTitle("Search New Stock");
        all_stocks=new ArrayList<>();
        apiClient = ApiClient.getClient().create(RetrofitInterface.class);
        initView();
    }

    private void initView() {
        searchView_new = findViewById(R.id.search_view_next);
        no_stock = findViewById(R.id.no_stock);
        no_stock = findViewById(R.id.no_stock);
        progress_bar = findViewById(R.id.progress_bar);

        recyclerView = findViewById(R.id.recycler_view);

        searchView_new.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.length()>3)
                {
                    callAPI(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length()>3)
                {
                    callAPI(newText);
                }
                return false;
            }
        });
        initRecyclerView();
    }

    private void callAPI(String query)
    {
        no_stock.setVisibility(View.GONE);
        progress_bar.setVisibility(View.VISIBLE);
        Call<APIResponse> call = apiClient.getStockDetail(query);
        call.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> apiResponseResponse) {

                if (apiResponseResponse.body()!=null) {
                    List<RetroStockModel> stockModels = apiResponseResponse.body().stockModels;
                    Timber.i("Found " + stockModels.size());
                    refresh(stockModels);
                    if (stockModels.size()>0)
                    {
                        no_stock.setVisibility(View.GONE);
                        progress_bar.setVisibility(View.GONE);

                    }
                    else
                    {
                        no_stock.setVisibility(View.VISIBLE);
                        progress_bar.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                AppUtils.showToast(SearchStocks.this,"Error calling API");
            }
        });
    }


    private void initRecyclerView() {
//        this.all_stocks = nameList;
        stockAdapter = new RetroStockAdapter(this, all_stocks,this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(stockAdapter);
    }

    private void refresh(List<RetroStockModel> stockModels){
        all_stocks = stockModels;
        initRecyclerView();
    }

    @Override
    public void onItemClick(String symbol) {
        Intent i = new Intent(this,StockDetails.class);
        i.putExtra(AppUtils.STOCK_SYMBOL_EXTRA,symbol);
        startActivity(i);
    }
}
