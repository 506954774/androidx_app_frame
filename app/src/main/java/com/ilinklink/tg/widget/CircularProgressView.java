package com.ilinklink.tg.widget;

/**
 * CircularProgressView
 * 可拖拽的环形进度条
 * Created By:Chuck
 * Des:
 * on 2020/9/3 17:46
 */

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.ilinklink.tg.utils.LogUtil;
import com.spc.pose.demo.R;

import androidx.core.content.ContextCompat;

import static android.animation.ValueAnimator.RESTART;


public class CircularProgressView extends View {

    public static final String TAG="CPV";
    public static final double MAX_PROGRESS=100;// 回调出去的进度最大值

    private int mOutCircularStrokeWidth = 20;//外层环形的宽度,单位:dp
    private int mNumberOfScales = 120; // 刻度的总数量
    private int mNumberOfScalesValue = 7; // 刻度数值的数量(包含最右边那个"关闭")
    private int mPortion = 20; // 一个mSection等分份数

    private int mBackgroungStartAngle = 150; // 背景环起始角度
    private int mBackgroundSweepAngle = 240; // 背景环划过的绘制角度


    private int mStartAngle = 180; // 实际进度条起始的角度
    private int mMaxAngle = 180; // 实际进度条允许滑动的最大角度
    private int mProgress = 0; // 进度 ,不可超过100,半圈[0,100]
    private double mAngle = 0; // 划过的角度 [0,180]

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
    //划过的刻度画笔
    private Paint linePaint1;

    private int mStrokeWidth; // 画笔宽度

    private int[] mProgressBackgroundColors;
    private int[] mProgressColors;

    private RectF mRectFArc;
    private RectF mRectFInnerArc;

    // 圆环上的圆圈
    Bitmap mDragBitmap;
    // 圆圈的宽
    int bitmapWidth;
    // 圆圈的高度
    int bitmapHight;


    private int scaleLineCorlor;//刻度线背景色
    private int scaleLineCorlorSelected;//刻度线选中色
    private int textCorlor;//文本颜色
    private String title;//底部文本
    private String limitHint;//"单杠模式"
    private boolean touchable;//是否支持滑动
    private String[] mTexts;
    private Rect mRectText;
    private String mHeaderText = "kg"; //
    private int mLength2; // 刻度读数顶部的相对圆弧的长度




    public CircularProgressView(Context context) {
        this(context, null);
    }

    public CircularProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

        TypedArray types = context.obtainStyledAttributes(attrs, R.styleable.CircularProgressView);
        scaleLineCorlor = types.getColor(R.styleable.CircularProgressView_scaleLineCorlor, getResources().getColor(R.color.gray_424242));
        textCorlor = types.getColor(R.styleable.CircularProgressView_textCorlor, getResources().getColor(R.color.gray_262626));
        touchable = types.getBoolean(R.styleable.CircularProgressView_touchable, false);
        title = types.getString(R.styleable.CircularProgressView_title );


    }

    private void init() {

        limitHint=getContext().getString(R.string.cpv_limit);

        /**
         * 圆圈bitmap
         */
        mBitmapPaint = new Paint();
        mBitmapPaint.setDither(true); // 设置防抖动
        mBitmapPaint.setFilterBitmap(true); // 对Bitmap进行滤波处理
        mBitmapPaint.setAntiAlias(true); // 设置抗锯齿

        scaleLineCorlorSelected = Color.parseColor("#FA87D1");
        //mStrokeWidth = dp2px(mOutCircularStrokeWidth);



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
        linePaintBackground.setColor(scaleLineCorlor);


        mProgressBackgroundColors = new int[]{
                ContextCompat.getColor(getContext(), R.color.cor_FFD865),
                ContextCompat.getColor(getContext(), R.color.cor_FEE5F2)
        };

        mProgressColors = new int[]{
                ContextCompat.getColor(getContext(), R.color.cor_FFD865),
                ContextCompat.getColor(getContext(), R.color.cor_FC7EBC1),
                ContextCompat.getColor(getContext(), R.color.cor_9671F5)};


        mTexts = new String[mNumberOfScalesValue]; // 需要显示mSection + 1个刻度读数
        int n =  mNumberOfScales  / (mTexts.length-1);
        for (int i = 0; i < mTexts.length; i++) {
            mTexts[i] = String.valueOf(0 + i * n);
            if(i==mTexts.length-1){
                mTexts[i]=getContext().getString(R.string.cpv_turn_off);
            }
        }

        mRectText = new Rect();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        //调用了setMeasuredDimension既可以获得 中心位置
        mCenterX = mCenterY = getMeasuredWidth() / 2f;
        mRadius = (int) mCenterX;

        if(mStrokeWidth==0){
            mStrokeWidth = (int) (1.0f*getMeasuredWidth()*18/358);
        }

        mLength2 = 3*mStrokeWidth+dp2px(10);

        mRectFInnerArc = new RectF();

        mRectFArc = new RectF(0 + mStrokeWidth, 0 + mStrokeWidth,
                getMeasuredWidth() - mStrokeWidth, getMeasuredHeight() - mStrokeWidth);


        /**
         * 滑动按钮bitmap
         */
        //bitmapWidth = Utils.dip2px(context, 30);
        bitmapWidth =2*mStrokeWidth ;
        //bitmapHight = Utils.dip2px(context, 30);
        bitmapHight =2*mStrokeWidth ;
        mDragBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.dial_icon_dot);
        mDragBitmap = Utils.conversionBitmap(mDragBitmap, bitmapWidth, bitmapHight);

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
         * 画出外层圆环的背景
         */
        drawCircleOutBackground(canvas);

        /**
         * 画外边缘圆弧
         */
        drawCircleOut(canvas);

        /**
         * 画出刻度
         */
        drawLine(canvas);

        /**
         * 划过的刻度-红色
         */
        drawLineProgress(canvas, mProgress);

        /**
         * 画出小球
         */
        drawBall(canvas, mProgress);

        /**
         * 画出刻度数
         */
        drawTextCircle(canvas);

        /**
         * 画出当前进度(力臂的kg数)
         */
        drawProgressText(canvas);


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
        mPaint.setShader(generateBackgroundSweepGradient());
        canvas.drawArc(mRectFArc, mBackgroungStartAngle, mBackgroundSweepAngle, false, mPaint);
    }

    private void drawCircleOut(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.theme_blue));
        mPaint.setShader(generateSweepGradient());
        canvas.drawArc(mRectFArc, mStartAngle, mMaxAngle * mProgress / 100, false, mPaint);


      /*  Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.divider));
        paint.setStrokeWidth(dp2px(1));
        canvas.drawRect(mRectFArc, paint);*/
    }

    /**
     * 实现画刻度线的功能
     */
    private void drawLine(final Canvas canvas) {
        // 保存之前的画布状态
        canvas.save();
        // 移动画布，实际上是改变坐标系的位置
        canvas.translate(mCenterX, mCenterX);
        canvas.rotate(90);//把画布转动90度,使第一个刻度在9点钟方向
        // 设置画笔的宽度（线的粗细）
        linePaintBackground.setStrokeWidth(Utils.sp2px(getContext(), 2));

        // 累计叠加的角度
        float sweepAngle = mMaxAngle;
        float a = sweepAngle / mNumberOfScales;//mNumberOfScales是总刻度数量

        for (int i = 0; i <= mNumberOfScales; i++) {//每次画完了,会旋转画布,所以.无论现在在画哪一个刻度,都假设此时正在画第一个刻度,9点钟方向的那个刻度为"0"的刻度线
            if (i == 0 || i == mNumberOfScales) {//想象一下,把画布移动到圆心,并旋转90度,那么画刻度实际就是在圆心画一条垂直的竖线,刻度要和外层环形有间隔,所以减去了2*mStrokeWidth
                //dp2px(14)是大刻度的实际长度
                canvas.drawLine(0, mCenterX - dp2px(14) - 2 * mStrokeWidth, 0, mCenterX - dp2px(5) - 2 * mStrokeWidth, linePaintBackground);
            } else {
                ////dp2px(10)是大刻度的实际长度
                if (i % mPortion == 0) {
                    canvas.drawLine(0, mCenterX - dp2px(14) - 2 * mStrokeWidth, 0, mCenterX - dp2px(5) - 2 * mStrokeWidth, linePaintBackground);
                } else {
                    canvas.drawLine(0, mCenterX - dp2px(10) - 2 * mStrokeWidth, 0, mCenterX - dp2px(5) - 2 * mStrokeWidth, linePaintBackground);
                }
            }
            canvas.rotate(a);
        }
        // 恢复画布状态。
        canvas.restore();
    }

    /**
     * 实现画刻度线的功能
     */
    private void drawLineProgress(final Canvas canvas, int progress) {
        // 保存之前的画布状态
        canvas.save();
        // 移动画布，实际上是改变坐标系的位置
        canvas.translate(mCenterX, mCenterX);
        canvas.rotate(90);//把画布转动90度,使第一个刻度在9点钟方向
        // 设置画笔的宽度（线的粗细）
        linePaintBackground.setStrokeWidth(Utils.sp2px(getContext(), 2));

        // 累计叠加的角度
        float sweepAngle = mMaxAngle;
        float a = sweepAngle / mNumberOfScales;//mNumberOfScales是总刻度数量

        for (int i = 0; i <= mNumberOfScales; i++) {//每次画完了,会旋转画布,所以.无论现在在画哪一个刻度,都假设此时正在画第一个刻度,9点钟方向的那个刻度为"0"的刻度线
            if(progress==0){
                if(i==0){
                    linePaintBackground.setColor(scaleLineCorlorSelected);// 第一个也是红色
                }
                else {
                    linePaintBackground.setColor(scaleLineCorlor);//进度条默认的颜色
                }
            }
            else {//进度大于0,则判断,避免初始化时,第一个刻度误设为红色
                if (i * 100 / mNumberOfScales <= progress ) {//120个刻度,总进度却只有100
                    linePaintBackground.setColor(scaleLineCorlorSelected);//进度小于当前进度,则是红色
                } else {
                    linePaintBackground.setColor(scaleLineCorlor);//进度条默认的颜色
                }
            }

            if (i == 0 || i == mNumberOfScales) {//想象一下,把画布移动到圆心,并旋转90度,那么画刻度实际就是在圆心画一条垂直的竖线,刻度要和外层环形有间隔,所以减去了2*mStrokeWidth
                //dp2px(14)是大刻度的实际长度
                canvas.drawLine(0, mRadius - dp2px(14) - 2 * mStrokeWidth, 0, mRadius - dp2px(5) - 2 * mStrokeWidth, linePaintBackground);
            } else {
                ////dp2px(10)是大刻度的实际长度
                if (i % mPortion == 0) {
                    canvas.drawLine(0, mRadius - dp2px(14) - 2 * mStrokeWidth, 0, mRadius - dp2px(5) - 2 * mStrokeWidth, linePaintBackground);
                } else {
                    canvas.drawLine(0, mRadius - dp2px(10) - 2 * mStrokeWidth, 0, mRadius - dp2px(5) - 2 * mStrokeWidth, linePaintBackground);
                }
            }
            canvas.rotate(a);
        }
        // 恢复画布状态。
        canvas.restore();
    }

    /**
     * 触碰球的位置
     */
    private void drawBall(final Canvas canvas, int progress) {

        // 保存之前的画布状态
        canvas.save();
        // 移动画布，实际上是改变坐标系的位置
        canvas.translate(mCenterX, mCenterX);

        canvas.rotate(1.0f*progress*mMaxAngle/100);

        canvas.drawBitmap(mDragBitmap, -1 * (mCenterX - mStrokeWidth + 0.5f * bitmapWidth), -0.5f * bitmapHight, mBitmapPaint);

        //总共进度是100,180度分为100份,每份 1.0f*180/100,旋转画布,使得小球停止某个角度,和进度条的角度一致
        //canvas.rotate(1.0f*progress*mMaxAngle/100);

        // 恢复画布状态。
        canvas.restore();
    }

    /**
     * 触碰球的位置
     */
    private void drawBall2(final Canvas canvas, int progress) {
        // 保存之前的画布状态
        canvas.save();
        // 移动画布，实际上是改变坐标系的位置
        canvas.translate(mCenterX, mCenterX);
        canvas.rotate(90);//把画布转动90度,使第一个刻度在9点钟方向


        // 先画出一个球,再旋转画布.球就到指定的位置了
        float a = mMaxAngle * progress / 100;//把球画在哪个位置取决于进度
        canvas.drawBitmap(mDragBitmap, 2 * mStrokeWidth - bitmapWidth / 2, getWidth() / 2 - bitmapWidth / 2, mBitmapPaint);
        canvas.rotate(a);
        canvas.restore();

    }

    private void drawTextCircle(Canvas canvas) {
        mTextPaint.setColor(scaleLineCorlor);
        mTextPaint.setTextSize(1.0f*getMeasuredWidth()*12/336);
        float α;
        float[] p;
        float angle =  mMaxAngle  / (mTexts.length-1);//每个数值,转过的角度是30度.刻度是20.但是角度是30
        for (int i = 0; i <=  mTexts.length-1; i++) {
            mTextPaint.getTextBounds(mTexts[i], 0, mTexts[i].length(), mRectText);
            α = mStartAngle + angle * i;
            p = getCoordinatePoint(mRadius - mLength2, α);
            if (α % 360 > 135 && α % 360 < 225) {
                mTextPaint.setTextAlign(Paint.Align.LEFT);
            } else if ((α % 360 >= 0 && α % 360 < 45) || (α % 360 > 315 && α % 360 <= 360)) {
                mTextPaint.setTextAlign(Paint.Align.RIGHT);
            } else {
                mTextPaint.setTextAlign(Paint.Align.CENTER);
            }
            int txtH = mRectText.height();
            if (i <= 1 || i >= mNumberOfScalesValue - 1) {
                canvas.drawText(mTexts[i], p[0], p[1] + txtH / 2, mTextPaint);
            } else if (i == 3) {
                canvas.drawText(mTexts[i], p[0] + txtH / 2, p[1] + txtH, mTextPaint);
            } else if (i == mNumberOfScalesValue - 3) {
                canvas.drawText(mTexts[i], p[0] - txtH / 2, p[1] + txtH, mTextPaint);
            } else {
                canvas.drawText(mTexts[i], p[0], p[1] + txtH, mTextPaint);
            }
        }
    }

    /**
     * 依圆心坐标，半径，扇形角度，计算出扇形终射线与圆弧交叉点的xy坐标
     */
    public float[] getCoordinatePoint(int radius, float angle) {
        float[] point = new float[2];

        double arcAngle = Math.toRadians(angle); //将角度转换为弧度
        if (angle < 90) {
            point[0] = (float) (mCenterX + Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY + Math.sin(arcAngle) * radius);
        } else if (angle == 90) {
            point[0] = mCenterX;
            point[1] = mCenterY + radius;
        } else if (angle > 90 && angle < 180) {
            arcAngle = Math.PI * (180 - angle) / 180.0;
            point[0] = (float) (mCenterX - Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY + Math.sin(arcAngle) * radius);
        } else if (angle == 180) {
            point[0] = mCenterX - radius;
            point[1] = mCenterY;
        } else if (angle > 180 && angle < 270) {
            arcAngle = Math.PI * (angle - 180) / 180.0;
            point[0] = (float) (mCenterX - Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY - Math.sin(arcAngle) * radius);
        } else if (angle == 270) {//270度时,将要居中展示
            int txtW = mRectText.width();
            point[0] = mCenterX-txtW/2;
            point[1] = mCenterY - radius;
        } else {
            arcAngle = Math.PI * (360 - angle) / 180.0;
            point[0] = (float) (mCenterX + Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY - Math.sin(arcAngle) * radius);
        }

        return point;
    }

    private void startAnimator1() {
        final ValueAnimator animator = ValueAnimator.ofInt(0,101,50,101);
        animator.setDuration(5000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                /**
                 * 通过这样一个监听事件，我们就可以获取
                 * 到ValueAnimator每一步所产生的值。
                 *
                 * 通过调用getAnimatedValue()获取到每个时间因子所产生的Value。
                 * */
                Integer value = (Integer) animation.getAnimatedValue();
                update(value);

            }
        });
        animator.setRepeatMode(RESTART);
        animator.setRepeatCount(1000);
        animator.start();
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
     * 根据x,y,已经圆心坐标,计算出角度
     * @param x
     * @param y
     */
    private void update(int x,int y) {

        if(x==mCenterX){//12点钟方向
            mProgress= 50;
        }
        else {
            //y除以该点距离圆心的距离,是该角度的正弦值
            double xSqure = Math.pow(mCenterX - x, 2);
            double ySqure = Math.pow(mCenterY - y, 2);
            double distance= Math.sqrt(xSqure + ySqure);

            double a=Math.asin(Math.abs(mCenterY-y)*1.0f/distance);

            mAngle=Math.toDegrees(a);

            if(x<mCenterX){//90度以内
            }
            else {
                mAngle=180-mAngle;
            }
            int buffer=3;
            int threshold=150;//阈值,超过它,则把小球滑到最右边
            mProgress= (int) (mAngle*100/180);

           /* if(mAngle>threshold&&mAngle<=threshold+buffer){//角度超过阈值(加个3度的buffer,避免过于灵敏带来的不良体验)时,对应的刻度是100kg,进度是,再往右边滑动,则直接改为"关闭"
                mProgress=83;
            }
            if(mAngle>threshold+buffer){
                mProgress=100;
            }*/


        }


        invalidate();

    }

    private double parseProgress(double dgree) {
        try {
            double progress= dgree*2/3;
            String text=String.format("%.3f", progress) ;
            String integer=text.substring(0,text.length()-4);
            int decimal=Integer.parseInt(text.substring(text.length()-3));
            if(decimal<250){
                text=integer+".0";
            }
            else if(decimal>=250&&decimal<750){
                text=integer+".5";
            }
            else  {
                text=(Integer.parseInt(integer)+1)+".0";
            }
            return    Double.parseDouble(text);
        } catch ( Exception e) {
            LogUtil.e(TAG,"解析数值异常:{0}",e.getMessage());
            return 0;
        }
    }

    private void update(float progress) {
        this.mProgress = (int) progress;
        invalidate();
    }

    /**
     * 单位:斤
     * @param jin
     */
    public void updateKg(float jin) {
        if(jin>=200){
            mAngle=180;
            update(100);
        }
        else {
            mAngle=1f*jin/2/120*180;
            update(100f*jin/2/120);
        }
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
        mProgressTextPaint.setColor(textCorlor);
        mProgressTextPaint.setTextSize(1.0f*getMeasuredWidth()*38/358);
        double progress= parseProgress(mAngle);
        if(progress>MAX_PROGRESS){
            progress=MAX_PROGRESS;
        }
        String text=String.format("%.1f", progress) ;
        mProgressTextPaint.getTextBounds(text, 0, text.length(), mRectText);
        float withOfProgress = mProgressTextPaint.measureText(text);//力度的宽度
        float withOf100 = mProgressTextPaint.measureText("100.0");//力度的最大宽度
        mProgressTextHintPaint.setTextSize(1.0f*getMeasuredWidth()*25/358);
        float withOfKg= mProgressTextHintPaint.measureText(mHeaderText);//"kg"字符的宽度

        int txtH = mRectText.height();
        float[] p = new float[2];
        p[0] = mCenterX-(withOfProgress+withOfKg)/2;
        p[1] = mCenterY+txtH/2  ;
        mTextPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(text, p[0], p[1] , mProgressTextPaint);

        //绘制:"kg"
        mProgressTextHintPaint.setColor(textCorlor);
        mProgressTextHintPaint.setTextSize(1.0f*getMeasuredWidth()*25/358);
        //mProgressTextHintPaint.getTextBounds(mHeaderText, 0, mHeaderText.length(), mRectText);
        float[] p2 = new float[2];
        p2[0] = mCenterX+1.0f*(withOf100+withOfKg)/2.0f-withOfKg;//挨着力度值,往右偏2dp即可
        p2[1] = p[1]  ;//和进度数字的底部对齐即可
        mProgressTextHintPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(mHeaderText, p2[0], p2[1] , mProgressTextHintPaint);

        //绘制:"左臂力度"
        if(TextUtils.isEmpty(title)){
            title= getContext().getString(R.string.cpv_title);
        }
        String bottomTitle="";
        if(mAngle>150){
            bottomTitle= limitHint;
        }
        else {
            bottomTitle=title;
        }
        mProgressTextHintPaint.setColor(textCorlor);
        mProgressTextHintPaint.setTextSize(1.0f*getMeasuredWidth()*28/358);
        //mProgressTextHintPaint.getTextBounds(mHeaderText, 0, mHeaderText.length(), mRectText);
        float[] p3 = new float[2];

        mProgressTextHintPaint.setTextAlign(Paint.Align.LEFT);

        float v = mProgressTextHintPaint.measureText(bottomTitle);
        p3[0] = mCenterX-v/2;
        p3[1] = (float) (mCenterY+mRadius*Math.cos(Math.toRadians(60))+v/8);//和底下那个弦底部对齐就行
        canvas.drawText(bottomTitle, p3[0], p3[1] , mProgressTextHintPaint);
    }


    @Override
    public synchronized boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        logI(TAG, "onTouchEvent: x:"+x+",y:"+y);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if(!touchable){//如果设置了不可触摸,则不消费DOWN事件
                    return false;
                }
                if (isOnRing(x, y)) {
                    update(x,y);
                    return true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (y<mCenterY) {//只对上半个圆环监听move事件
                    update(x,y);
                    return true;
                }

            case MotionEvent.ACTION_UP://抬起手指的时候,触发回调
                if(mAngle>150){
                    mProgress=100;
                }
                invalidate();

                if(mCallback!=null&&touchable){
                    mCallback.onProgressChanged(parseProgress(mAngle),mAngle);
                }

                break;

        }
        return super.onTouchEvent(event);
    }

    /**
     * 判断当前触摸屏幕的位置是否位于咱们定的可滑动区域内。
     */
    private boolean isOnRing(float eventX, float eventY) {
        if(eventY>mCenterY+bitmapHight){//只针对上半个圆环的区域监听滑动手势(bitmapHight,是buffer值)
            return false;
        }
        //如果此时距离圆心的距离小于外层圆的半径,大于内层圆的半径,则认为在环内
        boolean result = false;
        int radiusOut =mRadius;
        int radiusInner =mRadius-4*mStrokeWidth;

        double xSqure = Math.pow(mCenterX - eventX, 2);
        double ySqure = Math.pow(mCenterY - eventY, 2);
        double distance= Math.sqrt(xSqure + ySqure);
        result=distance>radiusInner&&distance<radiusOut;

        logI(TAG, "isOnRing: radiusOut:"+radiusOut);
        logI(TAG, "isOnRing: radiusInner:"+radiusInner);
        logI(TAG, "isOnRing: xSqure:"+xSqure);
        logI(TAG, "isOnRing: ySqure:"+ySqure);
        logI(TAG, "isOnRing: distance:"+distance);
        logI(TAG, "isOnRing: result:"+result);
        return result;
    }

    private void logI(String tag,String msg){
        boolean logSwitch=true;
        if (logSwitch)
        Log.i(tag, msg);
    }

    private CallBack mCallback;
    public interface CallBack{
        /**
         * 回调
         * @param progress:力度 [0,100]
         * @param mAngle 角度[0,180]
         */
        void onProgressChanged(double progress,double mAngle);
    }

    public CallBack getmCallback() {
        return mCallback;
    }

    public void setmCallback(CallBack mCallback) {
        this.mCallback = mCallback;
    }
}
