package com.pradeep.stockapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.pradeep.stockapp.service.MyJobService;
import com.pradeep.stockapp.view.StockAdapter;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements RoomItemClickListner {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private StockAdapter stockAdapter;
    private LinearLayout no_fav_stock;
    private LinearLayout fav_stocks;
    private FloatingActionButton add_new_stock;
    StockRepository stockRepository ;
    BroadcastReceiver broadcastReceiver;
    private int checked_sorted;

    private List<StockModel> all_stocks=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();
        setTitle("StockApp (WatchList)");
        stockRepository = new StockRepository(this);
        checked_sorted = 0;
        initView();
        initAutoUpdateService();
    }

    private void initAutoUpdateService() {
        MyJobService.stopMyJobService(this);
//        MyJobService.startMyJobService(this);
    }

    private void initView() {
        searchView = findViewById(R.id.search_view);
        recyclerView = findViewById(R.id.recycler_view);
        no_fav_stock = findViewById(R.id.no_fav_stock);
        fav_stocks = findViewById(R.id.fav_stocks);
        add_new_stock = findViewById(R.id.add_new_stock);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Timber.i("Received Broadcast");
                if (intent.getAction().equals(AppUtils.STOCK_UPDATED)){
                    getAllStocks();
                }
            }
        };
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
        findViewById(R.id.sort_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSortDialog();
            }
        });
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


    @Override
    protected void onResume() {
        super.onResume();

        try {
            this.registerReceiver(broadcastReceiver, new IntentFilter(AppUtils.STOCK_UPDATED));
        } catch (Exception ignored) {

        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        try {
            this.unregisterReceiver(broadcastReceiver);
        } catch (Exception ignored) {

        }
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
        Intent i = new Intent(this,StockDetailsCharts.class);
        i.putExtra(AppUtils.STOCK_SYMBOL_EXTRA,symbol);
        startActivity(i);
    }

    @Override
    public void onItemLongClick(final String symbol) {
//        AppUtils.showToast(this,"Long clicked");

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Delete Stock");
        alert.setMessage("Are you sure you want to delete?");
        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                stockRepository.deleteStock(symbol);
            }
        });
        alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // close dialog
                dialog.cancel();
            }
        });
        alert.show();
    }

    private void showSortDialog(){
        String[] grpname = {"Sort by added","Sort by % increase"};

        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        //alt_bld.setIcon(R.drawable.icon);
        alt_bld.setTitle("Sort by :");
        alt_bld.setSingleChoiceItems(grpname, checked_sorted, new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                checked_sorted = item;
                Toast.makeText(getApplicationContext(),
                        "Sorting by : "+grpname[item], Toast.LENGTH_SHORT).show();
                showSorted(item);
                dialog.dismiss();// dismiss the alertbox after chose option

            }
        });
        AlertDialog alert = alt_bld.create();
        alert.show();

    }

    private void showSorted(int num){
        if (num == 0)
        {
            getAllStocks();
        }
        else
        {
            stockRepository.getStocksSorted().observe(this, new Observer<List<StockModel>>() {
                @Override
                public void onChanged(@Nullable List<StockModel> stocks) {
                    initRecyclerView(stocks);
                }
            });
        }
    }


}
