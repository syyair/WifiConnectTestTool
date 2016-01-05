package com.ryg.tianxuntest.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.ryg.tianxuntest.WifiDetialActivity;
import com.ryg.tianxuntest.constant.Constant;
import com.ryg.tianxuntest.dbcontrol.WifiLogControl;
import com.ryg.tianxuntest.interfaces.ConnectedStatusListener;
import com.ryg.tianxuntest.utils.Debug;
import com.ryg.tianxuntest.utils.FileUtil;
import com.ryg.tianxuntest.utils.WifiAdmin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import greendao.WifiLog;

/**
 * Created by renyiguang on 2015/9/22.
 */
public class WifiConnectReceiver extends BroadcastReceiver {

    private WifiLogControl wifiLogControl = WifiLogControl.getInstance();

    private WifiAdmin wifiAdmin;

    private static String BSSID;

    public static void setBSSID(String BSSID) {
        WifiConnectReceiver.BSSID = BSSID;
    }

    public WifiConnectReceiver(WifiAdmin wifiAdmin) {
        this.wifiAdmin = wifiAdmin;
    }

    private static Set<ConnectedStatusListener> connectedStatusListenerSet = new HashSet<>();

    private int count = 0;

    WifiLog wifiLog = new WifiLog();

    public static void registerConnectedStatusListener(ConnectedStatusListener statusListener) {
        connectedStatusListenerSet.add(statusListener);
    }

    public static void unregisterConnectedStatusListener(ConnectedStatusListener statusListener) {
        connectedStatusListenerSet.remove(statusListener);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if(info.getState().equals(NetworkInfo.State.DISCONNECTED)){

                Debug.log("NETWORK_STATE_CHANGE_ACTION============WIFI_DISCONNECTED");
                //Toast.makeText(context, "WIFI_DISCONNECTED", Toast.LENGTH_SHORT).show();
                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");

                if(Constant.isConnect){
                    Iterator<ConnectedStatusListener> connectedStatusListenerIterator = connectedStatusListenerSet.iterator();
                    while (connectedStatusListenerIterator.hasNext()) {
                        ConnectedStatusListener connectedStatusListener = connectedStatusListenerIterator.next();
                        connectedStatusListener.disconnected();
                    }
                    wifiLog = new WifiLog(null,"WIFI_DISCONNECTED",date.getTime(), BSSID,format.format(date));
                }else{
                    wifiLog = new WifiLog(null,"WIFI_DISCONNECTED",date.getTime(),"No BSSID", format.format(date));
                }
                Debug.log("NETWORK_STATE_CHANGE_ACTION==========================time = "+format.format(date));
                wifiLogControl.insert(wifiLog);
                Debug.log("NETWORK_STATE_CHANGE_ACTION==============wifiLogInsert");
                Debug.log("NETWORK_STATE_CHANGE_ACTION=============disconnected time = ");

                if(Constant.wifiConfiguration != null) {
                    boolean enable = wifiAdmin.addNetwork(Constant.wifiConfiguration);

                }
                if(Constant.isStart) {
//                        String str = "WIFI_DISCONNECTED-" + format.format(date) + "\r\n";
//                        FileUtil.writeFileToSD(str);

                    wifiAdmin.addNetwork(Constant.wifiConfiguration);
                    if (count++ >= 5) {
                        Iterator<ConnectedStatusListener> connectedStatusListenerIterator = connectedStatusListenerSet.iterator();
                        while (connectedStatusListenerIterator.hasNext()) {
                            ConnectedStatusListener connectedStatusListener = connectedStatusListenerIterator.next();
                            connectedStatusListener.disconnected();
                        }
                        count = 0;
                    }
                }

            }else if(info.getState().equals(NetworkInfo.State.CONNECTED)){

                Debug.log("NETWORK_STATE_CHANGE_ACTION============WIFI_CONNECTED");
                // Toast.makeText(context,"WIFI_CONNECTED",Toast.LENGTH_SHORT).show();
                WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();

                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
                wifiLog = new WifiLog(null,"WIFI_CONNECTED",date.getTime(),wifiInfo.getBSSID(), format.format(date));
                Debug.log("NETWORK_STATE_CHANGE_ACTION==========================time = " + format.format(date));
                wifiLogControl.insert(wifiLog);
                Debug.log("NETWORK_STATE_CHANGE_ACTION===================wifilogInsert");

                if(Constant.isConnect){
                    if(BSSID.equals(wifiInfo.getBSSID())) {
                        Iterator<ConnectedStatusListener> connectedStatusListenerIterator = connectedStatusListenerSet.iterator();
                        while (connectedStatusListenerIterator.hasNext()) {
                            ConnectedStatusListener connectedStatusListener = connectedStatusListenerIterator.next();
                            connectedStatusListener.connected();
                        }
                    }
                }

                if(Constant.isStart) {
//                    String str = "WIFI_CONNECTED-" + format.format(date) + "-" + wifiInfo.getBSSID() + "\r\n";
//                    FileUtil.writeFileToSD(str);
                }

                Debug.log("============SSID = "+wifiInfo.getSSID());
            }
        }
        else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
            int message = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);

            switch (message) {
                case WifiManager.WIFI_STATE_DISABLED:
                    Debug.log("============WIFI_STATE_DISABLED");

                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    Debug.log("============WIFI_STATE_DISABLING");

                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    Debug.log("============WIFI_STATE_ENABLED");

                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    Debug.log("============WIFI_STATE_ENABLING");

                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    Debug.log("============WIFI_STATE_UNKNOWN");

                    break;
                default:
                    break;
            }
        }
    }
}
