package com.qdong.communal.library.module.GuiderActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.spc.pose.demo.R;


import java.util.ArrayList;
import java.util.List;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


/**
 * BaseGuideActivity
 * 引导界面的抽象父类,子类必须实现三个方法:
 * {@link #getGuideImageResourseIds()}:提供看完引导页,点击按钮要跳转的类
 * {@link #getDestinationActivity()}:提供引导界面资源图片数组
 * {@link #onShowGuideFinish()}:引导页看完的回调函数
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/8/27  19:45
 * Copyright : 趣动智能科技有限公司-版权所有
 **/
public  abstract class BaseGuideActivity extends Activity implements ViewPager.OnPageChangeListener,View.OnClickListener {

    private LinearLayout mLlearLayoutRoot;//子类的点击控件的父容器
    private List<ImageView> imageViewList;
    private int basicWidth = -1; // 点与点之间的距离, 默认为-1
    private LinearLayout llPointGroup; // 点的组
    private ImageView mSelectedPoint;
    private Button btnStartExperience;

    //引导页数量以及显示的图片资源id
    int[] imageIDs = getGuideImageResourseIds();
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_guide);

        initView();
    }


    /**
     * @method name:initView
     * @des:初始化控件
     * @param :[]
     * @return type:void
     * @date 创建时间:2016/8/27
     * @author Chuck
     **/
    @SuppressLint("WrongViewCast")
    private void initView() {

        mViewPager = (ViewPager) findViewById(R.id.vp_guide);
        btnStartExperience = (Button) findViewById(R.id.btn_guide_start_experience);
        mLlearLayoutRoot= (LinearLayout) findViewById(R.id.ll_action_view_root);
        mLlearLayoutRoot.setVisibility(View.GONE);
        View actionView=getActionView();
        if(actionView!=null){
            mLlearLayoutRoot.addView(actionView);
        }
        llPointGroup = (LinearLayout) findViewById(R.id.ll_guide_point_group);
        mSelectedPoint = findViewById(R.id.v_guide_selected_point);

        if(getSelectedIndicatorIconResourseId()>0){
            mSelectedPoint.setImageResource(getSelectedIndicatorIconResourseId());
        }

        imageViewList = new ArrayList<ImageView>();

        ImageView iv;
        View view;
        for (int i = 0; i < imageIDs.length; i++) {
            iv = new ImageView(this);
            iv.setBackgroundResource(imageIDs[i]);
            imageViewList.add(iv);

            // 向LinearLayout中添加一个点的控件
            view = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    getResources().getDimensionPixelOffset(R.dimen.guide_point_bulk),
                    getResources().getDimensionPixelOffset(R.dimen.guide_point_bulk));
            if(i != 0) {
                params.leftMargin = 20;
            }
            view.setLayoutParams(params);

            if(getIndicatorIconResourseId()>0){//指示器
                //getIndicatorIconResourseId()
                view.setBackgroundResource(getIndicatorIconResourseId());
            }
            llPointGroup.addView(view);
        }

        // 把Adapter适配器和ViewPager关联起来
        GuideAdapter mAdapter = new GuideAdapter();
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(this);
        //btnStartExperience.setOnClickListener(this);
    }

    class GuideAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // 把对应position位置的Imageview在ViewPager中移除掉
            container.removeView(imageViewList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            // 向ViewPager中添加一个ImageView对象, 并且在此方法中把那个被添加的imageView对象返回
            ImageView imageView = imageViewList.get(position);


            //益拍2.0的逻辑:点击某张图片,进下一步:
            if(position<imageViewList.size()){
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(position+1);
                    }
                });
            }


            container.addView(imageView);
            return imageView;
        }
    }

    /**
     * 当滚动时
     * position 目前被选中的页面的索引
     * positionOffset 移动到下一个页面的比例, 范围是: 0.0 ~ 1.0
     */
    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        if(basicWidth <= 0) {
            // 只有在第一次时, 当滚动时取出基本移动的宽度, 计算方式: 第1个点的左边 - 第0个点的左边 = 距离
            basicWidth = llPointGroup.getChildAt(1).getLeft() - llPointGroup.getChildAt(0).getLeft();
        }

        int leftMargin = (int) ((position + positionOffset) * basicWidth);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                getResources().getDimensionPixelOffset(R.dimen.guide_point_bulk),
                getResources().getDimensionPixelOffset(R.dimen.guide_point_bulk));
        params.leftMargin = leftMargin;
        mSelectedPoint.setLayoutParams(params);
    }

    /**
     * 当新的页面被选中时
     * 如果子类自己提供了事件布局,则默认的btn隐藏
     */
    @Override
    public void onPageSelected(int position) {
        if(position == imageViewList.size() - 1) {//滑动到了最后
            if(mLlearLayoutRoot.getChildCount()==0){//子类自己没有提供事件布局
                btnStartExperience.setVisibility(View.VISIBLE);//展示默认的按钮
                mLlearLayoutRoot.setVisibility(View.GONE);
            }
            else {//子类有自己的事件布局
                btnStartExperience.setVisibility(View.GONE);//默认的按钮隐藏
                mLlearLayoutRoot.setVisibility(View.VISIBLE);//自定义布局展示
            }
        } else {
            btnStartExperience.setVisibility(View.GONE);
            mLlearLayoutRoot.setVisibility(View.GONE);
        }
    }

    /**
     * 当页面滚动的状态改变时
     */
    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {

       /* if(getDestinationActivity()!=null){
            //跳转到xxx Activity
            startActivity(new Intent(this, getDestinationActivity()));
            // 在此函数里修改布尔值
            onShowGuideFinish();
            finish();
        }*/

    }

    protected  int getIndicatorIconResourseId(){
        return R.mipmap.guide_point_gray_bg;
    }
    protected  int getSelectedIndicatorIconResourseId(){
        return R.mipmap.guide_point_bg;
    }

    /**子类提供点击后跳转的类**/
    public abstract Class getDestinationActivity();
    /***子类提供滑动的几张图片**/
    public abstract int[] getGuideImageResourseIds();
    /***引导页结束,回调,通常在里面改变一个布尔值,表示引导页已经展示过了***/
    public abstract void onShowGuideFinish();
    /***实现此方法,提供带事件的view,此view会被放进mLlearLayoutRoot里***/
    public abstract View getActionView();
}
