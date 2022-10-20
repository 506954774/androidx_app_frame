package com.ilinklink.tg.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import android.util.AttributeSet;
import android.util.TypedValue;

import com.spc.pose.demo.R;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;


/**
 * @author create by wjh
 *
 *圆形头像,圆角头像
 *
 * @Date 2018/9/30
 */
public class RoundOvalImageView extends AppCompatImageView {


    private Paint mPaint;

    private int mWidth;

    private int mHeight;

    //private int mRadius;//圆半径

    private int mRoundRadius;// 圆角大小

    private BitmapShader mBitmapShader;//图形渲染

    private Matrix mMatrix;

    private int DEFAULT_BORDER_WIDTH = 0;
    private int DEFAULT_BORDER_COLOR = Color.WHITE;

    public static final int TYPE_CIRCLE = 0;// 圆形
    public static final int TYPE_ROUND = 1;// 圆角矩形
    public static final int TYPE_OVAL = 2;//椭圆形

    private int mType;// 记录是圆形还是圆角矩形
    public static final int DEFAUT_ROUND_RADIUS = 10;//默认圆角大小
    private Paint mBorderPaint;
    private final RectF mDrawableRect = new RectF();
    private final RectF mBorderRect = new RectF();
    private float mBorderRadius;
    private float mBorderWidth = 0;
    private  int mBorderColor;
    private float mDrawableRadius;

    public RoundOvalImageView(Context context) {
        this(context, null);
    }

    public RoundOvalImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundOvalImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundOvalImageView, defStyleAttr, 0);

        mBorderWidth = a.getDimensionPixelSize(R.styleable.RoundOvalImageView_borderWidth, DEFAULT_BORDER_WIDTH);
        mBorderColor = a.getColor(R.styleable.RoundOvalImageView_borderColor, DEFAULT_BORDER_COLOR);
        mType = a.getInt(R.styleable.RoundOvalImageView_shapeType,TYPE_CIRCLE);
        mRoundRadius = a.getDimensionPixelSize(R.styleable.RoundOvalImageView_roundRadius,DEFAUT_ROUND_RADIUS);

        a.recycle();

        initView();
    }

    private void initView() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mMatrix = new Matrix();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 如果是绘制圆形，则强制宽高大小一致
        if (mType == TYPE_CIRCLE) {
            mWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());
            //mRadius = mWidth / 2;
            setMeasuredDimension(mWidth, mWidth);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (null == getDrawable()) {
            return;
        }

        setBitmapShader();
        if (mType == TYPE_CIRCLE) {

            canvas.drawCircle(getWidth() / 2, getHeight() / 2, mDrawableRadius, mPaint);
            if (mBorderWidth != 0) {
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, mBorderRadius, mBorderPaint);
            }

        } else if (mType == TYPE_ROUND) {
            mPaint.setColor(Color.RED);
            canvas.drawRoundRect(mDrawableRect, mRoundRadius, mRoundRadius, mPaint);
            if (mBorderWidth != 0) {
                canvas.drawRoundRect(mBorderRect, mRoundRadius, mRoundRadius, mBorderPaint);
            }
        }else if(mType == TYPE_OVAL){

            canvas.drawOval(mDrawableRect, mPaint);

        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);

    }

    /**
     * 设置BitmapShader
     */
    private void setBitmapShader() {
        Drawable drawable = getDrawable();
        if (null == drawable) {
            return;
        }
        Bitmap bitmap = drawableToBitmap(drawable);
        // 将bitmap作为着色器来创建一个BitmapShader
        mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        if(mBorderWidth > 0){
            //有边框
            mBorderPaint = new Paint();
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setAntiAlias(true);
            mBorderPaint.setColor(mBorderColor);
            mBorderPaint.setStrokeWidth(mBorderWidth);

            mBorderRect.set(0, 0, getWidth(), getHeight());
            mBorderRadius = Math.min((mBorderRect.height() - mBorderWidth) / 2,
                    (mBorderRect.width() - mBorderWidth) / 2);

            mDrawableRect.set(mBorderWidth, mBorderWidth, mBorderRect.width() - mBorderWidth,
                    mBorderRect.height() - mBorderWidth);
        }else{

            //没有边框
            mDrawableRect.set(0, 0, getWidth(), getHeight());
        }

        mDrawableRadius = Math.min(mDrawableRect.height() / 2, mDrawableRect.width() / 2);
        
        float scale = 1.0f;
        if (mType == TYPE_CIRCLE) {
            
            // 拿到bitmap宽或高的小值
            int bSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
            scale = mWidth * 1.0f / bSize;

        } else if (mType == TYPE_ROUND || mType == TYPE_OVAL) {
            // 如果图片的宽或者高与view的宽高不匹配，计算出需要缩放的比例；缩放后的图片的宽高，一定要大于我们view的宽高；所以我们这里取大值；
            scale = Math.max(getWidth() * 1.0f / bitmap.getWidth(), getHeight() * 1.0f / bitmap.getHeight());
        }
        // shader的变换矩阵，我们这里主要用于放大或者缩小
        mMatrix.setScale(scale, scale);
        // 设置变换矩阵
        mBitmapShader.setLocalMatrix(mMatrix);
        mPaint.setShader(mBitmapShader);

    }

    /**
     * drawable转bitmap
     *
     * @param drawable
     * @return
     */
    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            return bitmapDrawable.getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }
    /**
     * 单位dp转单位px
     */
    public int dpTodx(int dp){

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, getResources().getDisplayMetrics());
    }

    public int getType() {
        return mType;
    }
    /**
     * 设置图片类型：圆形、圆角矩形、椭圆形
     * @param mType
     */
    public void setType(int mType) {
        if(this.mType != mType){
            this.mType = mType;
            invalidate();
        }

    }
    public int getRoundRadius() {
        return mRoundRadius;
    }
    /**
     * 设置圆角大小
     * @param mRoundRadius
     */
    public void setRoundRadius(int mRoundRadius) {
        if(this.mRoundRadius != mRoundRadius){
            this.mRoundRadius = mRoundRadius;
            invalidate();
        }
    }

    public void setBorderWidth(float borderWidth){
        mBorderWidth = borderWidth;
        invalidate();
    }

    public float getBorderWidth(float borderWidth){
        return mBorderWidth;
    }

    public void setBorderColor(int color){
        mBorderColor = color;
        invalidate();
    }

    public int getBorderWidth(){
        return mBorderColor;
    }
}
