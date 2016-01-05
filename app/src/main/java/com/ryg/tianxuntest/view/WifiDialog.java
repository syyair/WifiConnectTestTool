package com.ryg.tianxuntest.view;

import android.app.Dialog;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ryg.tianxuntest.R;
import com.ryg.tianxuntest.constant.Constant;
import com.ryg.tianxuntest.interfaces.ConnectedListener;
import com.ryg.tianxuntest.utils.WifiAdmin;
import com.ryg.tianxuntest.utils.WifiMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by renyiguang on 2015/9/22.
 */
public class WifiDialog extends Dialog implements View.OnClickListener {


    private Context context;

    private EditText et_password;
    private TextView tv_connect;
    private TextView tv_disconnect;

    private WifiAdmin wifiAdmin;
    private ScanResult scanResult;

    private ConnectedListener connectedListener;

    public WifiDialog(Context context,WifiAdmin wifiAdmin,ScanResult scanResult) {
        super(context);
        this.context = context ;
        this.wifiAdmin = wifiAdmin;
        this.scanResult = scanResult;
    }

    public WifiDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public WifiDialog(Context context, int theme) {
        super(context, theme);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_wifi);
        setTitle(R.string.network_manager);

        et_password = (EditText)findViewById(R.id.et_password);
        tv_connect = (TextView)findViewById(R.id.tv_connect);
        tv_disconnect = (TextView)findViewById(R.id.tv_disconnect);

        tv_connect.setOnClickListener(this);
        tv_disconnect.setOnClickListener(this);

//        createWifiConiguration();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_connect:
                String password = et_password.getText().toString().trim();
                if(!TextUtils.isEmpty(password)){
                    int type;
                    String capabilities = scanResult.capabilities;
                    if(capabilities.contains("WPA")){
                        type = 3;
                    }else if(capabilities.contains("WEP")){
                        type = 2;
                    }else{
                        type = 1;
                    }
                    WifiConfiguration wifiConfiguration = wifiAdmin.CreateWifiInfo(scanResult.SSID,scanResult.BSSID,password,type);
                    boolean enable = wifiAdmin.addNetwork(wifiConfiguration);
                    if(enable){
                        Constant.wifiConfiguration = wifiConfiguration;
                        connectedListener.connected();
                        dismiss();
                    }
                }else {
                    Toast.makeText(context,R.string.please_enter_password,Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.tv_disconnect:
                int id = wifiAdmin.getNetworkId();
                wifiAdmin.disconnectWifi(id);
                dismiss();
                break;
        }
    }


    public void setConnectedListener(ConnectedListener connectedListener) {
        this.connectedListener = connectedListener;
    }

//    private String ssid;
//    private String mac;
//    private String password;
//    private int type;
    private WifiMessage wifiMessage = WifiMessage.getWifiMessage();

    public void createWifiConiguration(){

//        getMessage();
//        File sdDir = new File(String.valueOf(Environment.getExternalStorageDirectory()));
//        File wifiFile = new File(sdDir+"//wifiTest//wifiMessage.txt");
//        Log.e("wifitest","wifiFile path is =====================" + wifiFile.getAbsolutePath());
//        if(!wifiFile.exists()){
//            try {
//                wifiFile.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        BufferedReader reader = null;
//        try {
//            reader = new BufferedReader(new FileReader(wifiFile));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        String str = null;
//        String[] strArr = new String[4];
//        try {
//            while((str = reader.readLine()) != null){
//                strArr = str.split("\\s+");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            reader.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            try {
//                reader.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        ssid = strArr[0];
//        mac = strArr[1];
//        password = strArr[2];
//        type = Integer.parseInt(strArr[3]);
//        Log.e("wifitest","wifiMessage is ====================="+ssid+"    "+mac+"    "+password+"   "+type);

        WifiConfiguration wifiConfiguration = wifiAdmin.CreateWifiInfo(wifiMessage.getSsid(),wifiMessage.getMac(),wifiMessage.getPassword(),wifiMessage.getType());
            boolean enable = wifiAdmin.addNetwork(wifiConfiguration);
            if(enable){
                Constant.wifiConfiguration = wifiConfiguration;
//                connectedListener.connected();
                dismiss();
            }
    }

    public void getMessage(){
        File sdDir = new File(String.valueOf(Environment.getExternalStorageDirectory()));
        File wifiFile = new File(sdDir+"//wifiTest//wifiMessage.txt");
        Log.e("wifitest", "wifiFile path is =====================" + wifiFile.getAbsolutePath());
        if(!wifiFile.exists()){
            try {
                wifiFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        BufferedReader reader = null;
        String str;
        String[] strArr = new String[4];
        try {
            reader = new BufferedReader(new FileReader(wifiFile));
            while((str = reader.readLine()) != null){
                strArr = str.split("\\s+");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        wifiMessage.setSsid(strArr[0]);
//        ssid = strArr[0];
        wifiMessage.setMac(strArr[1]);
//        mac = strArr[1];
        wifiMessage.setPassword(strArr[2]);
//        password = strArr[2];
        wifiMessage.setType(Integer.parseInt(strArr[3]));
//        type = Integer.parseInt(strArr[3]);
        Log.e("wifitest","wifiMessage is ====================="+wifiMessage.getSsid()+"    "+wifiMessage.getMac()+"    "+wifiMessage.getPassword()+"   "+wifiMessage.getType());

        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
