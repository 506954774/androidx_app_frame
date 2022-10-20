package com.qdong.communal.library.module.BaseRefreshableListFragment.adapter;


/**
 * Copyright 2013 Joan Zapata
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import androidx.annotation.IdRes;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import com.qdong.communal.library.util.LogUtil;

import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 * BaseViewHolder2,
 *
 * 改良自:BaseViewHolder,加入DataBinding
        * @see com.chad.library.adapter.base.BaseViewHolder
        *
        * 责任人:  Chuck
        * 修改人： Chuck
        * 创建/修改时间: 2017/9/10  0:18
        * Copyright : 2014-2016 深圳趣动智能科技有限公司-版权所有
        **/

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class BaseViewHolder2 extends RecyclerView.ViewHolder {

    private static final String TAG="BaseViewHolder2";

    /**
     * Views indexed with their BindViews
     */
    private final SparseArray<ViewDataBinding> mDataBindings;


    /**
     * Views indexed with their IDs
     */
    private final SparseArray<View> views;

    public HashSet<Integer> getNestViews() {
        return nestViews;
    }

    private final HashSet<Integer> nestViews;

    private final LinkedHashSet<Integer> childClickViewIds;

    private final LinkedHashSet<Integer> itemChildLongClickViewIds;
    private BaseQuickAdapter2 adapter;
    /**
     * use itemView instead
     */
    @Deprecated
    public View convertView;

    /**
     * Package private field to retain the associated user object and detect a change
     */
    Object associatedObject;


    public BaseViewHolder2(final View view) {
        super(view);
        this.mDataBindings = new SparseArray<>();
        this.views = new SparseArray<>();
        this.childClickViewIds = new LinkedHashSet<>();
        this.itemChildLongClickViewIds = new LinkedHashSet<>();
        this.nestViews = new HashSet<>();
        convertView = view;


    }

    private int getClickPosition() {
        return getLayoutPosition() - adapter.getHeaderLayoutCount();
    }

    public HashSet<Integer> getItemChildLongClickViewIds() {
        return itemChildLongClickViewIds;
    }

    public HashSet<Integer> getChildClickViewIds() {
        return childClickViewIds;
    }

    /**
     * use itemView instead
     *
     * @return the ViewHolder root view
     */
    @Deprecated
    public View getConvertView() {

        return convertView;
    }






    /**
     * Sets the on click listener of the view.
     *
     * @param viewId   The view id.
     * @param listener The on click listener;
     * @return The BaseViewHolder2 for chaining.
     */
    @Deprecated
    public BaseViewHolder2 setOnClickListener(@IdRes int viewId, View.OnClickListener listener, int positon) {
        View view = getView(viewId, positon);
        view.setOnClickListener(listener);
        return this;
    }

    /**
     * add childView id
     *
     * @param viewId add the child view id   can support childview click
     * @return if you use adapter bind listener
     * @link {(adapter.setOnItemChildClickListener(listener))}
     * <p>
     * or if you can use  recyclerView.addOnItemTouch(listerer)  wo also support this menthod
     */
    @SuppressWarnings("unchecked")
    public BaseViewHolder2 addOnClickListener(@IdRes final int viewId, int positon) {
        childClickViewIds.add(viewId);
        final View view = getView(viewId,positon);
        if (view != null) {
            if (!view.isClickable()) {
                view.setClickable(true);
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (adapter.getOnItemChildClickListener() != null) {
                        adapter.getOnItemChildClickListener().onItemChildClick(adapter, v, getClickPosition());
                    }
                }
            });
        }

        return this;
    }


    /**
     * set nestview id
     *
     * @param viewId add the child view id   can support childview click
     * @return
     */
    public BaseViewHolder2 setNestView(@IdRes int viewId, int positon) {
        addOnClickListener(viewId,positon);
        addOnLongClickListener(viewId,positon);
        nestViews.add(viewId);
        return this;
    }

    /**
     * add long click view id
     *
     * @param viewId
     * @return if you use adapter bind listener
     * @link {(adapter.setOnItemChildLongClickListener(listener))}
     * <p>
     * or if you can use  recyclerView.addOnItemTouch(listerer)  wo also support this menthod
     */
    @SuppressWarnings("unchecked")
    public BaseViewHolder2 addOnLongClickListener(@IdRes final int viewId, int positon) {
        itemChildLongClickViewIds.add(viewId);
        final View view = getView(viewId,positon);
        if (view != null) {
            if (!view.isLongClickable()) {
                view.setLongClickable(true);
            }
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return adapter.getOnItemChildLongClickListener() != null &&
                            adapter.getOnItemChildLongClickListener().onItemChildLongClick(adapter, v, getClickPosition());
                }
            });
        }
        return this;
    }


    /**
     * Sets the on touch listener of the view.
     *
     * @param viewId   The view id.
     * @param listener The on touch listener;
     * @return The BaseViewHolder2 for chaining.
     */
    @Deprecated
    public BaseViewHolder2 setOnTouchListener(@IdRes int viewId, View.OnTouchListener listener, int positon) {
        View view = getView(viewId, positon);
        view.setOnTouchListener(listener);
        return this;
    }

    /**
     * Sets the on long click listener of the view.
     *
     * @param viewId   The view id.
     * @param listener The on long click listener;
     * @return The BaseViewHolder2 for chaining.
     * Please use {@link #addOnLongClickListener(int,int)} (adapter.setOnItemChildLongClickListener(listener))}
     */
    @Deprecated
    public BaseViewHolder2 setOnLongClickListener(@IdRes int viewId, View.OnLongClickListener listener, int positon) {
        View view = getView(viewId,positon);
        view.setOnLongClickListener(listener);
        return this;
    }

    /**
     * Sets the listview or gridview's item click listener of the view
     *
     * @param viewId   The view id.
     * @param listener The item on click listener;
     * @return The BaseViewHolder2 for chaining.
     * Please use {@link #addOnClickListener(int,int)} (int)} (adapter.setOnItemChildClickListener(listener))}
     */
    @Deprecated
    public BaseViewHolder2 setOnItemClickListener(@IdRes int viewId, AdapterView.OnItemClickListener listener, int positon) {
        AdapterView view = getView(viewId,positon);
        view.setOnItemClickListener(listener);
        return this;
    }

    /**
     * Sets the listview or gridview's item long click listener of the view
     *
     * @param viewId   The view id.
     * @param listener The item long click listener;
     * @return The BaseViewHolder2 for chaining.
     */
    public BaseViewHolder2 setOnItemLongClickListener(@IdRes int viewId, AdapterView.OnItemLongClickListener listener, int positon) {
        AdapterView view = getView(viewId,positon);
        view.setOnItemLongClickListener(listener);
        return this;
    }

    /**
     * Sets the listview or gridview's item selected click listener of the view
     *
     * @param viewId   The view id.
     * @param listener The item selected click listener;
     * @return The BaseViewHolder2 for chaining.
     */
    public BaseViewHolder2 setOnItemSelectedClickListener(@IdRes int viewId, AdapterView.OnItemSelectedListener listener, int positon) {
        AdapterView view = getView(viewId,positon);
        view.setOnItemSelectedListener(listener);
        return this;
    }


    /**
     * Set a view visibility to VISIBLE (true) or GONE (false).
     *
     * @param viewId  The view id.
     * @param visible True for VISIBLE, false for GONE.
     * @return The BaseViewHolder for chaining.
     */
    public BaseViewHolder2 setVisible(@IdRes int viewId, boolean visible, int position) {
        View view = getView(viewId,position);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }


    /**
     * Sets the on checked change listener of the view.
     *
     * @param viewId   The view id.
     * @param listener The checked change listener of compound button.
     * @return The BaseViewHolder2 for chaining.
     */
    public BaseViewHolder2 setOnCheckedChangeListener(@IdRes int viewId, CompoundButton.OnCheckedChangeListener listener, int position) {
        CompoundButton view = getView(viewId,position);
        view.setOnCheckedChangeListener(listener);
        return this;
    }

    /**
     * Sets the tag of the view.
     *
     * @param viewId The view id.
     * @param tag    The tag;
     * @return The BaseViewHolder2 for chaining.
     */
    public BaseViewHolder2 setTag(@IdRes int viewId, Object tag, int position) {
        View view = getView(viewId,position);
        view.setTag(tag);
        return this;
    }

    /**
     * Sets the tag of the view.
     *
     * @param viewId The view id.
     * @param key    The key of tag;
     * @param tag    The tag;
     * @return The BaseViewHolder2 for chaining.
     */
    public BaseViewHolder2 setTag(@IdRes int viewId, int key, Object tag, int position) {
        View view = getView(viewId,position);
        view.setTag(key, tag);
        return this;
    }



    /**
     * Sets the adapter of a adapter view.
     *
     * @param adapter The adapter;
     * @return The BaseViewHolder2 for chaining.
     */
    public BaseViewHolder2 setAdapter(BaseQuickAdapter2 adapter) {
        this.adapter = adapter;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T getView(@IdRes int viewId, int position) {

        LogUtil.e(TAG,"getView(@IdRes int viewId,int position):position="+position);




        if(mDataBindings.get(viewId)==null){
            /***这是DataBindingUtil里面的判断语句,如果set了tag,不是string,则抛出异常  这里改一下,兼容非dataBind的
             * 布局,因为loadMore的viewHold与数据的viewHolder是同一个实例  by:Chuck******************************/
            Object tagObj = itemView.getTag();
            if (!(tagObj instanceof String)) {//非dataBind的布局,为了兼容那个LoadMoreView的业务
                View view = views.get(viewId);
                if (view == null) {
                    view = itemView.findViewById(viewId);
                    views.put(viewId, view);
                }
                return (T) view;
            }
            else{
                ViewDataBinding dataBinding= DataBindingUtil.bind(itemView);
                mDataBindings.put(viewId, dataBinding);
            }
        }

        View view = mDataBindings.get(viewId).getRoot();
        if (view == null) {
            ViewDataBinding dataBinding= DataBindingUtil.bind(itemView);
            mDataBindings.put(viewId, dataBinding);
        }
        return (T) view;
    }

    /**
     * @method name:getViewBind
     * @des:新增这个方法,来获取DataBind
     * @param :[viewId]
     * @return type:T
     * @date 创建时间:2017/9/10
     * @author Chuck
     **/
    @SuppressWarnings("unchecked")
    public <T extends ViewDataBinding > T getViewBind(@IdRes int viewId, int position) {
        if(mDataBindings.get(viewId)==null){
            ViewDataBinding dataBinding= DataBindingUtil.bind(itemView);



            mDataBindings.put(viewId, dataBinding);
        }

        View view = mDataBindings.get(viewId).getRoot();
        if (view == null) {
            ViewDataBinding dataBinding= DataBindingUtil.bind(itemView);
            mDataBindings.put(viewId, dataBinding);
        }
        return (T) mDataBindings.get(viewId);
    }

    /**
     * Retrieves the last converted object on this view.
     */
    public Object getAssociatedObject() {
        return associatedObject;
    }

    /**
     * Should be called during convert
     */
    public void setAssociatedObject(Object associatedObject) {
        this.associatedObject = associatedObject;
    }
}
