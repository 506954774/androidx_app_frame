package com.qdong.communal.library.module.network.adapter;

import com.chad.library.R;


/**
 * Created by BlingBling on 2016/10/11.
 */

public final class SimpleLoadMoreView2 extends LoadMoreView2 {

    @Override
    public int getLayoutId() {
        return R.layout.quick_view_load_more;
    }

    @Override
    protected int getLoadingViewId() {
        return R.id.load_more_loading_view;
    }

    @Override
    protected int getLoadFailViewId() {
        return R.id.load_more_load_fail_view;
    }

    @Override
    protected int getLoadEndViewId() {
        return R.id.load_more_load_end_view;
    }
}
