package com.qdong.communal.library.jsbridge;

import android.content.Context;
import android.util.AttributeSet;

/**
 * NoScrollWebView
 * Created By:Chuck
 * Des:
 * on 2019/1/9 10:49
 */
public class NoScrollWebView extends BridgeWebView {

    public NoScrollWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoScrollWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public NoScrollWebView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, mExpandSpec);
    }

}
