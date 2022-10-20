package com.qdong.communal.library.widget.TimePicker.timePicker;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.qdong.communal.library.widget.TimePicker.entity.SelectData;
import com.qdong.communal.library.widget.TimePicker.timePicker.adapter.ArrayWheelAdapter;
import com.qdong.communal.library.widget.TimePicker.timePicker.interfaces.DataProvider;
import com.qdong.communal.library.widget.TimePicker.timePicker.interfaces.OnWheelScrollListener;
import com.qdong.communal.library.widget.TimePicker.timePicker.interfaces.ThreeColumnChoseFinishedListener;
import com.qdong.communal.library.widget.TimePicker.timePicker.view.WheelView;
import com.spc.pose.demo.R;

/**
 * PopWindowChooseDate
 * 从底部弹出的日期选择器
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/3/9  17:47
 *  * Copyright : 趣动智能科技有限公司-版权所有
 **/
public class PopWindowChooseTwoColumn extends PopupWindow implements View.OnClickListener{

    private final DataProvider mDataProvider;
    private final String title;
    private View mMenuView;


    private SelectData[] mFirstDatas;
    private SelectData[] mSecondDatas;

    private SelectData mFirstData;
    private SelectData mSecondData;
    private SelectData mThirdData;


    /**
     * 常量
     */
    private static final String TAG ="ChooseThreeColumn";







    /**视图构造器*/
    private LayoutInflater inflater;
    /**上下文对象*/
    private Context context;
    /**选取完成以后的回调接口**/
    private ThreeColumnChoseFinishedListener finishedListner;

    /***
     * UI控件
     */
    private Button cancelBtn;
    private Button commitBtn;





    private WheelView mFirstPicker;
    private WheelView mSecondPicker;

    LinearLayout ll;//父容器
    View view=null;





    public PopWindowChooseTwoColumn(Context context, String title, DataProvider  mDataProvider, ThreeColumnChoseFinishedListener finishedListner) {
        super(context);
        this.title = title;
        this.mDataProvider = mDataProvider;
        this.finishedListner = finishedListner;
        this.context=context;

        initView();

    }



    private void initView() {
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.popwindow_choose_time,
                null);


        TextView textView=mMenuView.findViewById(R.id.tv_title);
        textView.setText(title);

        cancelBtn=(Button) mMenuView.findViewById(R.id.btn_cancel);
        commitBtn=(Button) mMenuView.findViewById(R.id.btn_submmit);

        ll=(LinearLayout)mMenuView. findViewById(R.id.ll_pickers);

        ll.addView(getDataPick());


        setListener();


        // 设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果

        /** 避免对整体窗口采用动画,分别对两个按钮采取动画 **/
        this.setAnimationStyle(R.style.AnimEventPosterBottom);
        // 实例化一个ColorDrawable颜色为透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

    }

    /**
     * 按钮加点击监听
     */
    private void setListener() {
        cancelBtn.setOnClickListener(this);
        commitBtn.setOnClickListener(this);

    }

    /* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
    @Override
    public void onClick(View v) {

        int id =v.getId();
        if(id== R.id.btn_cancel){
            if(finishedListner!=null){//非空验证
                finishedListner.handleCancle();
            }
            this.dismiss();
        }
        else if(id== R.id.btn_submmit){

            if(finishedListner!=null){//非空验证
                finishedListner.handleChoose(mFirstData,mSecondData,mThirdData);
            }
            this.dismiss();
        }

    }

    /**
     *
     * @method name: getDataPick
     * @des:构造视图
     * @param : @return
     * @return type:View
     * @date 创建时间：2015年7月30日
     * @author Chuck
     */
    private View getDataPick() {



        view = inflater.inflate(R.layout.wheel_two_column_picker, null);//获取视图


        mFirstPicker = (WheelView) view.findViewById(R.id.year);
        mSecondPicker = (WheelView) view.findViewById(R.id.month);


        mFirstDatas=mDataProvider.getFirstColumnDatas();
        mFirstData=mFirstDatas[0];
        ArrayWheelAdapter<SelectData> adapter1=new ArrayWheelAdapter<>(context,mFirstDatas);
        mFirstPicker.setViewAdapter(adapter1);
        //mFirstPicker.setCyclic(true);//是否可循环滑动
        //mFirstPicker.setCyclic(mFirstDatas.length>=3);//是否可循环滑动
        mFirstPicker.setCyclic(false);//是否可循环滑动

        mFirstPicker.addScrollingListener(scrollListener1);
        mFirstPicker.setVisibleItems(3);//设置显示行数


        mSecondDatas=mDataProvider.getSecondColumnDatas(mFirstDatas[0].getCode());
        mSecondData=mSecondDatas[0];
        ArrayWheelAdapter<SelectData> adapter2=new ArrayWheelAdapter<>(context,mSecondDatas);
        mSecondPicker.setViewAdapter(adapter2);
        //mSecondPicker.setCyclic(mSecondDatas.length!=1);//是否可循环滑动
        mSecondPicker.setCyclic(false);//是否可循环滑动
        mSecondPicker.addScrollingListener(scrollListener2);
        mSecondPicker.setVisibleItems(3);//设置显示行数





        mFirstPicker.setCurrentItem(0);
        mSecondPicker.setCurrentItem(0);


        return view;
    }




    /**
     * 重置第二个滚轮
     */
    private void resetSecondPicker(String firstDataCode) {
        mSecondDatas=mDataProvider.getSecondColumnDatas(firstDataCode);
        mSecondData=mSecondDatas[0];
        ArrayWheelAdapter<SelectData> adapter2=new ArrayWheelAdapter<>(context,mSecondDatas);
        //mSecondPicker.setCyclic(mSecondDatas.length>=3);//是否可循环滑动
        mSecondPicker.setViewAdapter(adapter2);
        mSecondPicker.setCyclic(false);//是否可循环滑动

        mSecondPicker.setCurrentItem(0);
    }







    OnWheelScrollListener scrollListener1 = new OnWheelScrollListener() {
        @Override
        public void onScrollingStarted(WheelView wheel) {

        }

        /**选完之后的回调*/
        @Override
        public void onScrollingFinished(WheelView wheel) {

            try {
                mFirstData=mFirstDatas[mFirstPicker.getCurrentItem()];
                resetSecondPicker(mFirstData.getCode());


                // mSecondData=mSecondDatas[mSecondPicker.getCurrentItem()];
               // mThirdData=mThirdDatas[mThirdPicker.getCurrentItem()];

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

        }


    };
    OnWheelScrollListener scrollListener2 = new OnWheelScrollListener() {
        @Override
        public void onScrollingStarted(WheelView wheel) {

        }

        /**选完之后的回调*/
        @Override
        public void onScrollingFinished(WheelView wheel) {

            try {
                mSecondData=mSecondDatas[mSecondPicker.getCurrentItem()];



            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

        }


    };




}
