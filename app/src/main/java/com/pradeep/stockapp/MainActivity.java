package com.pradeep.stockapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.pradeep.stockapp.common.DummyData;
import com.pradeep.stockapp.domain.Name;
import com.pradeep.stockapp.view.StockAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private StockAdapter stockAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        searchView = findViewById(R.id.search_view);
        recyclerView = findViewById(R.id.recycler_view);
        initRecyclerView(DummyData.getDummyName());

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                stockAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                stockAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void initRecyclerView(List<Name> nameList) {
        stockAdapter = new StockAdapter(this, nameList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(stockAdapter);
    }
}
