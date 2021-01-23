package com.pradeep.stockapp.room_db;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import com.pradeep.stockapp.common.AppUtils;

import java.util.List;

public class StockRepository {

    private String DB_NAME = "fav_stock_data";

    private StockDatabase stockDatabase;
    public StockRepository(Context context) {
        stockDatabase = Room.databaseBuilder(context, StockDatabase.class, DB_NAME).build();
    }

    public void insertTask(String name,
                           String type,String symbol,double rate,double curr_rate) {

        StockModel stock = new StockModel();
        stock.setName(name);
        stock.setType(type);
        stock.setRate(rate);
        stock.setSymbol(symbol);
        stock.setCurr_rate(curr_rate);
        stock.setCreatedAt(AppUtils.getCurrentDateTime());
        stock.setModifiedAt(AppUtils.getCurrentDateTime());

        insertStock(stock);
    }

    public void insertStock(final StockModel stock) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                stockDatabase.daoAccess().insertTask(stock);
                return null;
            }
        }.execute();
    }

    public void updateStock(final StockModel stock) {
        stock.setModifiedAt(AppUtils.getCurrentDateTime());

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                stockDatabase.daoAccess().updateStock(stock);
                return null;
            }
        }.execute();
    }

    public void deleteStock(final int id) {
        final LiveData<StockModel> task = getStock(id);
        if(task != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    stockDatabase.daoAccess().deleteStock(task.getValue());
                    return null;
                }
            }.execute();
        }
    }

    public void deleteTask(final StockModel stock) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                stockDatabase.daoAccess().deleteStock(stock);
                return null;
            }
        }.execute();
    }

    public LiveData<StockModel> getStock(int id) {
        return stockDatabase.daoAccess().getStock(id);
    }

    public LiveData<List<StockModel>> getStocks() {
        return stockDatabase.daoAccess().fetchAllStocks();
    }
}