package com.pradeep.stockapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pradeep.stockapp.common.AppUtils;
import com.pradeep.stockapp.custom_components.RoomItemClickListner;
import com.pradeep.stockapp.custom_components.SimpleListDividerDecorator;
import com.pradeep.stockapp.room_db.StockModel;
import com.pradeep.stockapp.room_db.StockRepository;
import com.pradeep.stockapp.view.StockAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RoomItemClickListner {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private StockAdapter stockAdapter;
    private LinearLayout no_fav_stock;
    private LinearLayout fav_stocks;
    private FloatingActionButton add_new_stock;
    StockRepository stockRepository ;

    private List<StockModel> all_stocks=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("StockApp (WatchList)");
        stockRepository = new StockRepository(this);
        initView();
    }

    private void initView() {
        searchView = findViewById(R.id.search_view);
        recyclerView = findViewById(R.id.recycler_view);
        no_fav_stock = findViewById(R.id.no_fav_stock);
        fav_stocks = findViewById(R.id.fav_stocks);
        add_new_stock = findViewById(R.id.add_new_stock);
        getAllStocks();


        Drawable v = ContextCompat.getDrawable(this, R.drawable.list_divider_h);
        recyclerView.addItemDecoration(new SimpleListDividerDecorator(v, true));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (stockAdapter != null)
                    stockAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (stockAdapter != null)
                    stockAdapter.getFilter().filter(newText);
                return false;
            }
        });
        initFab();
    }

    private void initFab() {
        add_new_stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                visitAddNewStockScreen();
            }
        });
    }

    private void refresh(){
        stockRepository.getStocks().observe(this, new Observer<List<StockModel>>() {
            @Override
            public void onChanged(@Nullable List<StockModel> stocks) {
                notifyAdapter(stocks);
            }
        });
    }

    private void notifyAdapter(List<StockModel> stocks){
        this.all_stocks = stocks;
        stockAdapter.notifyDataSetChanged();
    }

    private void getAllStocks()
    {
        stockRepository.getStocks().observe(this, new Observer<List<StockModel>>() {
            @Override
            public void onChanged(@Nullable List<StockModel> stocks) {
                initRecyclerView(stocks);
            }
        });
    }

    private void initRecyclerView(List<StockModel> nameList) {
        this.all_stocks = nameList;
        stockAdapter = new StockAdapter(this, all_stocks,this);
        if (nameList.size()>0){
            fav_stocks.setVisibility(View.VISIBLE);
            no_fav_stock.setVisibility(View.INVISIBLE);
        }
        else
        {
            fav_stocks.setVisibility(View.INVISIBLE);
            no_fav_stock.setVisibility(View.VISIBLE);
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(stockAdapter);
    }

    private void visitAddNewStockScreen(){
//        stockRepository.insertStock("HTMedia", "BSE","HTMEDIA.BO",12.3,34.6);
//        stockRepository.insertStock("Vodafone", "BSE","IDEA.BO",70.1,77.8);
//        stockRepository.insertStock("Reliance", "BSE","RELIANCE.BO",234.8,237.2);
//        stockRepository.insertStock("TATA Motors", "NSE","TATAMOTORS.NS",234.0,228.7);

        startActivity(new Intent(this,SearchStocks.class));
    }

    @Override
    public void onItemClick(String symbol) {
        Intent i = new Intent(this,StockDetails.class);
        i.putExtra(AppUtils.STOCK_SYMBOL_EXTRA,symbol);
        startActivity(i);
    }
}
