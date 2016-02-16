package com.ryg.tianxuntest.utils;

import android.os.Environment;
import android.util.Log;

import com.ryg.tianxuntest.constant.Constant;
import com.ryg.tianxuntest.dbcontrol.WifiLogControl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import greendao.WifiLog;

/**
 * Created by sunyingying on 2015/12/8.
 * 计算每次的链接时间和断开时间
 * 把结果写入到文件中
 */
public class GetConnectTime {

    private WifiLogControl wifiLogControl = WifiLogControl.getInstance();
    private  double onTime = 0, offTime = 0;
    FileWriter wifiFile;
    File fileResut;


    public void getConnectTime() throws IOException {
        wifiFile = null;
        List<WifiLog> list = wifiLogControl.getAll();
        WifiLog lastWifiLog= null;
        WifiLog wifiLog = null;
        Iterator<WifiLog> wifiLogIterator = list.iterator();
        while(wifiLogIterator.hasNext()){
            wifiLog = wifiLogIterator.next();
            if(lastWifiLog == null){
                lastWifiLog = wifiLog;
            }else {
                if (!lastWifiLog.getName().equals(wifiLog.getName())) {

                    if (lastWifiLog.getName().equals("WIFI_CONNECTED")) {
//                        onTime += wifiLog.getTime() - lastWifiLog.getTime();
                        onTime = wifiLog.getTime() - lastWifiLog.getTime();
                        wirteConnectResult(lastWifiLog.getData(),wifiLog.getData(),"Connected",onTime, lastWifiLog.getBSSID());
                        Log.e("wifitest", "ontime is =====================" + onTime);
                    } else {
//                        offTime += wifiLog.getTime() - lastWifiLog.getTime();
                        offTime = wifiLog.getTime() - lastWifiLog.getTime();
                        wirteConnectResult(lastWifiLog.getData(),wifiLog.getData(),"UnConnected",offTime, lastWifiLog.getBSSID());
                        Log.e("wifitest","offTime is =================="+offTime);
                    }
                    lastWifiLog = wifiLog;
                }
            }

        }

        //循环到最后的状态，用现在的时间减去最后的时间，就是最后状态持续的时间
        Date date = new Date();
        if(lastWifiLog.getName().equals("WIFI_CONNECTED")){
//            onTime += date.getTime() - wifiLog.getTime();
            onTime = date.getTime() - lastWifiLog.getTime();
            SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            wirteConnectResult(lastWifiLog.getData(),format.format(date),"Connected",onTime, lastWifiLog.getBSSID());
            Log.e("wifitest", "ontime is =====================" + onTime);
        }else{
//            offTime += date.getTime() - wifiLog.getTime();
            offTime = date.getTime() - lastWifiLog.getTime();
            SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            wirteConnectResult(lastWifiLog.getData(),format.format(date),"UnConnected",offTime,lastWifiLog.getBSSID());
            Log.e("wifitest", "offTime is ==================" + offTime);
        }
//        if(lastWifiLog.getName().equals(wifiLog.getName())) {
//            if (lastWifiLog.getName().equals("WIFI_CONNECTED")) {
//                onTime += wifiLog.getTime() - lastWifiLog.getTime();
//                onTime += date.getTime() - wifiLog.getTime();
//                Log.e("wifitest", "ontime is =====================" + onTime);
//            } else {
//                offTime += wifiLog.getTime() - lastWifiLog.getTime();
//                offTime += date.getTime() - wifiLog.getTime();
//                Log.e("wifitest", "offTime is ==================" + offTime);
//            }
//        }

        Log.e("wifitest", "OnTime is ===============" + onTime + "");
        Log.e("wifitest", "OffTime is ===============" + offTime + "");

        wifiFile.close();

//        if(onTime == 0){
//            percent = 0;
//            Log.e("wifitest","onTime is 0=================");
//        }
//        else if(offTime == 0){
//            percent = 100;
//            Log.e("wifitest","offTime is 0=================");
//        }
//        else{
//            percent = (double)(onTime - offTime) / offTime * 100;
//        }
//        percent = (double)(onTime - offTime) / offTime * 100;
//        DecimalFormat df = new DecimalFormat("#.00");
//        Log.e("wiyitest","在线时间比离线时间多出 " + df.format(percent) + "%");
//        Toast.makeText(WifiDetialActivity.this,"在线时间: " + onTime + "离线时间: " + offTime + "在线时间比离线时间多出 " + df.format(percent) + "%",Toast.LENGTH_LONG).show();

//        double test = (double)(0-9999)/9999*100;
//        Debug.log("(0-9999)/9999*100 ============== " + df.format(test)+"%");
    }



    public void wirteConnectResult(String dataBefor, String dataAfter, String status, double time, String mac) throws IOException {
        Debug.log("~~~~~~~~~~~~~~~~~~~~~~~~~~~action");
        File sdDir = new File(String.valueOf(Environment.getExternalStorageDirectory()));
        if(!sdDir.exists()){
            Debug.log("~~~~~~~~~~~~~~~~~~~~~~~dir");
            sdDir.mkdir();
        }

        if(Constant.isWritWifiResult){

            //每次都会调用吗，还是只会调用一次
            fileResut = new File(sdDir + "//ConnectResult_wifi.txt");
        }
        else if (Constant.isWirteTerminalResult){

            fileResut = new File(sdDir + "//ConnectResult_Terminal.txt");
        }

        if(!fileResut.exists()){
            Debug.log("~~~~~~~~~~~~~~~~~~~~~~~~~resultdir");
            fileResut.createNewFile();
        }

        Debug.log(fileResut.getAbsolutePath()+"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        try {
            if(wifiFile == null){
                Debug.log("~~~~~~~~~~~~~~~~~~~~~~~~"+wifiFile);
                wifiFile = new FileWriter(fileResut,true);

            }
            double s = time / 1000 ;
            Debug.log("~~~~~~~~~~~~~~~~~~~~~~~~write");
            wifiFile.write(dataBefor + "   " + dataAfter + "   " + status + "   " + mac + "\n" + s + "s \n");

        } catch (IOException e) {
            e.printStackTrace();
            Debug.log("~~~~~~~~~~~~~~~~~~~"+e.toString());
        }
        //这为什么不close 为什么在上面close
//        finally {
//            try {
//                wifiFile.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

    }
}
