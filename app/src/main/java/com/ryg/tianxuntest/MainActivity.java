package com.ryg.tianxuntest;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ryg.tianxuntest.adapter.WifiAdapter;
import com.ryg.tianxuntest.constant.Constant;
import com.ryg.tianxuntest.dbcontrol.WifiLogControl;
import com.ryg.tianxuntest.interfaces.ConnectedStatusListener;
import com.ryg.tianxuntest.receiver.WifiConnectReceiver;
import com.ryg.tianxuntest.utils.GetConnectTime;
import com.ryg.tianxuntest.utils.WifiAdmin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by sunyingying on 2016/2/16
 * 打开应用进入的第一个activity
 * 设置界面，包括adapterlist，底部的三个功能按钮，下拉刷新以及点击事件
 * 扫描wifi记录系统曾经链接过的wifi
 * 终端隔离测试自动连接wifi的功能
 */
public class MainActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener,ConnectedStatusListener {

    //下拉刷新
    private PullToRefreshListView lv_wifi;
    private WifiAdapter wifiAdapter;

    private TextView tv_open_wifi;
    private TextView tv_close_wifi;
    private TextView tv_terminal_test;
    private boolean registerFlag;

    private WifiAdmin wifiAdmin;
    private WifiConnectReceiver wifiConnectReceiver;
    private GetConnectTime getConnectTime = new GetConnectTime();
    private WifiLogControl wifiLogControl;

    private HashMap<String ,ScanResult> stringScanResultHashMap;

    private List<WifiConfiguration> wifiConfigurationList;
    private Iterator<WifiConfiguration> wifiConfigurationIterator;


    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {

        //给tool_bar.xml中的title_bar_center_tv textview设置标题
        setCenterTextView(R.string.app_name);

        lv_wifi = (PullToRefreshListView) findViewById(R.id.lv_wifi);
        wifiAdapter = new WifiAdapter(context,new ArrayList<ScanResult>());
        lv_wifi.setAdapter(wifiAdapter);
        lv_wifi.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        lv_wifi.setOnItemClickListener(this);

        wifiAdmin =  WifiAdmin.getInstance();
        wifiConnectReceiver = new WifiConnectReceiver(wifiAdmin);

        tv_open_wifi = (TextView)findViewById(R.id.tv_open_wifi);
        tv_close_wifi = (TextView)findViewById(R.id.tv_close_wifi);
        tv_terminal_test = (TextView)findViewById(R.id.tv_terminal_test);

        stringScanResultHashMap = new HashMap<>();
        wifiLogControl = WifiLogControl.getInstance();

        Constant.isWirteTerminalResult = false;
    }

    @Override
    public void setView() {

        boolean open = wifiAdmin.openWifi();
        if(open){
            scanWifi();
            showToast(R.string.wifi_open_success);
        }else {
            showToast(R.string.wifi_open_fail);
        }

        tv_open_wifi.setOnClickListener(this);
        tv_close_wifi.setOnClickListener(this);
        tv_terminal_test.setOnClickListener(this);

        lv_wifi.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                scanWifi();
            }
        });

//        registerWIFI(wifiAdmin);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.tv_open_wifi:
                boolean open = wifiAdmin.openWifi();
                if(open){
                    showToast(R.string.wifi_open_success);
                }else {
                    showToast(R.string.wifi_open_fail);
                }
                break;
            case R.id.tv_close_wifi:
                wifiAdmin.closeWifi();
                break;
            case R.id.tv_terminal_test:
                if(Constant.isStart) {
                    showToast("停止测试");
                    //Broadcast中当Constant.isStart发生变化时，记录数据
                    Constant.isStart = false;
                    if(registerFlag){
                        unRigisterWIFI();
                    }

                    Constant.isWirteTerminalResult = true;
                    File file = new File(String.valueOf(Environment.getExternalStorageDirectory()) + "//ConnectResult_Terminal.txt");
                    if(file.exists()){
                        file.delete();
                    }
                    try {
                        getConnectTime.getConnectTime();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else {
                    showToast("开始测试");
                    //清除数据库
                    wifiLogControl.clear();
                    registerWIFI(wifiAdmin);
                    registerFlag = true;
                    Constant.isStart = true;
                }
                break;
        }
    }

    private void scanWifi(){
        wifiAdmin.startScan();
        List<ScanResult> orignList = wifiAdmin.getWifiList();

        Comparator<ScanResult> comparator = new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult lhs, ScanResult rhs) {
                return (lhs.level > rhs.level ? -1
                        : (lhs.level == rhs.level ? 0 : 1));
            }
        };
        Collections.sort(orignList, comparator);

        wifiAdapter.refreshItems(orignList);

        lv_wifi.postDelayed(new Runnable() {
            @Override
            public void run() {
                lv_wifi.onRefreshComplete();
            }
        }, 1000);

        //查找曾经链接过的wifi最后存入到wifiConfigurationIterator
        Iterator<ScanResult> scanResultIterator = orignList.iterator();
        while (scanResultIterator.hasNext()){
            ScanResult scanResult = scanResultIterator.next();
            stringScanResultHashMap.put(scanResult.SSID,scanResult);
        }

        wifiConfigurationList = wifiAdmin.getConfiguredNetworks();
        wifiConfigurationIterator = wifiConfigurationList.iterator();
        while (wifiConfigurationIterator.hasNext()){
            WifiConfiguration wifiConfiguration = wifiConfigurationIterator.next();
            String key = wifiConfiguration.SSID.substring(1,wifiConfiguration.SSID.length()-1);
            if(!stringScanResultHashMap.containsKey(key)){
                wifiConfigurationIterator.remove();
            }else {
                wifiConfiguration.BSSID = stringScanResultHashMap.get(key).BSSID;
            }
        }
        wifiConfigurationIterator = wifiConfigurationList.iterator();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        ScanResult scanResult = wifiAdapter.getItem(position - 1);

        Intent intent = new Intent(this,WifiDetialActivity.class);
        intent.putExtra("scanResult",scanResult);
        startActivity(intent);

    }

    @Override
    public void disconnected() {
        //如果没连接上，就从wifiConfigurationIterator中查找下一个赋值给Constant.wifiConfiguration
        if(!wifiConfigurationIterator.hasNext()){
            wifiConfigurationIterator = wifiConfigurationList.iterator();
        }
        Constant.wifiConfiguration = wifiConfigurationIterator.next();
    }

    @Override
    public void connected() {

    }

    private void registerWIFI(WifiAdmin wifiAdmin) {
        IntentFilter mWifiFilter = new IntentFilter();
        mWifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mWifiFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mWifiFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiConnectReceiver, mWifiFilter);
        WifiConnectReceiver.registerConnectedStatusListener(this);
    }

    private void unRigisterWIFI(){
        unregisterReceiver(wifiConnectReceiver);
    }


    @Override
    protected void onDestroy() {
        WifiConnectReceiver.unregisterConnectedStatusListener(this);
        super.onDestroy();
    }
}
