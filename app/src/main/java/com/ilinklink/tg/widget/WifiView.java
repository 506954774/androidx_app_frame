package com.ilinklink.tg.widget;

/**
 * CircularProgressView
 * 不可拖拽的普通环形进度条
 * Created By:Chuck
 * Des:
 * on 2020/9/3 17:46
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;

import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;


import com.spc.pose.demo.R;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;


public class WifiView extends View {

    public static final String TAG="WifiView";

    //没有开启Wifi或开启了Wifi但没有连接
    private static final int LEVEL_NONE = 0;

    //Wifi信号等级（最弱）
    private static final int LEVEL_1 = 1;

    //Wifi信号等级
    private static final int LEVEL_2 = 2;

    //Wifi信号等级
    private static final int LEVEL_3 = 3;

    //Wifi信号等级（最强）
    private static final int LEVEL_4 = 4;

    private int mSignalIntensity=LEVEL_NONE;

    private int centerRadiusHeight;//圆心直径

    private int a=47;//越大,wifi区域跨越的弧度越窄

    private int mBackgroungStartAngle = 180+a; // 背景环起始角度
    private int mBackgroundSweepAngle = 180-2*a; // 背景环划过的绘制角度

    private float mCenterX, mCenterY; // 圆心坐标
    private int mRadius; // 扇形半径

    private Paint mPaintBackground;
    private Paint mPaint;



    //刻度画笔
    private Paint linePaintBackground;

    private int mStrokeWidth; // 画笔宽度


    private RectF mRectFArc;
    private RectF mRectFInnerArc;
    private RectF mRectFInner2Arc;


    private WifiManager wifiManager;

    private  WifiHandler wifiHandler;


    /**
     * 背景色,背景色是白色,则wifi图标会是黑色
     */
    private Type mType= Type.WHITE;

    public WifiView(Context context) {
        this(context, null);
    }

    public WifiView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WifiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiHandler = new WifiHandler(this);
    }

    private void init() {

        mPaintBackground = new Paint();
        mPaintBackground.setAntiAlias(true);
        mPaintBackground.setStrokeCap(Paint.Cap.ROUND);
        mPaintBackground.setColor(mType== Type.WHITE? ContextCompat.getColor(getContext(), R.color.divider):ContextCompat.getColor(getContext(), R.color.divider));

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(mType== Type.BLACK?ContextCompat.getColor(getContext(), R.color.white):ContextCompat.getColor(getContext(), R.color.black));






        linePaintBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaintBackground.setDither(true);


        postDelayed(new Runnable() {
            @Override
            public void run() {
                checkSingal();
            }
        },300);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //centerRadiusHeight=dp2px(20);
        if(centerRadiusHeight==0){
            centerRadiusHeight = (int) (1.0f*getMeasuredWidth()*95/500);
        }

        //圆心的x坐标是控件宽度的1/2,y坐标是控件的高度
        mCenterX =getMeasuredWidth() / 2f;
        mCenterY=getMeasuredHeight()-centerRadiusHeight/2-dp2px(1);

        mRadius = (int) (1.0f*mCenterX/Math.cos(Math.toRadians(a)));


        //几个重要的尺寸完全按比例来控制,不再使用xml配置
        if(mStrokeWidth==0){
            mStrokeWidth = (int) (1.0f*mRadius/7.2f);
        }
        if(mRectFArc==null){
            int mRadius=this.mRadius;
            mRectFArc = getmRectFArc(mRadius);
        }
        if(mRectFInnerArc==null){
            int mRadius= (int) (this.mRadius-1.8f*mStrokeWidth);//"1.5f":这个值越大,间隔越大
            mRectFInnerArc = getmRectFArc(mRadius);
        }
        if(mRectFInner2Arc==null){
            int mRadius= (int) (this.mRadius-3.7f*mStrokeWidth);
            mRectFInner2Arc = getmRectFArc(mRadius);
        }


    }

    @NonNull
    private RectF getmRectFArc(int mRadius) {
        return new RectF(
                (float) (mCenterX-(mRadius)*Math.cos(Math.toRadians(a))-1.0f*mStrokeWidth),
                mCenterY -mRadius,
                (float) (mCenterX +(mRadius)*Math.cos(Math.toRadians(a))+1.0f*mStrokeWidth),
                mCenterY +mRadius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        /**
         * 绘制背景圆环
         */
        drawTheBackgroundArc(canvas);

        /**
         * 绘制信号强度
         */
        drawSignalIntensity(canvas);


    }


    private void drawCenterOfTheCircle(Canvas canvas) {
        mPaintBackground.setStyle(Paint.Style.FILL);
        mPaintBackground.setStrokeWidth(1);
        canvas.drawCircle(mCenterX,mCenterY,centerRadiusHeight/2,mPaintBackground);
    }

    private void drawTheCircle(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.divider));
        paint.setStrokeWidth(dp2px(1));
        //paint.setStrokeWidth(centerRadiusHeight);
        canvas.drawCircle(mCenterX, mCenterY, mRadius, paint);
    }

    private void drawTheBackgroundArc(Canvas canvas) {


        //画出圆心
        mPaintBackground.setStyle(Paint.Style.FILL);
        mPaintBackground.setStrokeWidth(1);
        canvas.drawCircle(mCenterX,mCenterY,centerRadiusHeight/2,mPaintBackground);

        mPaintBackground.setStyle(Paint.Style.STROKE);
        mPaintBackground.setStrokeWidth(mStrokeWidth);


        //画出最外层圆环
        canvas.drawArc(mRectFArc, mBackgroungStartAngle, mBackgroundSweepAngle, false, mPaintBackground);

        //中间圆环
        canvas.drawArc(mRectFInnerArc, mBackgroungStartAngle, mBackgroundSweepAngle, false, mPaintBackground);

        //最内侧圆环
        canvas.drawArc(mRectFInner2Arc, mBackgroungStartAngle, mBackgroundSweepAngle, false, mPaintBackground);
    }

    private void drawSignalIntensity(Canvas canvas) {

        switch (mSignalIntensity){
            case LEVEL_NONE:
                break;
            case LEVEL_1:
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setStrokeWidth(1);
                canvas.drawCircle(mCenterX,mCenterY,centerRadiusHeight/2,mPaint);

                break;
            case LEVEL_2:
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setStrokeWidth(1);
                canvas.drawCircle(mCenterX,mCenterY,centerRadiusHeight/2,mPaint);

                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeWidth(mStrokeWidth);

                canvas.drawArc(mRectFInner2Arc, mBackgroungStartAngle, mBackgroundSweepAngle, false, mPaint);


                break;
            case LEVEL_3:
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setStrokeWidth(1);
                canvas.drawCircle(mCenterX,mCenterY,centerRadiusHeight/2,mPaint);

                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeWidth(mStrokeWidth);

                canvas.drawArc(mRectFInnerArc, mBackgroungStartAngle, mBackgroundSweepAngle, false, mPaint);

                canvas.drawArc(mRectFInner2Arc, mBackgroungStartAngle, mBackgroundSweepAngle, false, mPaint);


                break;
            case LEVEL_4:
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setStrokeWidth(1);
                canvas.drawCircle(mCenterX,mCenterY,centerRadiusHeight/2,mPaint);

                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeWidth(mStrokeWidth);

                canvas.drawArc(mRectFArc, mBackgroungStartAngle, mBackgroundSweepAngle, false, mPaint);

                canvas.drawArc(mRectFInnerArc, mBackgroungStartAngle, mBackgroundSweepAngle, false, mPaint);

                canvas.drawArc(mRectFInner2Arc, mBackgroungStartAngle, mBackgroundSweepAngle, false, mPaint);
                break;
        }


    }


    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
    }

    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                Resources.getSystem().getDisplayMetrics());
    }

    private void logI(String tag,String msg){
        boolean logSwitch=true;
        if (logSwitch)
        Log.i(tag, msg);
    }

    /**
     * 什么颜色的
     * @param mType
     */
    public void setmType(Type mType) {
        this.mType = mType;
        mPaint.setColor(mType== Type.BLACK?ContextCompat.getColor(getContext(), R.color.white):ContextCompat.getColor(getContext(), R.color.black));
    }


    public enum Type{
        NONE,//隐藏
        WHITE,//控件为白色
        BLACK ;//控件为黑色
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter intentFilter = new IntentFilter();
        //Wifi连接状态变化
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        //Wifi信号强度变化
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        getContext().registerReceiver(wifiStateReceiver, intentFilter);
    }

    public void setmSignalIntensity(int mSignalIntensity) {
        this.mSignalIntensity = mSignalIntensity;
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        wifiHandler.removeCallbacksAndMessages(null);
        getContext().unregisterReceiver(wifiStateReceiver);
    }

    private static class WifiHandler extends Handler {

        //虚引用
        private WeakReference<WifiView> stateViewWeakReference;

        WifiHandler(WifiView wifiStateView) {
            stateViewWeakReference = new WeakReference<>(wifiStateView);
        }

        @Override
        public void handleMessage(Message msg) {
            WifiView wifiStateView = stateViewWeakReference.get();
            if (wifiStateView == null) {
                return;
            }
            switch (msg.what) {
                case LEVEL_1:
                    wifiStateView.setmSignalIntensity(LEVEL_1);
                    break;
                case LEVEL_2:
                    wifiStateView.setmSignalIntensity(LEVEL_2);
                    break;
                case LEVEL_3:
                    wifiStateView.setmSignalIntensity(LEVEL_3);

                    break;
                case LEVEL_4:
                    wifiStateView.setmSignalIntensity(LEVEL_4);

                    break;
                case LEVEL_NONE:
                default:
                    wifiStateView.setmSignalIntensity(LEVEL_NONE);
                    break;
            }
        }
    }

    private BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "action " + intent.getAction());
            switch (intent.getAction()) {
                case WifiManager.WIFI_STATE_CHANGED_ACTION:
                    if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING) {
                        wifiHandler.sendEmptyMessage(LEVEL_NONE);
                    }
                    break;
                case WifiManager.RSSI_CHANGED_ACTION:
                    checkSingal();
                    break;
            }
        }
    };

    private void checkSingal() {
        if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
            wifiHandler.sendEmptyMessage(LEVEL_NONE);
            return;
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 5);
        Log.e(TAG, "level:" + level);
        wifiHandler.sendEmptyMessage(level);
    }


}
