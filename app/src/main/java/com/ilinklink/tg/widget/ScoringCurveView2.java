package com.ilinklink.tg.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.ilinklink.tg.utils.LogUtil;
import com.qdong.communal.library.util.DensityUtil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by zouxiang on 2016/9/22.
 */

public class ScoringCurveView2 extends View {

    public static final String TAG="ScoringCurveView2";

    public static final int UNIT_PIX=5;
    private float lineSmoothness = 0.2f;
    private float drawScale = 1f;
    //private List<Point> mPointList;

    private int mPaintWidth = 6;


    private int mShadowHeight = 18;//阴影的高度
    private boolean mWithShadow;//是否绘制阴影

    private boolean mTouchable=false;//是否允许滑动
    private boolean mTileByPointsQuantity=true;//按点数,平铺


    private float[] mScores;

    private static final long LOOP_FIRST_DELAY = 200L;
    private static final long LOOP_PERIOD = 1000L;
    private static final int LOOPER_QUANTITY = Integer.MAX_VALUE;
    private boolean mToStopLoop;//是否停止轮询



    int[] mColors = {Color.parseColor("#FFD15D"), Color.parseColor("#FC7EBC"), Color.parseColor("#9671F5"), Color.parseColor("#9671F5")};
    int[] mColorsShadow = {Color.parseColor("#40FFD15D"), Color.parseColor("#40FC7EBC"), Color.parseColor("#409671F5"), Color.parseColor("#409671F5")};
    private Subscription mSubscription;

    public ScoringCurveView2(Context context) {
        super(context);
        scrollCurveView();
    }

    public ScoringCurveView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        scrollCurveView();
    }


    /**
     * 第二个参数是:是否绘制阴影曲线
     * @param scores
     * @param mWithShadow
     */
    public void setPointList(final float[] scores, boolean mWithShadow) {
        this.mWithShadow = mWithShadow;
        this.mScores = scores;
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mMainPaths.clear();
                mMainPaths.addAll(generatePath(scores));
                mShadowPaths = (ArrayList<PathData>) mMainPaths.clone();
                invalidate();

            }
        }, 100);

    }
    /**
     * 第二个参数是:是否绘制阴影曲线
     * @param scores
     * @param mWithShadow
     */
    public void setPointList(final float[] scores, boolean mWithShadow, boolean mTouchable,boolean mTileByPointsQuantity) {
        this.mWithShadow = mWithShadow;
        this.mTouchable = mTouchable;
        this.mTileByPointsQuantity = mTileByPointsQuantity;
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mMainPaths.clear();
                mMainPaths.addAll(generatePath(scores));
                mShadowPaths = (ArrayList<PathData>) mMainPaths.clone();
                invalidate();
            }
        }, 100);



    }

    public void setLineSmoothness(float lineSmoothness) {
        if (lineSmoothness != this.lineSmoothness) {
            this.lineSmoothness = lineSmoothness;
            postInvalidate();
        }
    }

    public void setDrawScale(float drawScale) {
        this.drawScale = drawScale;
        postInvalidate();
    }

    public void startAnimation(long duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "drawScale", 0f, 1f);
        animator.setDuration(duration);
        animator.start();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (mMainPaths == null || mMainPaths.size() == 0)
            return;


        for (PathData path : mMainPaths) {//画出主曲线
            Path dst = new Path();
            dst.rLineTo(0, 0);
            float distance = path.getmPathMeasure().getLength() * drawScale;
            if (path.getmPathMeasure().getSegment(0, distance, dst, true)) {
                //渐变
                float[] position = {0f, 0.3f, 0.6f, 0.8f};
                LinearGradient linearGradient = new LinearGradient(0, getMeasuredHeight(), 0, 0, mColors, position, Shader.TileMode.CLAMP);
                path.getPaint().setShader(linearGradient);
                canvas.drawPath(dst, path.getPaint());
                float[] pos = new float[2];
                path.getmPathMeasure().getPosTan(distance, pos, null);
            }
        }
        if (mWithShadow && mShadowPaths != null && mShadowPaths.size() > 0) {//是否绘制阴影曲线
            canvas.save();
            canvas.translate(0, mShadowHeight);//把画布移动

            for (PathData path : mShadowPaths) {
                Path dst = new Path();
                dst.rLineTo(0, 0);
                float distance = path.getmPathMeasure().getLength() * drawScale;
                if (path.getmPathMeasure().getSegment(0, distance, dst, true)) {
                    //渐变
                    float[] position = {0f, 0.3f, 0.6f, 0.8f};
                    LinearGradient linearGradient = new LinearGradient(0, getMeasuredHeight(), 0, 0, mColorsShadow, position, Shader.TileMode.CLAMP);
                    path.getPaint().setShader(linearGradient);
                    canvas.drawPath(dst, path.getPaint());
                    float[] pos = new float[2];
                    path.getmPathMeasure().getPosTan(distance, pos, null);
                }
            }
            canvas.restore();
        }


    }


    private PathData measurePath(ArrayList<Point> pointList) {
        if (pointList == null || pointList.size() == 0) {
            return null;
        }
        PathData result = new PathData();
        Path path = new Path();
        Path assistPath = new Path();
        float prePreviousPointX = Float.NaN;
        float prePreviousPointY = Float.NaN;
        float previousPointX = Float.NaN;
        float previousPointY = Float.NaN;
        float currentPointX = Float.NaN;
        float currentPointY = Float.NaN;
        float nextPointX;
        float nextPointY;

        final int lineSize = pointList.size();
        for (int valueIndex = 0; valueIndex < lineSize; ++valueIndex) {
            if (Float.isNaN(currentPointX)) {
                Point point = pointList.get(valueIndex);
                currentPointX = point.x;
                currentPointY = point.y;
            }
            if (Float.isNaN(previousPointX)) {
                //是否是第一个点
                if (valueIndex > 0) {
                    Point point = pointList.get(valueIndex - 1);
                    previousPointX = point.x;
                    previousPointY = point.y;
                } else {
                    //是的话就用当前点表示上一个点
                    previousPointX = currentPointX;
                    previousPointY = currentPointY;
                }
            }

            if (Float.isNaN(prePreviousPointX)) {
                //是否是前两个点
                if (valueIndex > 1) {
                    Point point = pointList.get(valueIndex - 2);
                    prePreviousPointX = point.x;
                    prePreviousPointY = point.y;
                } else {
                    //是的话就用当前点表示上上个点
                    prePreviousPointX = previousPointX;
                    prePreviousPointY = previousPointY;
                }
            }

            // 判断是不是最后一个点了
            if (valueIndex < lineSize - 1) {
                Point point = pointList.get(valueIndex + 1);
                nextPointX = point.x;
                nextPointY = point.y;
            } else {
                //是的话就用当前点表示下一个点
                nextPointX = currentPointX;
                nextPointY = currentPointY;
            }

            if (valueIndex == 0) {
                // 将Path移动到开始点
                path.moveTo(currentPointX, currentPointY);
                assistPath.moveTo(currentPointX, currentPointY);
            } else {
                // 求出控制点坐标
                final float firstDiffX = (currentPointX - prePreviousPointX);
                final float firstDiffY = (currentPointY - prePreviousPointY);
                final float secondDiffX = (nextPointX - previousPointX);
                final float secondDiffY = (nextPointY - previousPointY);
                final float firstControlPointX = previousPointX + (lineSmoothness * firstDiffX);
                final float firstControlPointY = previousPointY + (lineSmoothness * firstDiffY);
                final float secondControlPointX = currentPointX - (lineSmoothness * secondDiffX);
                final float secondControlPointY = currentPointY - (lineSmoothness * secondDiffY);
                //画出曲线
                path.cubicTo(firstControlPointX, firstControlPointY, secondControlPointX, secondControlPointY,
                        currentPointX, currentPointY);
                //将控制点保存到辅助路径上
                assistPath.lineTo(firstControlPointX, firstControlPointY);
                assistPath.lineTo(secondControlPointX, secondControlPointY);
                assistPath.lineTo(currentPointX, currentPointY);
            }

            // 更新值,
            prePreviousPointX = previousPointX;
            prePreviousPointY = previousPointY;
            previousPointX = currentPointX;
            previousPointY = currentPointY;
            currentPointX = nextPointX;
            currentPointY = nextPointY;
        }
        PathMeasure pathMeasure = new PathMeasure(path, false);
        result.setmAssistPath(assistPath);
        result.setmPathMeasure(pathMeasure);
        result.setmPath(path);
        result.setmPointList(pointList);
        return result;
    }

    private ArrayList<PathData> mMainPaths = new ArrayList<>();
    private ArrayList<PathData> mShadowPaths = new ArrayList<>();


    private class PathData {
        private ArrayList<Point> mPointList;
        private Path mPath;
        private Path mAssistPath;
        private PathMeasure mPathMeasure;
        private Paint paint = new Paint();

        public PathData() {
            paint.setStrokeWidth(mPaintWidth);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
        }


        public ArrayList<Point> getmPointList() {
            return mPointList;
        }

        public void setmPointList(ArrayList<Point> mPointList) {
            this.mPointList = mPointList;
        }

        public Path getmPath() {
            return mPath;
        }

        public void setmPath(Path mPath) {
            this.mPath = mPath;
        }

        public Path getmAssistPath() {
            return mAssistPath;
        }

        public void setmAssistPath(Path mAssistPath) {
            this.mAssistPath = mAssistPath;
        }

        public PathMeasure getmPathMeasure() {
            return mPathMeasure;
        }

        public void setmPathMeasure(PathMeasure mPathMeasure) {
            this.mPathMeasure = mPathMeasure;
        }


        public Paint getPaint() {
            return paint;
        }

        public void setPaint(Paint paint) {
            this.paint = paint;
        }
    }


    private ArrayList<PathData> generatePath(float scores[]) {
        if (scores == null || scores.length < 2) {
            return new ArrayList<>();
        } else {
            ArrayList<PathData> result = new ArrayList<>();
            ArrayList<Point> temp = generatePoints(scores);
            result.add(measurePath(temp));
            return result;
        }
    }

    private ArrayList<Point> generatePoints(float scores[]) {
        if (scores == null || scores.length < 2) {
            return new ArrayList<>();
        } else {
            ArrayList<Point> result = new ArrayList<>();
            float unitX = 1.0f * getMeasuredWidth() / scores.length;
            if(mTileByPointsQuantity){//按分数点,平铺
                unitX = 1.0f * getMeasuredWidth() / scores.length;
            }
            else {//每隔固定的像素,绘制一个point
                unitX = UNIT_PIX;
            }
            float unitY = 1.0f * (getMeasuredHeight() - mPaintWidth - mShadowHeight) / 100;//画笔有宽度,避免顶部曲线丢失
            for (int i = 0; i < scores.length; i++) {
                result.add(new Point((int) (i * unitX), (int) (mPaintWidth+mShadowHeight + unitY * (100 - scores[i]))));
            }
            return result;
        }
    }


    private float mRowX;
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(!mTouchable){
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mRowX=event.getRawX();
                break;

            case MotionEvent.ACTION_MOVE:
                setX(event.getRawX()-mRowX);
                break;

            case MotionEvent.ACTION_UP://
                //mRowX=event.getRawX();
                break;

        }

        return true;

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mToStopLoop=true;
        try {
            if (mSubscription != null && !mSubscription.isUnsubscribed()) {
                mSubscription.unsubscribe();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @method name:
     * @des:
     * 定时横向滑动控件.场景:
     * 当界面点太多,1090像素的宽度不够用时,横向滑动控件,使得最新的点可以被看到
     * @param :
     * @return type:
     * @date 创建时间:2020/10/14
     * @author Chuck
     **/

    private void scrollCurveView() {

        mSubscription = Observable.interval(
                LOOP_FIRST_DELAY,
                LOOP_PERIOD, TimeUnit.MILLISECONDS)
                .take(LOOPER_QUANTITY)
                .subscribeOn(Schedulers.io())
                .takeUntil(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long aLong) {
                        LogUtil.i(TAG,"================drawCurveView,停止loop的条件:mToStopLoop:"+mToStopLoop+(mToStopLoop?"停止loop":"将继续loop"));
                        return mToStopLoop;
                    }
                })
                .map(new Func1<Long, Long>() {
                    @Override
                    public Long call(Long aLong) {
                        LogUtil.i(TAG,"================drawCurveView,map,call,线程id:{0},轮询次数:{1}",Thread.currentThread().getId(),aLong);
                        return aLong;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long  >() {
                    @Override
                    public void onCompleted() {
                        LogUtil.e(TAG,"================drawCurveView,onCompleted()");
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.e(TAG,"================drawCurveView,onError");
                    }

                    @Override
                    public void onNext(Long aLong) {
                        LogUtil.i(TAG,"================drawCurveView,Observer,onNext,线程id:{0}",Thread.currentThread().getId());

                        LogUtil.i(TAG,"================drawCurveView" +
                                        ",mTileByPointsQuantity:{0}" +
                                        ",getWidth():{1}"
                                ,mTileByPointsQuantity,
                                getWidth() );



                        if(!mTileByPointsQuantity&&mScores!=null){

                             setX(-1*(aLong)*UNIT_PIX);


                            if(mScores.length*UNIT_PIX<DensityUtil.getDisplayWidth(getContext())){
                                return;
                            }
                            else {
                                int quantity=DensityUtil.getDisplayWidth(getContext())/UNIT_PIX;
                                //setX(-1*(mScores.length-quantity)*UNIT_PIX);
                            }



                            if(mScores.length*UNIT_PIX>=getWidth()){
                                int width=(mScores.length*UNIT_PIX)%getWidth();
                                //int time=(scores.length*UNIT_PIX)/getWidth();
                                int scrollX=getWidth()-width;

                                LogUtil.i(TAG,"================drawCurveView" +
                                                ",mTileByPointsQuantity:{0}" +
                                                ",getWidth():{1}" +
                                                ",width:{2}" +
                                                ",scrollX:{3}"
                                        ,mTileByPointsQuantity,
                                        getWidth(),
                                        width,
                                        scrollX);

                               // setX(scrollX);
                            }
                        }
                    }
                });
    }
}
