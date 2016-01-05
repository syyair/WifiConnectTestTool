package com.ryg.tianxuntest.dbcontrol;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.ryg.tianxuntest.TianXunApplication;
import com.ryg.tianxuntest.constant.Constant;

import greendao.DaoMaster;
import greendao.DaoSession;

/**
 * Created by renyiguang on 2015/8/13.
 */
public abstract class BaseControl {
    protected SQLiteDatabase db;
    protected DaoMaster daoMaster;
    protected DaoSession daoSession;

    public BaseControl() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(TianXunApplication.getInstance(), Constant.DB_NAME, null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public void close(){
        db.close();
    }


}
