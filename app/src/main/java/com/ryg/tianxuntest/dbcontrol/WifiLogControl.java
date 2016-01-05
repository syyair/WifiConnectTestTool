package com.ryg.tianxuntest.dbcontrol;

import java.util.List;

import greendao.WifiLog;
import greendao.WifiLogDao;

/**
 * Created by renyiguang on 2015/9/23.
 */
public class WifiLogControl extends BaseControl {

    private static WifiLogControl instance;

    private WifiLogDao wifiLogDao;

    private WifiLogControl(){
        super();
        wifiLogDao = daoSession.getWifiLogDao();
    }

    public static WifiLogControl getInstance(){
        if(instance == null){
            instance = new WifiLogControl();
        }
        return instance;
    }

    public void insert(WifiLog wifiLog){
        wifiLogDao.insertOrReplace(wifiLog);
    }

    public List<WifiLog> getAll(){
        return wifiLogDao.loadAll();
    }

    public void clear(){
        wifiLogDao.deleteAll();
    }

}
