package com.ilinklink.tg.base;

import android.content.Context;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * BaseRecyclerViewAdapter
 * recyclerView 适配器基类
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/9/5  11:43
 * Copyright : 全民智慧城市 版权所有
 **/
public  abstract class BaseRecyclerViewAdapter<VH extends RecyclerView.ViewHolder,T>  extends  RecyclerView.Adapter{

    protected ArrayList<T> mData;
    protected Context   mContext;

    public BaseRecyclerViewAdapter(ArrayList<T> mData, Context mContext) {
        this.mData = mData==null?new ArrayList<T>():mData;
        this.mContext = mContext;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        /**强转**/
        getView((VH) holder, position);
    }

    public void resetDatas(ArrayList<T> mData){
        if(mData==null){
            mData=new ArrayList<T>();
        }
        if(this.mData==null){
            this.mData=new ArrayList<T>();
        }
        this.mData.clear();
        this.mData.addAll(mData);
        notifyDataSetChanged();
    }

    public void addDatas(ArrayList<T> mData){
        if(mData==null){
            mData=new ArrayList<T>();
        }
        if(this.mData==null){
            this.mData=new ArrayList<T>();
        }
        this.mData.addAll(mData);
        notifyDataSetChanged();
    }

    public void addData(T bean){
        if(mData==null){
            mData=new ArrayList<T>();
        }
        this.mData.add(bean);
        notifyDataSetChanged();
    }


    /**
     * 实现此方法,根据数据绘制界面,作用类似原来的listview的adapter的getView,只不过可以直接获取holder的控件
     *
     * @param holder
     * @param position
     */
    public abstract void getView(VH holder, int position);
}
