package com.ilinklink.tg.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;


import com.ilinklink.tg.interfaces.CallBack;
import com.ilinklink.tg.utils.SdCardLogUtil;
import com.ilinklink.tg.widget.CircularProgressView;
import com.qdong.communal.library.util.BitmapUtil;
import com.ilinklink.app.fw.R;
import com.ilinklink.app.fw.databinding.DialogCustom3Binding;

import java.text.MessageFormat;

import androidx.databinding.DataBindingUtil;
import jp.wasabeef.blurry.Blurry;

/**
 * 自定义的弹框,模糊效果
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/10/22  11:18
 * Copyright : 趣动智能科技有限公司-版权所有
 **/
public class DialogNormal extends AlertDialog implements View.OnClickListener {

    private Bitmap mBitmap = null;
    private Activity mContext;

    private boolean showDeleteIcon;
    private boolean showCancelBtn;

    private String title;
    private String content;
    private int contentTextSize;
    private String rightBtnText;
    private String leftBtnText;

    private String name;
    private String start;
    private String end;

    private CallBack mCallBack;
    private DialogCustom3Binding mViewBinding;
    private boolean isHtml;

    private boolean isUserWeights = true;//是否使用权重





    public DialogNormal(Activity mContext,  String content,  String leftBtnText, String rightBtnText, CallBack mCallBack ) {

        super(mContext, R.style.MyDialog2);
        this.mContext = mContext;
        this.content = content;
        this.leftBtnText = leftBtnText;
        this.rightBtnText = rightBtnText;
        this.mCallBack = mCallBack;

        try {
            mBitmap = BitmapUtil.getScreenShot(mContext);
        } catch (Exception e) {

        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view;
        view = LayoutInflater.from(mContext).inflate(R.layout.dialog_custom3, null);
        mViewBinding = DataBindingUtil.bind(view);

        mViewBinding.setClick(this);

        Display display = getWindow().getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
//设置dialog的宽高为屏幕的宽高
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);

        this.setContentView(mViewBinding.getRoot(), layoutParams);


        getWindow().setLayout((ViewGroup.LayoutParams.MATCH_PARENT), ViewGroup.LayoutParams.MATCH_PARENT);


        mViewBinding.tvContent.setText(content);
        mViewBinding.customTvLeft.setText(leftBtnText);
        mViewBinding.customTvRight.setText(rightBtnText);


        setCancelable(false);//不可消失

        setListener();

    }

    private void setListener() {

    }

    @Override
    public void show() {
        super.show();
        /**
         * 设置宽度全屏，要设置在show的后面
         */
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getWindow().setAttributes(layoutParams);

        // from Bitmap  第三方库,模糊加载
        try {
            Blurry.with(mContext).from(mBitmap).into(mViewBinding.ivBack);
        } catch (Exception e) {
            e.printStackTrace();
        }

      /*  new Thread(new Runnable() {
            @Override
            public void run() {
                //高斯模糊处理图片
                Bitmap  bitmap = ImageFilter.doBlur(mBitmap, 30, false);
                //处理完成后返回主线程
                mViewBinding.ivBack.post(new Runnable() {
                    @Override
                    public void run() {
                        mViewBinding.ivBack.setImageBitmap(bitmap);
                    }
                });
            }
        }).start();*/

    }

    public static final String TAG="DialogArmSet";
    private int mLeftArmOfForce=0;//单位:斤
    private int mRightArmOfForce=0;//单位:斤
    private  CircularProgressView.CallBack mCallbackLeft=new CircularProgressView.CallBack() {
        @Override
        public void onProgressChanged(double progress, double mAngle) {
            SdCardLogUtil.d(TAG, MessageFormat.format("Left力臂控制控件,回调.progress:{0},mAngle:{1}",progress,mAngle));
            mLeftArmOfForce= (int) (progress*2);
            if(mLeftArmOfForce>200){
                mLeftArmOfForce=200;
            }
        }
    };
    private  CircularProgressView.CallBack mCallbackRight=new CircularProgressView.CallBack() {
        @Override
        public void onProgressChanged(double progress, double mAngle) {
            SdCardLogUtil.d(TAG, MessageFormat.format("Right力臂控制控件,回调.progress:{0},mAngle:{1}",progress,mAngle));
            mRightArmOfForce= (int) (progress*2);
            if(mRightArmOfForce>200){
                mRightArmOfForce=200;
            }
        }
    };

    public int getmLeftArmOfForce() {
        return mLeftArmOfForce;
    }

    public int getmRightArmOfForce() {
        return mRightArmOfForce;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.custom_tv_left://左边按钮返回false
                if(mCallBack!=null){
                    mCallBack.callBack(false);
                    dismiss();
                }
                break;
            case R.id.custom_tv_right://右边按钮返回true
                if(mCallBack!=null){
                    mCallBack.callBack(true);
                    dismiss();
                }
                break;
        }
    }

    public static class Entity{
        public Entity(int mLeftArmOfForce, int mRightArmOfForce) {
            this.mLeftArmOfForce = mLeftArmOfForce;
            this.mRightArmOfForce = mRightArmOfForce;
        }

        private int mLeftArmOfForce=0;//单位:斤
        private int mRightArmOfForce=0;//单位:斤

        public int getmLeftArmOfForce() {
            return mLeftArmOfForce;
        }

        public void setmLeftArmOfForce(int mLeftArmOfForce) {
            this.mLeftArmOfForce = mLeftArmOfForce;
        }

        public int getmRightArmOfForce() {
            return mRightArmOfForce;
        }

        public void setmRightArmOfForce(int mRightArmOfForce) {
            this.mRightArmOfForce = mRightArmOfForce;
        }
    }
}
