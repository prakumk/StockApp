package com.pradeep.stockapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class App extends Application {

    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }

    }

    @NonNull
    public static App getInstance(){
        return instance;
    }

    private static Map<String, String> currencySign = new HashMap<String, String>() {{
        put("INR", "₹");
    }};

    public static String getCurrencySign(@Nullable String currency){
        return currencySign.containsKey(currency) ? currencySign.get(currency) : currency;
    }

}
