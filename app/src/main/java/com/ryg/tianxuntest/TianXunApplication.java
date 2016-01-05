package com.ryg.tianxuntest;

import android.app.Application;

/**
 * Created by renyiguang on 2015/9/23.
 */
public class TianXunApplication extends Application {

    private static  TianXunApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }


    public static TianXunApplication getInstance(){
        if(instance == null){
            synchronized (TianXunApplication.class){
                instance = new TianXunApplication();
            }
        }
        return instance;
    }


}
