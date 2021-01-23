package com.pradeep.stockapp.room_db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {StockModel.class}, version = 1, exportSchema = false)
public abstract class StockDatabase extends RoomDatabase {

    public abstract DaoAccess daoAccess();
}