package com.pradeep.stockapp.RetroAPIModels;

import com.pradeep.stockapp.graph.BaseResponse;

public class TickerDetails extends BaseResponse {

    TickerDetailsSummaryProfile summaryProfile;

    public TickerDetails(){}

    class TickerDetailsSummaryProfile {

        String zip;
        String sector;
        String longBusinessSummary;
        String state;
        String country;
        String website;
    }

    class price{

    }
}
