package com.pradeep.stockapp.retrofit_api;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Response {
    @SerializedName("quotes")
    public ArrayList<RetroStockModel> stockModels;
}
