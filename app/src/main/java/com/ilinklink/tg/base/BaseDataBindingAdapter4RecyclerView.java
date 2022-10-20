package com.ilinklink.tg.base;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qdong.communal.library.module.BaseRefreshableListFragment.BaseRefreshableListFragment;
import com.qdong.communal.library.module.BaseRefreshableListFragment.MyBaseRecyclerAdapter;

import java.util.List;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * BaseDataBindingAdapter4RecyclerView
 * 在刷新list的fragment是使用,里面有dataBinding
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/9/24  10:49
 * Copyright : 全民智慧城市 版权所有
 **/
public class BaseDataBindingAdapter4RecyclerView<D, V extends ViewDataBinding> extends MyBaseRecyclerAdapter<BaseDataBindingAdapter4RecyclerView.ViewHolder, D> {

    private int layoutId;
    private int variableId;

    public BaseDataBindingAdapter4RecyclerView(int layoutId, int variableId, List<D> mData, BaseRefreshableListFragment fragment) {
        super(mData, fragment.getActivity());
        this.layoutId = layoutId;
        this.variableId = variableId;
    }
    public BaseDataBindingAdapter4RecyclerView(int layoutId, int variableId, List<D> mData, Context context) {
        super(mData,context);
        this.layoutId = layoutId;
        this.variableId = variableId;
    }

    private RecyclerView.ViewHolder mViewHolder;

    public RecyclerView.ViewHolder getmViewHolder() {
        return mViewHolder;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        return mViewHolder= new  ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {
        ViewHolder holder = (ViewHolder) holder1;
        holder.viewBind.setVariable(variableId, getItem(position));
        holder.viewBind.executePendingBindings();
        setItemEventHandler((V) holder.viewBind, getItem(position), position);
    }

    @Override
    public D getItem(int position) {
        if (mData != null) {
            return mData.get(position);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    /************************************************************************************************
            * 设置Item 事件处理,重写这个以实现复杂布局
    *
            * @param viewBind item 对应的viewBind对象
    * @param entity   item 对应的数据对象
    */
    public void setItemEventHandler(V viewBind, D entity, int position) {
    }


    public static class ViewHolder<V extends ViewDataBinding> extends RecyclerView.ViewHolder {

        public V viewBind;

        private ViewHolder(View itemView) {
            super(itemView);
            viewBind = DataBindingUtil.bind(itemView);
        }
    }


    /*************************************************************
     * 这个抽象出来,是因为MyBaseRecyclerAdapter有两个子类,那一个不使用DataBinding,这个类是使用dataBinding的,故实现为空即可
     *
     * @param parent   父容器
     * @param viewType viewType,多种视图时用到
     * @return
     */
    @Override
    public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    /*********************************************************************
     * 这个抽象出来,是因为MyBaseRecyclerAdapter有两个子类,那一个不使用DataBinding,这个类是使用dataBinding的,故实现为空即可
     *
     * @param holder
     * @param position
     */
    @Override
    public void getView(ViewHolder holder, int position) {

    }
}
