package com.ilinklink.tg.base;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

/**
 * ListView OR GridView 使用的BaseAdapter
 * Created by LHD on 2016/8/31.
 * * Copyright : 全民智慧城市 版权所有
 */
public class BaseListAdapter<E, V extends ViewDataBinding> extends BaseAdapter {

    private V viewBind;
    private int layoutId; // 布局文件资源ID
    private List<E> list; // 数据集合
    private int variableId; // Item 需要更新数据ID

    public BaseListAdapter(int layoutId, int variableId, List<E> list) {
        this.list = list==null?new ArrayList<E>():list;
        this.layoutId = layoutId;
        this.variableId = variableId;
    }

    @Override
    public int getCount() {
        if (list != null)
            return list.size();
        return 0;
    }

    @Override
    public E getItem(int position) {
        if (list != null && !list.isEmpty())
            return list.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            viewBind = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), layoutId, parent, false);
            convertView = viewBind.getRoot();
            convertView.setTag(viewBind);
        } else {
            viewBind = (V) convertView.getTag();
        }
        viewBind.setVariable(variableId, getItem(position));
        setItemEventHandler(position, viewBind, getItem(position));
        return convertView;
    }

    /**
     * 更新单个Item数据
     *
     * @param listView AbsListView 子类控件对象
     * @param entity   数据对象
     */
    public void notifyItemData(AbsListView listView, E entity) {
        V viewBind = getViewBind(listView, list.indexOf(entity));
        if (viewBind != null)
            viewBind.setVariable(variableId, entity);
    }

    /**
     * 获取到ViewBind对象
     *
     * @param listView  AbsListView 子类控件对象
     * @param itemIndex 需要获取的Item位置
     * @return null 不可见位置
     */
    public V getViewBind(AbsListView listView, int itemIndex) {
        int visiblePosition = listView.getFirstVisiblePosition();
        //只有当要更新的view在可见的位置时才更新，不可见时，跳过不更新
        if (itemIndex - visiblePosition < 0)
            return null;
        //调用adapter更新界面
        return (V) listView.getChildAt(itemIndex - visiblePosition).getTag();
    }


    /**
     * 更新集合数据
     *
     * @param list 数据集合内容
     */

    public void notityData(List<E> list) {

        if(this.list!=null&&list!=null){
            this.list.clear();
            this.list.addAll(list);
        }
        this.notifyDataSetChanged();
    }


    /**
     * 设置Item 事件处理
     *
     * @param viewBind item 对应的viewBind对象
     * @param entity   item 对应的数据对象
     */
    public void setItemEventHandler(int position, V viewBind, E entity) {
    }
}
