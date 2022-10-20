package com.ilinklink.tg.widget.customClick;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.ilinklink.tg.utils.LogUtil;


/**
 * 埋点控件,友盟统计点击事件
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2018/11/19  16:26
 * Copyright : 2017-2018 深圳令令科技有限公司-版权所有
 **/
@SuppressLint("AppCompatCustomView")
public class CustomClickRelativeLayout extends RelativeLayout {



    private static final String TAG = "CustomClickRelativeLayout";

    private String mCustomId;

    private Resources mResources;


    public CustomClickRelativeLayout(Context context) {
        super(context);
        mResources = getResources();
    }

    public CustomClickRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mResources = getResources();
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                com.spc.pose.demo.R.styleable.ClickView);
        init(mTypedArray);
    }



    public CustomClickRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mResources = getResources();
        //fake();
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                com.spc.pose.demo.R.styleable.ClickView);
        init(mTypedArray);

    }

    //全局定义
    private long lastClickTime = 0L;
    private static final int FAST_CLICK_DELAY_TIME = 1000;  // 快速点击间隔
    @Override
    public boolean performClick() {
        try {
            LogUtil.i(TAG,"performClick(),线程id:"+Thread.currentThread().getId());
            LogUtil.i(TAG,"控件自定义id:"+mCustomId);


            if (System.currentTimeMillis() - lastClickTime < FAST_CLICK_DELAY_TIME){
                LogUtil.i(TAG,"连续点击,return true");
                return true;
            }

            lastClickTime = System.currentTimeMillis(); //下面进行其他操作，比如跳转等 XXX


        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.performClick();
    }

    /**
     * 初始化一些常量
     */
    private void init(TypedArray mTypedArray) {
        mCustomId=mTypedArray.getString(com.spc.pose.demo.R.styleable.ClickView_customId);
        mTypedArray.recycle();
    }

    public String getmCustomId() {
        return mCustomId;
    }

    public void setmCustomId(String mCustomId) {
        this.mCustomId = mCustomId;
    }
}
