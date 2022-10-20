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
import android.view.View;

import java.util.ArrayList;

/**
 * ScoringCurveView3
 * 根据屏幕宽度,平铺点
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2020/11/6  9:56
 * Copyright : 2014-2020深圳令令科技有限公司-版权所有
 **/

public class ScoringCurveView3 extends View {


    private float lineSmoothness = 0.2f;
    private float drawScale = 1f;
    //private List<Point> mPointList;

    private int mPaintWidth = 6;


    private int mShadowHeight = 18;//阴影的高度
    private boolean mWithShadow;//是否绘制阴影

    int[] mColors = {Color.parseColor("#FFD15D"), Color.parseColor("#FC7EBC"), Color.parseColor("#9671F5"), Color.parseColor("#9671F5")};
    int[] mColorsShadow = {Color.parseColor("#40FFD15D"), Color.parseColor("#40FC7EBC"), Color.parseColor("#409671F5"), Color.parseColor("#409671F5")};

    public ScoringCurveView3(Context context) {
        super(context);
    }

    public ScoringCurveView3(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * 第二个参数是:是否绘制阴影曲线
     * @param scores
     * @param mWithShadow
     */
    public void setPointList(final float[] scores, boolean mWithShadow) {
        this.mWithShadow = mWithShadow;
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
            float unitY = 1.0f * (getMeasuredHeight() - mPaintWidth - mShadowHeight) / 100;//画笔有宽度,避免顶部曲线丢失
            for (int i = 0; i < scores.length; i++) {
                result.add(new Point((int) (i * unitX), (int) (mPaintWidth+mShadowHeight + unitY * (100 - scores[i]))));
            }
            return result;
        }
    }

}
