package com.ryg.tianxuntest.dbcontrol;

import java.util.List;

import greendao.WifiLog;
import greendao.WifiLogDao;

/**
 * Created by renyiguang on 2015/9/23.
 * 对数据库的操作
 * 数据库中存放的是热点的信息和链接状态，状态发生变化时的时间
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
