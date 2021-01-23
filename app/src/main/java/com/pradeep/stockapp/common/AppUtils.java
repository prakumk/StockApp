package com.pradeep.stockapp.common;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;

public class AppUtils {

    public static final String BASE_URL = "https://apidojo-yahoo-finance-v1.p.rapidapi.com/";
    public static final String HEADER_HOST = "apidojo-yahoo-finance-v1.p.rapidapi.com";
    public static final String HEADER_API_KEY = "a95692af65mshcf9eaebb246daf9p1b3720jsn9b11cfa3fbe0";


    public static final String STOCK_SYMBOL_EXTRA = "stock_symbol_extra";
    public static final String STOCK_UPDATED = "stocks_updated";

    public static Date getCurrentDateTime(){
        Date currentDate =  Calendar.getInstance().getTime();
        return currentDate;
    }

    public static String generateHash(String password) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
            md.update(password.getBytes());
            byte byteData[] = md.digest();
            String base64 = Base64.encodeToString(byteData, Base64.NO_WRAP);
            return base64;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void showToast(Context context,String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }



    public static void updateStocks(Context context){
        Intent intent = new Intent();
        intent.setAction(AppUtils.STOCK_UPDATED);
        context.sendBroadcast(intent);
    }
}
