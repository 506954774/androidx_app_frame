package com.qdong.communal.library.module.BaseRefreshableListFragment;

import android.content.Context;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * MyBaseRecyclerAdapter
 * recyclerView的adpter父类.三个泛型,第一个是RecyclerView.ViewHolder的子类,第二个是item的数据模型,第三个是界面宿主fragment
 * 两个抽象方法,其实只是简单做了方法传递,还是实现RecylerView.Adapter的onCreateViewHolder和onBindViewHolder
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/6/17  17:38
 * Copyright : 趣动智能科技有限公司-版权所有
 **/
public abstract class MyBaseRecyclerAdapter<VH extends RecyclerView.ViewHolder, D> extends RecyclerView.Adapter {


    public List<D> mData;
    public Context mContext;



    /**
     * 子类实现此方法,构造viewHolder
     *
     * @param parent   父容器
     * @param viewType viewType,多种视图时用到
     * @return
     */
    public abstract VH getViewHolder(ViewGroup parent, int viewType);

    /**
     * 实现此方法,根据数据绘制界面,作用类似原来的listview的adapter的getView,只不过可以直接获取holder的控件
     *
     * @param holder
     * @param position
     */
    public abstract void getView(VH holder, int position);


    @Override
    public int getItemCount() {
        return this.mData == null ? 0 : this.mData.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return getViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        //Log.i("RecyclerAdapter","泛型的holder:"+holder.toString()+",position:"+position);

        /**强转**/
        getView((VH) holder, position);
    }


    public MyBaseRecyclerAdapter(List<D> mData, Context context) {
        this.mData = mData == null ? new ArrayList<D>() : mData;
        this.mContext = context;
    }


    /**
     * @param :[data]
     * @return type:void
     * @method name:setResult
     * @des:adapter设置数据
     * @date 创建时间:2016/6/17
     * @author Chuck
     **/
    public void setData(List<D> data) {
        if (mData == null) {
            mData = new ArrayList<D>();
        }
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    /**
     * @param :[data]
     * @return type:void
     * @method name:addData
     * @des:adapter添加数据
     * @date 创建时间:2016/6/17
     * @author Chuck
     **/
    public void addDatas(List<D> data) {
        if (mData == null) {
            mData = new ArrayList<D>();
        }
        mData.addAll(data);
        notifyDataSetChanged();
    }

    /**
     * @param :[data]
     * @return type:void
     * @method name:addData
     * @des:adapter添加数据
     * @date 创建时间:2016/6/17
     * @author Chuck
     **/
    public void addData(D data) {
        if (data == null) {
            return;
        }
        if (mData == null) {
            mData = new ArrayList<D>();
        }
        mData.add(data);
        notifyDataSetChanged();
    }

    /**
     * @return type:void
     * @method name:addData
     * @des:adapter添加数据
     * @date 创建时间:2016/6/17
     * @author Chuck
     **/
    public void addData(int index, D data) {
        if (data == null) {
            return;
        }
        if (mData == null) {
            mData = new ArrayList<D>();
        }
        mData.add(index, data);
        notifyDataSetChanged();
    }

    public D getItem(int position) {
        if (mData == null || mData.isEmpty() || position > mData.size() - 1)
            return null;
        return mData.get(position);
    }

    /**
     *
     * @return
     */
    public List<D> getAllData() {

        return mData;
    }
}
