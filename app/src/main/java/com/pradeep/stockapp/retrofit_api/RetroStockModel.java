package com.pradeep.stockapp.retrofit_api;

import com.google.gson.annotations.SerializedName;

public class RetroStockModel {

    @SerializedName("exchange")
    private String exchange;

    @SerializedName("symbol")
    private String symbol;

    @SerializedName("longname")
    private String longname;

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getLongname() {
        return longname;
    }

    public void setLongname(String longname) {
        this.longname = longname;
    }
}
