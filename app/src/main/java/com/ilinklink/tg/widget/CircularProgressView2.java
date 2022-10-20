package com.ilinklink.tg.widget;

/**
 * CircularProgressView
 * 不可拖拽的普通环形进度条
 * Created By:Chuck
 * Des:
 * on 2020/9/3 17:46
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.spc.pose.demo.R;

import androidx.core.content.ContextCompat;


public class CircularProgressView2 extends View {

    public static final String TAG="CPV2";

    private int mBackgroungStartAngle = 150; // 背景环起始角度
    private int mBackgroundSweepAngle = 240; // 背景环划过的绘制角度

    private float mCenterX, mCenterY; // 圆心坐标
    private int mRadius; // 扇形半径

    private Paint mPaint;
    private Paint mTextPaint;
    private Paint mTextNumPaint;
    private Paint mProgressTextPaint;
    private Paint mProgressTextHintPaint;

    // 圆环上的小球
    private Paint mBitmapPaint;

    //刻度画笔
    private Paint linePaintBackground;

    private int mStrokeWidth; // 画笔宽度

    private int[] mProgressBackgroundColors;
    private int[] mProgressColors;

    private RectF mRectFArc;
    private RectF mRectFInnerArc;

    private String title;//底部文本
    private Rect mRectText;

    private int mScore=0;//得分,[0,100]
    private int mInnerStrokeWidth; // 内部圆环的画笔宽度
    private int mLengthOfTriangle;//指针三角形的边长
    private int mRotateStartAngle = 60; //画布旋转起始角度
    private int mRotateMaxAngle = 240; //三角形最大的旋转角度


    public CircularProgressView2(Context context) {
        this(context, null);
    }

    public CircularProgressView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularProgressView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        title=getContext().getString(R.string.cpv_total_score);

        /**
         * 圆圈bitmap
         */
        mBitmapPaint = new Paint();
        mBitmapPaint.setDither(true); // 设置防抖动
        mBitmapPaint.setFilterBitmap(true); // 对Bitmap进行滤波处理
        mBitmapPaint.setAntiAlias(true); // 设置抗锯齿





        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(sp2px(14));
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(Color.BLACK);

        mProgressTextPaint = new Paint();
        mProgressTextPaint.setAntiAlias(true);
        mProgressTextPaint.setTextSize(sp2px(14));
        mProgressTextPaint.setStyle(Paint.Style.FILL);
        mProgressTextPaint.setColor(Color.BLACK);

        mProgressTextHintPaint = new Paint();
        mProgressTextHintPaint.setAntiAlias(true);
        mProgressTextHintPaint.setTextSize(sp2px(14));
        mProgressTextHintPaint.setStyle(Paint.Style.FILL);
        mProgressTextHintPaint.setColor(Color.BLACK);


        mTextNumPaint = new Paint();
        mTextNumPaint.setAntiAlias(true);
        mTextNumPaint.setColor(Color.RED);
        mTextNumPaint.setTextSize(sp2px(24));
        mTextNumPaint.setStyle(Paint.Style.FILL);
        mTextNumPaint.setTextAlign(Paint.Align.CENTER);
        Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        mTextNumPaint.setTypeface(font);

        linePaintBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaintBackground.setDither(true);


        mProgressBackgroundColors = new int[]{
                ContextCompat.getColor(getContext(), R.color.cor_FFD865),
                ContextCompat.getColor(getContext(), R.color.cor_FEE5F2)
        };

        mProgressColors = new int[]{
                ContextCompat.getColor(getContext(), R.color.cor_FFD865),
                ContextCompat.getColor(getContext(), R.color.cor_FC7EBC1),
                ContextCompat.getColor(getContext(), R.color.cor_9671F5)};




        mRectText = new Rect();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        //调用了setMeasuredDimension既可以获得 中心位置
        mCenterX = mCenterY = getMeasuredWidth() / 2f;
        mRadius = (int) mCenterX;

        mInnerStrokeWidth=dp2px(3);

        //几个重要的尺寸完全按比例来控制,不再使用xml配置
        if(mStrokeWidth==0){
            mStrokeWidth = (int) (1.0f*getMeasuredWidth()*17/266);
        }
        if(mRectFArc==null){
            mRectFArc = new RectF(0 + mStrokeWidth, 0 + mStrokeWidth,
                    getMeasuredWidth() - mStrokeWidth, getMeasuredWidth() - mStrokeWidth);
        }
        if(mRectFInnerArc==null){
            mRectFInnerArc = new RectF(0 + 3*mStrokeWidth, 0 + 3*mStrokeWidth,
                    getMeasuredWidth() - 3*mStrokeWidth, getMeasuredWidth() - 3*mStrokeWidth);
        }

        if(mLengthOfTriangle==0){
            mLengthOfTriangle = (int) (1.0f*getMeasuredWidth()*16/266);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        /**
         * 画出圆心(开发阶段的辅助线)
         */
        //drawCenterOfTheCircle(canvas);

        /**
         * 画出这个圆,控件边距(开发阶段的辅助线)
         */
        //drawTheCircle(canvas);

        /**
         * 画出外层圆环
         */
        drawCircleOutBackground(canvas);

        /**
         * 画出内层圆环
         */
        drawCircleInnerBackground(canvas);


        /**
         * 画出当前得分
         */
        drawProgressText(canvas);

        /**
         * 画出三角形的指针
         */
        drawProgress(canvas);

    }


    private void drawCenterOfTheCircle(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.theme_blue));
        mPaint.setShader(generateSweepGradient());
        canvas.drawLine(mCenterX, mCenterY, mCenterX, mCenterY, mPaint);
    }

    private void drawTheCircle(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.divider));
        paint.setStrokeWidth(dp2px(1));
        canvas.drawCircle(mCenterX, mCenterY, mRadius, paint);
    }

    private void drawCircleOutBackground(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.theme_blue));
        mPaint.setShader(generateSweepGradient());
        canvas.drawArc(mRectFArc, mBackgroungStartAngle, mBackgroundSweepAngle, false, mPaint);
    }

    private void drawCircleInnerBackground(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mInnerStrokeWidth);
        mPaint.setColor(Color.parseColor("#9671F5"));
        mPaint.setShader(null);
        canvas.drawArc(mRectFInnerArc, mBackgroungStartAngle, mBackgroundSweepAngle, false, mPaint);

        /*Paint paint=new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(1);
        canvas.drawRect(mRectFInnerArc,paint);*/
    }






    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
    }

    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                Resources.getSystem().getDisplayMetrics());
    }




    /**
     * 外层背景圆环的渐变色
     *
     * @return
     */
    private SweepGradient generateBackgroundSweepGradient() {

        SweepGradient sweepGradient = new SweepGradient(mCenterX, mCenterY,
                mProgressBackgroundColors,
                new float[]{0.1f, 0.2f}
                //new float[]{0.41f,0.43f,0.66f}
        );

        Matrix matrix = new Matrix();
        matrix.setRotate(mBackgroungStartAngle - 10, mCenterX, mCenterY);
        sweepGradient.setLocalMatrix(matrix);

        return sweepGradient;
    }

    /**
     * 环形进度的渐变色
     *
     * @return
     */
    private SweepGradient generateSweepGradient() {
        mProgressColors = new int[]{
                ContextCompat.getColor(getContext(), R.color.cor_FFD865),
                ContextCompat.getColor(getContext(), R.color.cor_FC7EBC1),
                ContextCompat.getColor(getContext(), R.color.cor_9671F5)};
       /* mProgressColors = new int[]{
                ContextCompat.getColor(getContext(), R.color.cor_9671F5),
                ContextCompat.getColor(getContext(), R.color.cor_FFD865),
                ContextCompat.getColor(getContext(), R.color.cor_FC7EBC1)};
*/
        SweepGradient sweepGradient = new SweepGradient(mCenterX, mCenterY,
                mProgressColors,
                new float[]{0.1f, 0.2f, 0.6f}
                //new float[]{0.41f,0.43f,0.66f}
        );

        Matrix matrix = new Matrix();
        matrix.setRotate(mBackgroungStartAngle - 10, mCenterX, mCenterY);
        sweepGradient.setLocalMatrix(matrix);

        return sweepGradient;
    }




    private void drawProgressText(Canvas canvas) {

        //绘制力度值
        mProgressTextPaint.setColor(Color.parseColor("#9671F5"));
        mProgressTextPaint.setTextSize(1.0f*getWidth()*60/266);

        String text=String.valueOf(mScore);
        mProgressTextPaint.getTextBounds(text, 0, text.length(), mRectText);
        int txtH = mRectText.height();
        int txtW = mRectText.width();
        float[] p = new float[2];
        p[0] = mCenterX-txtW/2;
        p[1] = mCenterY+txtH/2  ;
        mTextPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(text, p[0], p[1] , mProgressTextPaint);

        //绘制:"总评分"
        String bottomTitle=title;
        mProgressTextHintPaint.setColor(Color.parseColor("#333333"));
        mProgressTextHintPaint.setTextSize(getWidth()/10);
        //mProgressTextHintPaint.getTextBounds(mHeaderText, 0, mHeaderText.length(), mRectText);
        float[] p3 = new float[2];
        mProgressTextHintPaint.setTextAlign(Paint.Align.LEFT);
        float v = mProgressTextHintPaint.measureText(bottomTitle);
        p3[0] = mCenterX-v/2;
        p3[1] = (float) (mCenterY+mRadius*Math.cos(Math.toRadians(60))+v/8);//和底下那个弦底部对齐就行
        canvas.drawText(bottomTitle, p3[0], p3[1] , mProgressTextHintPaint);
    }

    /**
     * 画出蓝色三角形
     * @param canvas
     */
    private void drawProgress(Canvas canvas) {

        // 保存之前的画布状态
        canvas.save();

        //把蓝色三角形旋转到指定的角度
        canvas.rotate(mRotateStartAngle+(1.0f*mScore*mRotateMaxAngle/100) ,mCenterX, mCenterX);

        //16//266
        Paint paint = new Paint();
        paint.setStrokeWidth(3);
        paint.setColor(Color.parseColor("#9671F5"));
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);

        //先在最底部画一个等边三角形(一个角正朝向下方),然后根据得分值,旋转一个角度
        //蓝色圆环的半径:
        int r=(getMeasuredWidth() - 6*mStrokeWidth)/2;

        Point a = new Point((int)(mCenterX-mLengthOfTriangle/2), (int)mCenterY+r-mInnerStrokeWidth/2);
        Point b = new Point((int)(mCenterX+mLengthOfTriangle/2), (int)mCenterY+r-mInnerStrokeWidth/2);
        Point c = new Point((int)mCenterX, (int) (mCenterY+r+mLengthOfTriangle*Math.sin(Math.toRadians(60)))-mInnerStrokeWidth/2);

        Path path = new Path();
        path.moveTo(a.x, a.y);
        path.lineTo(b.x, b.y);
        path.lineTo(c.x, c.y);
        path.lineTo(a.x, a.y);
        path.close();
        path.setFillType(Path.FillType. WINDING);
        canvas.drawPath(path, paint);

         canvas.restore();

        //canvas.drawPoint(a.x,a.y,paint);
        //canvas.drawPoint(b.x,b.y,paint);
        //canvas.drawPoint(c.x,c.y,paint);
    }





    private void logI(String tag,String msg){
        boolean logSwitch=true;
        if (logSwitch)
        Log.i(tag, msg);
    }

    public int getmScore() {
        return mScore;
    }

    public void setmScore(int mScore) {
        if(mScore>=0&&mScore<=100){
            this.mScore = mScore;
            invalidate();
        }
    }


}
