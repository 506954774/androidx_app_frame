package com.qdong.communal.library.widget.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * @author Jinhe on 6/21/17.
 */

public class HorizontalSwipePtrClassicFrameLayout extends PtrFrameLayout {
    public HorizontalSwipePtrClassicFrameLayout(Context context) {
        super(context);
    }

    public HorizontalSwipePtrClassicFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalSwipePtrClassicFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private float startY;
    private float startX;

    /**
     * 记录viewPager是否拖拽的标记
     */
    private boolean mIsHorizontalMove;

    /**
     * 记录事件是否已被分发
     */
    private boolean isDeal;

    private boolean needHorizontalMove;

    private int mTouchSlop;

    @Override
    public void disableWhenHorizontalMove(boolean disable) {
        super.disableWhenHorizontalMove(disable);

        this.needHorizontalMove = disable;

        if (needHorizontalMove) {
            return;
        }

        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledPagingTouchSlop();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!needHorizontalMove) {
            return super.dispatchTouchEvent(ev);
        }

        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 记录手指按下的位置
                startY = ev.getY();
                startX = ev.getX();

                // 初始化标记
                mIsHorizontalMove = false;

                isDeal = false;

                break;
            case MotionEvent.ACTION_MOVE:
                // 如果已经判断出是否由横向还是纵向处理，则跳出
                if (isDeal) {
                    break;
                }

                // 拦截禁止交给Ptr的 dispatchTouchEvent处理
                mIsHorizontalMove = true;

                // 获取当前手指位置
                float endY = ev.getY();
                float endX = ev.getX();
                float distanceX = Math.abs(endX - startX);
                float distanceY = Math.abs(endY - startY);
                if (distanceX != distanceY) {
                    // 如果X轴位移大于Y轴位移，那么将事件交给右滑控件处理。
                    if (distanceX > mTouchSlop && distanceX > distanceY) {
                        mIsHorizontalMove = true;
                        isDeal = true;
                    } else if (distanceY > mTouchSlop) {
                        mIsHorizontalMove = false;
                        isDeal = true;
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 下拉刷新状态时如果滚动了右滑控件 此时 mIsHorizontalMove 为 true, 会导致 PtrFrameLayout 无法恢复原位
                // 初始化标记
                mIsHorizontalMove = false;

                isDeal = false;

                break;
        }

        if (mIsHorizontalMove) {
            return dispatchTouchEventSupper(ev);
        }

        return super.dispatchTouchEvent(ev);
    }
}
