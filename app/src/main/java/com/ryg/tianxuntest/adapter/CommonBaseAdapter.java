package com.ryg.tianxuntest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by renyiguang on 2015/7/13.
 */
public abstract class CommonBaseAdapter<T> extends BaseAdapter {

    protected List<T> list;
    protected Context context;
    protected LayoutInflater inflater;

    public CommonBaseAdapter(Context context, List<T> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list == null?0:list.size();
    }

    @Override
    public T getItem(int position) {
        return list == null?null:list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public void addItems(List<T> objects){
        if(list==null){
            list = new ArrayList<T>();
        }
        if(objects!=null&&objects.size()>0) {
            list.addAll(objects);
        }
        notifyDataSetChanged();
    }

    public void refreshItems(List<T> objects){
        clearItems();
        addItems(objects);
    }

    public void clearItems(){
        if(list!=null&&list.size()>0){
            list.clear();
        }
        notifyDataSetChanged();
    }


    public List<T> getList(){
        return list;
    }
}
