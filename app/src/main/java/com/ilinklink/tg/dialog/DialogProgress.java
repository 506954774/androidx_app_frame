package com.ilinklink.tg.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;


import com.ilinklink.tg.interfaces.CallBack;
import com.ilinklink.app.fw.R;
import com.ilinklink.app.fw.databinding.DialogProgressBinding;

import androidx.databinding.DataBindingUtil;

/**
 * 自定义的弹框,模糊效果
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/10/22  11:18
 * Copyright : 趣动智能科技有限公司-版权所有
 **/
public class DialogProgress extends AlertDialog implements View.OnClickListener {

    private Activity mContext;

    private boolean showCancelBtn;
    private boolean showIconBoy;

    private String title;
    private String content;
    private int contentTextSize;
    private String commitText;
    private String cancelText;

    private CallBack mCallBack;
    private DialogProgressBinding mViewBinding;
    private int mProgress;

    public DialogProgress(Activity mContext, String content, CallBack mCallBack) {

        super(mContext, R.style.MyDialog2);
        this.mContext = mContext;
        this.showCancelBtn = true;
        this.title = title;
        this.content = content;
        this.contentTextSize = contentTextSize;
        this.commitText = commitText;
        this.cancelText = cancelText;
        this.mCallBack = mCallBack;


    }

    public DialogProgress(Activity mContext, String title, String content, String commitText, String cancelText, CallBack mCallBack) {

        super(mContext, R.style.MyDialog2);
        this.mContext = mContext;
        this.showCancelBtn = true;
        this.title = title;
        this.content = content;
        //this.contentTextSize = contentTextSize;
        this.commitText = commitText;
        this.cancelText = cancelText;
        this.mCallBack = mCallBack;


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_progress, null);
        mViewBinding = DataBindingUtil.bind(view);
        mViewBinding.setClick(this);
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        //设置dialog的宽高为屏幕的宽高
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
        this.setContentView(mViewBinding.getRoot(), layoutParams);
        getWindow().setLayout((ViewGroup.LayoutParams.MATCH_PARENT), ViewGroup.LayoutParams.MATCH_PARENT);

        if (!TextUtils.isEmpty(title)) {
            mViewBinding.tvTitle.setText(title);
        }

        if (!TextUtils.isEmpty(content)) {
            mViewBinding.tvContent.setText(content);
        }

        if (!TextUtils.isEmpty(commitText)) {
            mViewBinding.tvContent.setText(commitText);
        }
        if (!TextUtils.isEmpty(cancelText)) {
            mViewBinding.customTvCancel.setText(cancelText);
        }

        if (contentTextSize > 0) {
            mViewBinding.tvContent.setTextSize(contentTextSize);
        }

        //mViewBinding.ivDelete.setVisibility(showDeleteIcon ? View.VISIBLE : View.GONE);
        mViewBinding.customTvCancel.setVisibility(showCancelBtn ? View.VISIBLE : View.GONE);
        mViewBinding.ivBoy.setVisibility(showIconBoy ? View.VISIBLE : View.GONE);

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

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.custom_tv_cancel:
                this.dismiss();
                if (mCallBack != null) {
                    mCallBack.callBack(false);
                }
                break;
            case R.id.custom_tv_ok:
                //this.dismiss();

                if (mCallBack != null) {
                    mCallBack.callBack(true);
                }
                break;

        }
    }

    /**
     * 更新progress
     * @param progress
     */
    public void setProgress(int progress){
        mViewBinding.progressBar.setProgress(progress);
        mViewBinding.tvProgress.setText(progress + "");
    }

    /**
     * 显示progress视图
     */
    public void showProgressView(){
        mViewBinding.llUpdateContent.setVisibility(View.GONE);
        mViewBinding.llProgressBar.setVisibility(View.VISIBLE);
    }
}
