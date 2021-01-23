package com.pradeep.stockapp.RetroAPIModels;

import com.pradeep.stockapp.graph.BaseResponse;

public class TickerDetails extends BaseResponse {

    String symbol;

    Price price;

    SummaryDetails summaryDetail;

    public TickerDetails(){}

    class Price{
        String exchange;
        String longName;
        String currencySymbol;
    }

    class SummaryDetails{
        PreviousClose previousClose;
        Bid bid;
    }

    class PreviousClose{
          double raw;
    }


    class Bid{
        double raw;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getExchange(){
        return price.exchange;
    }

    public String getLongName(){
        return price.longName;
    }

    public String getCurrencySymbol(){
        return price.currencySymbol;
    }


    public double getPreviousClose(){
        return summaryDetail.previousClose.raw;
    }


    public double getCurrentPrice(){
        return summaryDetail.bid.raw;
    }

}
