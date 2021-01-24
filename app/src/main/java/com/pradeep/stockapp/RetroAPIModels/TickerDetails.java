package com.pradeep.stockapp.RetroAPIModels;

import com.pradeep.stockapp.graph.BaseResponse;

public class TickerDetails extends BaseResponse {

    String symbol;

    Price price;

    SummaryDetails summaryDetail;

    FinancialData financialData;

    public TickerDetails(){}

    class Price{
        String exchange;
        String longName;
        String currencySymbol;
    }

    class SummaryDetails{
        PreviousClose previousClose;
        Open open;
        Volume volume;
        DayLow dayLow;
        DayHigh dayHigh;
    }

    class FinancialData{
        CurrentPrice currentPrice;
    }

    class CurrentPrice{
          double raw;
    }

    class PreviousClose{
          double raw;
    }


    class Open{
        double raw;
    }

    class DayLow {
        double raw;
    }

    class DayHigh{
        double raw;
    }

    class Volume{
        String longFmt;
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
        return financialData.currentPrice.raw;
    }


    public double getDayHigh(){
        return summaryDetail.dayHigh.raw;
    }

    public double getDayLow(){
        return summaryDetail.dayLow.raw;
    }


    public String getVolume(){
        return summaryDetail.volume.longFmt;
    }


}
