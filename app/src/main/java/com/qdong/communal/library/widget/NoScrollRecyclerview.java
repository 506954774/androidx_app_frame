package com.qdong.communal.library.widget;

import android.content.Context;

import android.util.AttributeSet;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 不滚动的listView
 *
 * @author lsp
 */
public class NoScrollRecyclerview extends RecyclerView {

    public NoScrollRecyclerview(Context context) {
        super(context);

    }

    public NoScrollRecyclerview(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                View.MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
