package com.qdong.communal.library.widget.TimePicker;

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


import com.qdong.communal.library.widget.TimePicker.timePicker.adapter.ArrayWheelAdapter;
import com.qdong.communal.library.widget.TimePicker.timePicker.interfaces.GenderChoseFinishedListener;
import com.qdong.communal.library.widget.TimePicker.timePicker.interfaces.OnWheelScrollListener;
import com.qdong.communal.library.widget.TimePicker.timePicker.view.WheelView;
import com.ilinklink.app.fw.R;

/**
 * PopWindowChooseYear
 * 从底部弹出的年份选择器
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/3/9  17:47
 *  * Copyright : 趣动智能科技有限公司-版权所有
 **/
public class PopWindowChooseGender extends PopupWindow implements View.OnClickListener{

    private View mMenuView;

    /**
     * 常量
     */
    private static final String TAG ="PopWindowChooseGender";



    /**视图构造器*/
    private LayoutInflater inflater;
    /**上下文对象*/
    private Context context;
    /**选取完成以后的回调接口**/
    private GenderChoseFinishedListener finishedListner;

    /***
     * UI控件
     */
    private Button cancelBtn;
    private Button commitBtn;

    private WheelView genderView;//滚轮控件
    LinearLayout ll;//父容器
    View view=null;
    private int mGender;//0 男  1 女


    public PopWindowChooseGender(Context context, GenderChoseFinishedListener finishedListner) {
        super(context);
        this.finishedListner = finishedListner;
        this.context=context;
        initView();
    }

    private void initView() {
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.popwindow_choose_time,
                null);

        cancelBtn=(Button) mMenuView.findViewById(R.id.btn_cancel);
        commitBtn=(Button) mMenuView.findViewById(R.id.btn_submmit);

        TextView title=mMenuView.findViewById(R.id.tv_title);
        title.setText(context.getString(R.string.picker_chose_gender));

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
        if(id==R.id.btn_cancel){
            if(finishedListner!=null){//非空验证
                finishedListner.handleCancle();
            }
            this.dismiss();
        }
        else if(id==R.id.btn_submmit){
            if(finishedListner!=null){//非空验证
                finishedListner.handleChoose(mGender);
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
        view = inflater.inflate(R.layout.wheel_year_picker, null);//获取视图
        genderView = (WheelView) view.findViewById(R.id.year);


        /**
         *滚轮
         */
        String[] gender = {context.getString(R.string.picker_chose_gender_male),context.getString(R.string.picker_chose_gender_female)};
        ArrayWheelAdapter<String> arrayWheelAdapter = new ArrayWheelAdapter<String>(context,gender);
        genderView.setViewAdapter(arrayWheelAdapter);
        genderView.setCyclic(false);//是否可循环滑动
        genderView.addScrollingListener(scrollListener);
        genderView.setVisibleItems(3);//设置显示行数
        genderView.setCurrentItem(0);
        return view;
    }


    OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
        @Override
        public void onScrollingStarted(WheelView wheel) {

        }

        /**选完之后的回调*/
        @Override
        public void onScrollingFinished(WheelView wheel) {

            try {
               mGender =genderView.getCurrentItem();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

        }

    };

}
