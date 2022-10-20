package com.qdong.communal.library.module.network.adapter;

/**
 * LoadMoreView2
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2017/9/10  0:38
 * Copyright : 2014-2016 深圳趣动智能科技有限公司-版权所有
 **/



import com.chad.library.adapter.base.BaseQuickAdapter;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;

/**
 * Created by BlingBling on 2016/11/11.
 */

public abstract class LoadMoreView2 {

    public static final int STATUS_DEFAULT = 1;
    public static final int STATUS_LOADING = 2;
    public static final int STATUS_FAIL = 3;
    public static final int STATUS_END = 4;

    private int mLoadMoreStatus = STATUS_DEFAULT;
    private boolean mLoadMoreEndGone = false;

    public void setLoadMoreStatus(int loadMoreStatus) {
        this.mLoadMoreStatus = loadMoreStatus;
    }

    public int getLoadMoreStatus() {
        return mLoadMoreStatus;
    }

    public void convert(BaseViewHolder2 holder, int positions) {
        switch (mLoadMoreStatus) {
            case STATUS_LOADING:
                visibleLoading(holder, true,positions);
                visibleLoadFail(holder, false,positions);
                visibleLoadEnd(holder, false,positions);
                break;
            case STATUS_FAIL:
                visibleLoading(holder, false,positions);
                visibleLoadFail(holder, true,positions);
                visibleLoadEnd(holder, false,positions);
                break;
            case STATUS_END:
                visibleLoading(holder, false,positions);
                visibleLoadFail(holder, false,positions);
                visibleLoadEnd(holder, true,positions);
                break;
            case STATUS_DEFAULT:
                visibleLoading(holder, false,positions);
                visibleLoadFail(holder, false,positions);
                visibleLoadEnd(holder, false,positions);
                break;
        }
    }

    private void visibleLoading(BaseViewHolder2 holder, boolean visible, int positions) {
        holder.setVisible(getLoadingViewId(), visible,positions);
    }

    private void visibleLoadFail(BaseViewHolder2 holder, boolean visible, int positions) {
        holder.setVisible(getLoadFailViewId(), visible,positions);
    }

    private void visibleLoadEnd(BaseViewHolder2 holder, boolean visible, int positions) {
        final int loadEndViewId=getLoadEndViewId();
        if (loadEndViewId != 0) {
            holder.setVisible(loadEndViewId, visible,positions);
        }
    }

    public final void setLoadMoreEndGone(boolean loadMoreEndGone) {
        this.mLoadMoreEndGone = loadMoreEndGone;
    }

    public final boolean isLoadEndMoreGone(){
        if(getLoadEndViewId()==0){
            return true;
        }
        return mLoadMoreEndGone;}

    /**
     * No more data is hidden
     * @return true for no more data hidden load more
     * @deprecated Use {@link BaseQuickAdapter#loadMoreEnd(boolean)} instead.
     */
    @Deprecated
    public boolean isLoadEndGone(){return mLoadMoreEndGone;}

    /**
     * load more layout
     *
     * @return
     */
    public abstract @LayoutRes
    int getLayoutId();

    /**
     * loading view
     *
     * @return
     */
    protected abstract @IdRes
    int getLoadingViewId();

    /**
     * load fail view
     *
     * @return
     */
    protected abstract @IdRes
    int getLoadFailViewId();

    /**
     * load end view, you can return 0
     *
     * @return
     */
    protected abstract @IdRes
    int getLoadEndViewId();
}
