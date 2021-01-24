package com.pradeep.stockapp.room_db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DaoAccess {

    @Insert
    Long insertTask(StockModel note);


    @Query("SELECT * FROM StockModel ORDER BY created_at desc")
    LiveData<List<StockModel>> fetchAllStocks();


    @Query("SELECT *,((curr_rate - rate)/rate) AS difference FROM StockModel ORDER BY difference desc")
    LiveData<List<StockModel>> fetchAllStocksSorted();


    @Query("SELECT * FROM StockModel WHERE id =:taskId")
    LiveData<StockModel> getStock(int taskId);


    @Query("SELECT * FROM StockModel WHERE symbol =:symbol")
    LiveData<StockModel> getStock(String symbol);


    @Update
    void updateStock(StockModel note);


    @Delete
    void deleteStock(StockModel note);
}