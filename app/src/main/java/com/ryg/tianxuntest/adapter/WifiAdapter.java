package com.ryg.tianxuntest.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ryg.tianxuntest.R;

import java.util.List;

/**
 * Created by renyiguang on 2015/9/22.
 * 显示主界面热点信息数据的适配器
 */
public class WifiAdapter extends CommonBaseAdapter<ScanResult> {

    public WifiAdapter(Context context, List<ScanResult> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.wifi_item,null);
            holder = new Holder();
            holder.tv_name = (TextView)convertView.findViewById(R.id.tv_name);
            holder.tv_mac = (TextView)convertView.findViewById(R.id.tv_mac);
            convertView.setTag(holder);
        }else{
            holder = (Holder)convertView.getTag();
        }

        ScanResult scanResult = getItem(position);
        holder.tv_name.setText(scanResult.SSID);
        holder.tv_mac.setText(scanResult.BSSID);
        return convertView;
    }

    class Holder{
        TextView tv_name;
        TextView tv_mac;
    }
}
