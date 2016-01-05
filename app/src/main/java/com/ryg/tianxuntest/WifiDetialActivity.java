package com.ryg.tianxuntest;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ryg.tianxuntest.constant.Constant;
import com.ryg.tianxuntest.dbcontrol.WifiLogControl;
import com.ryg.tianxuntest.interfaces.ConnectedListener;
import com.ryg.tianxuntest.interfaces.ConnectedStatusListener;
import com.ryg.tianxuntest.receiver.WifiConnectReceiver;
import com.ryg.tianxuntest.utils.Debug;
import com.ryg.tianxuntest.utils.FileUtil;
import com.ryg.tianxuntest.utils.GetConnectTime;
import com.ryg.tianxuntest.utils.WifiAdmin;
import com.ryg.tianxuntest.utils.WifiMessage;
import com.ryg.tianxuntest.view.WifiDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import greendao.WifiLog;

/**
 * Created by renyiguang on 2015/9/25.
 */
public class WifiDetialActivity extends BaseActivity implements View.OnClickListener,ConnectedStatusListener{

    public TextView tv_wifi_status;
    public TextView tv_wifi_ssid;
    public TextView tv_wifi_bssid;
    private TextView tv_wifi_on_time;
    private TextView tv_wifi_off_time;
    private TextView tv_connect;
    private TextView tv_disconnect;
//    private Button btn_start;

    public ScanResult scanResult;
    private WifiLogControl wifiLogControl;
    private WifiAdmin wifiAdmin;
    private WifiMessage wifiMessage;
    private WifiConnectReceiver wifiConnectReceiver;
    private ConnectivityManager connectivityManager;
    private GetConnectTime getConnectTime = new GetConnectTime();

    private double onTime = 0;
    private double offTime = 0;
    private boolean enable = true;

    private boolean registerFlag ;

//    FileWriter wifiFile = null;


    @Override
    protected int getContentView() {
        return R.layout.activity_wifi_detial;
    }


    @Override
    public void initView() {
        setLeftBack();
        scanResult = (ScanResult)getIntent().getParcelableExtra("scanResult");
        setCenterTextView(scanResult.SSID);
        setCenterTextView("Auto Connect");

        tv_wifi_status = (TextView)findViewById(R.id.tv_wifi_status);
        tv_wifi_ssid = (TextView)findViewById(R.id.tv_wifi_ssid);
        tv_wifi_bssid = (TextView)findViewById(R.id.tv_wifi_bssid);
        tv_wifi_on_time = (TextView)findViewById(R.id.tv_wifi_on_time);
        tv_wifi_off_time = (TextView)findViewById(R.id.tv_wifi_off_time);
        tv_connect = (TextView)findViewById(R.id.tv_connect);
        tv_disconnect = (TextView)findViewById(R.id.tv_disconnect);
        tv_disconnect.setEnabled(false);
        tv_disconnect.setVisibility(View.INVISIBLE);
//        btn_start = (Button)findViewById(R.id.btn_start);

        File file = new File(String.valueOf(Environment.getExternalStorageDirectory()) + "//ConnectResult-wifi.txt");
        if(file.exists()){
            file.delete();
        }

        Constant.isWritWifiResult = false;
    }

    @Override
    public void setView() {
        tv_connect.setOnClickListener(this);
        tv_disconnect.setOnClickListener(this);
//        btn_start.setOnClickListener(this);
        wifiLogControl = WifiLogControl.getInstance();
        wifiAdmin = WifiAdmin.getInstance();
        wifiMessage = WifiMessage.getWifiMessage();
        wifiConnectReceiver = new WifiConnectReceiver(wifiAdmin);

        tv_wifi_ssid.setText(scanResult.SSID);
        tv_wifi_bssid.setText(scanResult.BSSID);

        tv_wifi_on_time.setText("0");
        tv_wifi_off_time.setText("0");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(registerFlag){
            unRigisterWIFI();
            registerFlag = false;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.tv_connect:
                tv_connect.setEnabled(false);
                tv_connect.setVisibility(View.INVISIBLE);
                tv_disconnect.setEnabled(true);
                tv_disconnect.setVisibility(View.VISIBLE);
                Constant.isConnect = true;

                registerWIFI();
                //清除数据库记录
                wifiLogControl.clear();
//                Log.e("wiyitest", "sendbroadcast================");
//                sendBroadcast(new Intent().setAction("wifiStatusChange"));

                if(Constant.wifiConfiguration != null && Constant.wifiConfiguration.BSSID.equals(scanResult.BSSID)){
                    boolean enable = wifiAdmin.addNetwork(Constant.wifiConfiguration);
                    if(enable){
                        showToast(R.string.connect_ok);
//                        btn_start.setText(R.string.end);
                        tv_wifi_on_time.setText("0");
                        tv_wifi_off_time.setText("0");

                    }else {

                        showToast(R.string.connect_error);
                    }
                }else {
                    WifiDialog wifiDialog = new WifiDialog(this,wifiAdmin,scanResult);
//                    WifiDialog wifiDialog = null;
                    wifiDialog.show();
                    wifiDialog.setConnectedListener(new ConnectedListener() {
                        @Override
                        public void connected() {
                            showToast(R.string.connect_ok);
                        }
                    });
                }
                break;
            case R.id.tv_disconnect:
                tv_disconnect.setEnabled(false);
                tv_disconnect.setVisibility(View.INVISIBLE);
                tv_connect.setEnabled(true);
                tv_connect.setVisibility(View.VISIBLE);
                Constant.isConnect = false;
                Constant.isWritWifiResult = true;

                Debug.log("===================================================");
                int id = wifiAdmin.getNetworkId();
                wifiAdmin.disconnectWifi(id);
                //如果有注册的广播，就取消注册
                if(registerFlag){
                    unRigisterWIFI();
                    registerFlag = false;
                }
                Log.e("wiyitest", "unrigisterwifi================");

                try {
                    getConnectTime.getConnectTime();
                } catch (IOException e) {
                    e.printStackTrace();
                    Debug.log("~~~~~~~~~~~~~~~~~~~~~~~~~"+e.toString());
                }

                Debug.log("=========================should write file");
//                tv_wifi_on_time.setText(onTime+"");
//                tv_wifi_off_time.setText(offTime+"");
                break;
//            case R.id.btn_start:
//                if(btn_start.getText().toString().trim().equals(getResources().getString(R.string.start))){
//
//                    if(Constant.wifiConfiguration!=null && Constant.wifiConfiguration.BSSID.equals(scanResult.BSSID)){
//                        boolean enable = wifiAdmin.addNetwork(Constant.wifiConfiguration);
//                        if(enable){
//                            showToast(R.string.connect_ok);
//                            wifiLogControl.clear();
//                            btn_start.setText(R.string.end);
//                            tv_wifi_on_time.setText("0");
//                            tv_wifi_off_time.setText("0");
//
//                        }else {
//                            showToast(R.string.connect_error);
//                        }
//                    }else {
//                        WifiDialog wifiDialog = new WifiDialog(this,wifiAdmin,scanResult);
//                        wifiDialog.show();
//                        wifiDialog.setConnectedListener(new ConnectedListener() {
//                            @Override
//                            public void connected() {
//                                showToast(R.string.connect_ok);
//
//                                wifiLogControl.clear();
//                                btn_start.setText(R.string.end);
//                                tv_wifi_on_time.setText("0");
//                                tv_wifi_off_time.setText("0");
//                            }
//                        });
//                    }
//
//                }
//                else{
//                    btn_start.setText(R.string.start);
//                    List<WifiLog> list = wifiLogControl.getAll();
//                    long onTime=0;
//                    long offTime=0;
//                    WifiLog lastWifiLog= null;
//                    Iterator<WifiLog> wifiLogIterator = list.iterator();
//                    while(wifiLogIterator.hasNext()){
//                        WifiLog wifiLog = wifiLogIterator.next();
//                        if(lastWifiLog == null){
//                            lastWifiLog = wifiLog;
//                        }else {
//                            if (!lastWifiLog.getName().equals(wifiLog.getName())) {
//
//                                if (lastWifiLog.getName().equals("WIFI_CONNECTED")) {
//                                    onTime += wifiLog.getTime() - lastWifiLog.getTime();
//                                } else {
//                                    offTime += wifiLog.getTime() - lastWifiLog.getTime();
//                                }
//                                lastWifiLog = wifiLog;
//                            }
//                        }
//                    }
//                    try {
//                        getConnectTime();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    tv_wifi_on_time.setText(onTime+"  ms");
//                    tv_wifi_off_time.setText(offTime + "  ms");
//
//                    Log.e("wifitest", "onTime~~~~~~~~~~~~~~" + onTime);
//                    Log.e("wifitest","offTime~~~~~~~~~~~~~~"+offTime);
//                }
//                break;
        }
    }


    private void registerWIFI() {
        IntentFilter mWifiFilter = new IntentFilter();
        mWifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mWifiFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mWifiFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
//        mWifiFilter.addAction("wifiStatusChange");
        registerReceiver(wifiConnectReceiver, mWifiFilter);
        registerFlag = true;

        WifiConnectReceiver.registerConnectedStatusListener(this);
        WifiConnectReceiver.setBSSID(scanResult.BSSID);
    }

    private void unRigisterWIFI(){
        WifiConnectReceiver.unregisterConnectedStatusListener(this);
        unregisterReceiver(wifiConnectReceiver);
    }
    public void connect(){
        if(Constant.wifiConfiguration!=null && Constant.wifiConfiguration.BSSID.equals(scanResult.BSSID)){
            boolean enable = wifiAdmin.addNetwork(Constant.wifiConfiguration);
            if(enable){
                showToast(R.string.connect_ok);
                wifiLogControl.clear();
//                btn_start.setText(R.string.end);
                tv_wifi_on_time.setText("0");
                tv_wifi_off_time.setText("0");

            }else {
                showToast(R.string.connect_error);
            }
        }else {
            WifiDialog wifiDialog = new WifiDialog(this,wifiAdmin,null);
            wifiDialog.show();
            wifiDialog.setConnectedListener(new ConnectedListener() {
                @Override
                public void connected() {
                    showToast(R.string.connect_ok);

                    wifiLogControl.clear();
//                    btn_start.setText(R.string.end);
                    tv_wifi_on_time.setText("0");
                    tv_wifi_off_time.setText("0");
                    }
                });
            }
        }
    private double percent;

//    public void getConnectTime() throws IOException {
//        List<WifiLog> list = wifiLogControl.getAll();
//        WifiLog lastWifiLog= null;
//        WifiLog wifiLog = null;
//        Iterator<WifiLog> wifiLogIterator = list.iterator();
//        while(wifiLogIterator.hasNext()){
//            wifiLog = wifiLogIterator.next();
//            if(lastWifiLog == null){
//                lastWifiLog = wifiLog;
//            }else {
//                if (!lastWifiLog.getName().equals(wifiLog.getName())) {
//
//                    if (lastWifiLog.getName().equals("WIFI_CONNECTED")) {
////                        onTime += wifiLog.getTime() - lastWifiLog.getTime();
//                        onTime = wifiLog.getTime() - lastWifiLog.getTime();
//                        wirteConnectResult(lastWifiLog.getData(),wifiLog.getData(),"Connected",onTime, lastWifiLog.getBSSID());
//                        Log.e("wifitest","ontime is ====================="+onTime);
//                    } else {
////                        offTime += wifiLog.getTime() - lastWifiLog.getTime();
//                        offTime = wifiLog.getTime() - lastWifiLog.getTime();
//                        wirteConnectResult(lastWifiLog.getData(),wifiLog.getData(),"UnConnected",offTime, lastWifiLog.getBSSID());
//                        Log.e("wifitest","offTime is =================="+offTime);
//                    }
//                    lastWifiLog = wifiLog;
//                }
//            }
//
//        }
//
//        //循环到最后的状态，用现在的时间减去最后的时间，就是最后状态持续的时间
//        Date date = new Date();
//        if(lastWifiLog.getName().equals("WIFI_CONNECTED")){
////            onTime += date.getTime() - wifiLog.getTime();
//            onTime = date.getTime() - lastWifiLog.getTime();
//            SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
//            wirteConnectResult(lastWifiLog.getData(),format.format(date),"Connected",onTime, lastWifiLog.getBSSID());
//            Log.e("wifitest", "ontime is =====================" + onTime);
//        }else{
////            offTime += date.getTime() - wifiLog.getTime();
//            offTime = date.getTime() - lastWifiLog.getTime();
//            SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
//            wirteConnectResult(lastWifiLog.getData(),format.format(date),"UnConnected",offTime,lastWifiLog.getBSSID());
//            Log.e("wifitest", "offTime is ==================" + offTime);
//        }
////        if(lastWifiLog.getName().equals(wifiLog.getName())) {
////            if (lastWifiLog.getName().equals("WIFI_CONNECTED")) {
////                onTime += wifiLog.getTime() - lastWifiLog.getTime();
////                onTime += date.getTime() - wifiLog.getTime();
////                Log.e("wifitest", "ontime is =====================" + onTime);
////            } else {
////                offTime += wifiLog.getTime() - lastWifiLog.getTime();
////                offTime += date.getTime() - wifiLog.getTime();
////                Log.e("wifitest", "offTime is ==================" + offTime);
////            }
////        }
//
//        Log.e("wifitest", "OnTime is ===============" + onTime + "");
//        Log.e("wifitest", "OffTime is ===============" + offTime + "");
//
//        wifiFile.close();
//
////        if(onTime == 0){
////            percent = 0;
////            Log.e("wifitest","onTime is 0=================");
////        }
////        else if(offTime == 0){
////            percent = 100;
////            Log.e("wifitest","offTime is 0=================");
////        }
////        else{
////            percent = (double)(onTime - offTime) / offTime * 100;
////        }
////        percent = (double)(onTime - offTime) / offTime * 100;
////        DecimalFormat df = new DecimalFormat("#.00");
////        Log.e("wiyitest","在线时间比离线时间多出 " + df.format(percent) + "%");
////        Toast.makeText(WifiDetialActivity.this,"在线时间: " + onTime + "离线时间: " + offTime + "在线时间比离线时间多出 " + df.format(percent) + "%",Toast.LENGTH_LONG).show();
//
////        double test = (double)(0-9999)/9999*100;
////        Debug.log("(0-9999)/9999*100 ============== " + df.format(test)+"%");
//    }



//    public void wirteConnectResult(String dataBefor, String dataAfter, String status, double time, String mac) throws IOException {
//        Debug.log("~~~~~~~~~~~~~~~~~~~~~~~~~~~action");
//        File sdDir = new File(String.valueOf(Environment.getExternalStorageDirectory()));
//        if(!sdDir.exists()){
//            Debug.log("~~~~~~~~~~~~~~~~~~~~~~~dir");
//            sdDir.mkdir();
//        }
//
//        File result = new File(sdDir + "//ConnectResult.txt");
//        if(!result.exists()){
//            Debug.log("~~~~~~~~~~~~~~~~~~~~~~~~~resultdir");
//            result.createNewFile();
//        }
//
//        Debug.log(result.getAbsolutePath()+"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//
//        try {
//            if(wifiFile == null){
//                Debug.log("~~~~~~~~~~~~~~~~~~~~~~~~"+wifiFile);
//                wifiFile = new FileWriter(result,true);
//
//            }
//            double s = time / 1000 ;
//            wifiFile.write(dataBefor + "   " + dataAfter + "   " + status + "   " + mac + "\n" + s + "s \n");
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            Debug.log("~~~~~~~~~~~~~~~~~~~"+e.toString());
//        }
////        finally {
////            try {
////                wifiFile.close();
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////        }
//
//    }

    public void wirteResult(){
        File sdDir = new File(String.valueOf(Environment.getExternalStorageDirectory()));
        FileWriter wifiFile = null;
        try {
            wifiFile = new FileWriter(sdDir+"//wifiTest//wifiResult.txt");
            if(percent >= 50){
                wifiFile.write(0+"");
            }
            else{
                wifiFile.write(1+"");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            wifiFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                wifiFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void disconnected() {
        tv_wifi_status.setText("disConnected");
    }

    @Override
    public void connected() {
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        tv_wifi_status.setText("Connected");
        tv_wifi_ssid.setText(wifiInfo.getSSID());
        tv_wifi_bssid.setText(wifiInfo.getBSSID());
    }
}
