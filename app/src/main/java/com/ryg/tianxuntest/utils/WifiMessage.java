package com.ryg.tianxuntest.utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by sunyingying on 2015/10/20.
 */
public class WifiMessage {

    private static WifiMessage wifiMessage;
    private String ssid;
    private String mac;
    private String password;
    private int type;

    public static WifiMessage getWifiMessage(){
        if(wifiMessage == null){
            wifiMessage = new WifiMessage();
        }
            return wifiMessage;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
