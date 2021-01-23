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


    @Query("SELECT * FROM StockModel WHERE id =:taskId")
    LiveData<StockModel> getStock(int taskId);


    @Update
    void updateStock(StockModel note);


    @Delete
    void deleteStock(StockModel note);
}