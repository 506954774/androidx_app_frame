package com.qdong.communal.library.module.network.adapter;


import androidx.annotation.LayoutRes;
import androidx.databinding.ViewDataBinding;

import android.util.SparseArray;
import android.view.ViewGroup;

import com.qdong.communal.library.util.LogUtil;

import java.util.List;

/**
 * CustomSingleQuickAdapter
 * 使用DataBinding的布局,单一item样式的适配器
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2017/9/10  1:00
 * Copyright : 2014-2016 深圳趣动智能科技有限公司-版权所有
 **/

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public  class CustomSingleQuickAdapter<T,K extends BaseViewHolder2,D extends ViewDataBinding> extends BaseQuickAdapter2<T, K> {

    /**
     * layouts indexed with their types
     */
    private SparseArray<Integer> layouts;


    ////chuck:单一的type,选取值应该避免以下几个值:
//    public static final int HEADER_VIEW = 0x00000111;
//    public static final int LOADING_VIEW = 0x00000222;
//    public static final int FOOTER_VIEW = 0x00000333;
//    public static final int EMPTY_VIEW = 0x00000555;
    private static final int SINGLE_ONLY_TYPE = 1;//就这一个



    private static final int DEFAULT_VIEW_TYPE = -0xff;
    public static final int TYPE_NOT_FOUND = -404;


    private int mBR_id;//插件自动生成的实体id;

    public CustomSingleQuickAdapter(List<T> data, int layoutId, int BR_id) {
        super(data);
        addItemType(SINGLE_ONLY_TYPE,layoutId);
        this.mBR_id=BR_id;
    }

    @Override
    protected int getDefItemViewType(int position) {
        Object item = mData.get(position);
        if (item !=null) {
            return SINGLE_ONLY_TYPE;
        }
        return DEFAULT_VIEW_TYPE;
    }

    protected void setDefaultViewTypeLayout(@LayoutRes int layoutResId) {
        addItemType(DEFAULT_VIEW_TYPE, layoutResId);
    }

    @Override
    public K onCreateDefViewHolder(ViewGroup parent, int viewType) {
        return createBaseViewHolder2(parent, getLayoutId(viewType));
    }

    private int getLayoutId(int viewType) {
        return layouts.get(viewType,TYPE_NOT_FOUND);
    }

    protected void addItemType(int type, @LayoutRes int layoutResId) {
        if (layouts == null) {
            layouts = new SparseArray<>();
        }
        layouts.put(type, layoutResId);
    }


    /**
     * @method name:convert
     * @des:通过DataBind先设置一些简单的属性
     * @param :[helper, item, position]
     * @return type:void
     * @date 创建时间:2017/9/10
     * @author Chuck
     **/
    @Override
    protected void convert(K helper, T item,int position) {
        if(helper!=null){
            try {
                if(helper.getItemViewType()==SINGLE_ONLY_TYPE){//就这一个
                    ViewDataBinding viewDataBinding=helper.getViewBind(layouts.get(SINGLE_ONLY_TYPE),position);
                    if(viewDataBinding !=null){

                        //简单的数据填充,自动的
                        viewDataBinding.setVariable(mBR_id, item);
                        viewDataBinding.executePendingBindings();

                        //手动加入复杂的填充,可以直接拿到view,不必findviewById
                        convert(helper, item, (D) viewDataBinding,position);//强转为子类的DataBind
                    }
                }
            } catch (Exception e) {
                LogUtil.e("CustomSingleQuickAdapter",e.getMessage());
                e.printStackTrace();
            }
        }
    }


    /**
     * @method name:convert
     * @des:子类可以不重写这个方法,因为如果item界面非常简单,则直接通过databind
     * 在item的xml里面配置即可
     * @param :[helper, item, viewBind, position]
     * @return type:void
     * @date 创建时间:2017/9/10
     * @author Chuck
     **/
    protected void convert(K helper, T item,D viewBind,int position) {

    }

}



