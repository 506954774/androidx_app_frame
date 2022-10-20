package com.ilinklink.tg.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;


import com.ilinklink.tg.interfaces.CallBack;
import com.spc.pose.demo.R;
import com.spc.pose.demo.databinding.DialogCustomBinding;

import androidx.databinding.DataBindingUtil;

/**
 * 自定义的弹框,模糊效果
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/10/22  11:18
 * Copyright : 趣动智能科技有限公司-版权所有
 **/
public class DialogConstom extends AlertDialog implements View.OnClickListener {

    private Activity mContext;

    private boolean showCancelBtn;
    private boolean showIconBoy;

    private String title;
    private String content;
    private int contentTextSize;
    private String commitText;
    private String cancelText;

    private String name;
    private String start;
    private String end;

    private CallBack mCallBack;
    private DialogCustomBinding mViewBinding;
    private boolean isHtml;


    public DialogConstom(Activity mContext, String content,  CallBack mCallBack) {

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

    public DialogConstom(Activity mContext, boolean showCancelBtn, String title, String content,  CallBack mCallBack) {

        super(mContext, R.style.MyDialog2);
        this.mContext = mContext;
        this.showCancelBtn = showCancelBtn;
        this.title = title;
        this.content = content;
        this.mCallBack = mCallBack;


    }

    public DialogConstom(Activity mContext, boolean showIconBoy, boolean showCancelBtn, String title, String content,  CallBack mCallBack) {

        super(mContext, R.style.MyDialog2);
        this.mContext = mContext;
        this.showCancelBtn = showCancelBtn;
        this.title = title;
        this.content = content;
        this.mCallBack = mCallBack;
        this.showIconBoy = showIconBoy;

    }


    /**
     * title 值为"null"时  表示弹框不需要title
     *
     * @param mContext
     * @param showDeleteIcon
     * @param showCancelBtn
     * @param title
     * @param content
     * @param contentTextSize
     * @param commitText
     * @param cancelText
     * @param mCallBack
     */
    public DialogConstom(Activity mContext, boolean showDeleteIcon, boolean showCancelBtn, String title, String content, int contentTextSize, String commitText, String cancelText, CallBack mCallBack) {

        super(mContext, R.style.MyDialog2);
        this.mContext = mContext;
        this.showCancelBtn = showCancelBtn;
        this.title = title;
        this.content = content;
        this.contentTextSize = contentTextSize;
        this.commitText = commitText;
        this.cancelText = cancelText;
        this.mCallBack = mCallBack;


    }

    /**
     * title 值为"null"时  表示弹框不需要title
     *
     * @param mContext
     * @param showDeleteIcon
     * @param showCancelBtn
     * @param title
     * @param content
     * @param contentTextSize
     * @param commitText
     * @param cancelText
     * @param mCallBack
     */
    public DialogConstom(Activity mContext, boolean showDeleteIcon, boolean showCancelBtn, String title, String content, int contentTextSize, String commitText, String cancelText, CallBack mCallBack, boolean isHtml) {

        super(mContext, R.style.MyDialog2);
        this.mContext = mContext;
        this.showCancelBtn = showCancelBtn;
        this.title = title;
        this.content = content;
        this.contentTextSize = contentTextSize;
        this.commitText = commitText;
        this.cancelText = cancelText;
        this.mCallBack = mCallBack;
        this.isHtml = isHtml;

    }

    /**
     * 自定义弹窗 构造方法
     *
     * @param mContext
     * @param showDeleteIcon
     * @param showCancelBtn
     * @param title
     * @param name
     * @param commitText
     * @param cancelText
     * @param mCallBack
     */
    public DialogConstom(Activity mContext, boolean showDeleteIcon, boolean showCancelBtn, String title, String start, String name, String end, String commitText, String cancelText, CallBack mCallBack) {
        super(mContext, R.style.MyDialog2);
        this.mContext = mContext;
        this.showCancelBtn = showCancelBtn;
        this.title = title;
        this.name = name;
        this.commitText = commitText;
        this.cancelText = cancelText;
        this.mCallBack = mCallBack;
        this.start = start;
        this.end = end;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_custom, null);
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
            if (isHtml) {
                mViewBinding.tvTitle.setText(Html.fromHtml(title));
                mViewBinding.tvTitle.setTextSize(20);
            } else {
                mViewBinding.tvTitle.setText(title);
            }

        }
        if ("null".equals(title)) {
            mViewBinding.tvTitle.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(content)) {
            mViewBinding.tvContent.setText(content);
        }
        //黑名单
        if (!TextUtils.isEmpty(name)) {
            SpannableString spannableString = new SpannableString(start + name + end);
            spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.yellow_fec500)),
                    start.length(),
                    start.length() + name.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new AbsoluteSizeSpan(16, true), start.length(), start.length() + name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mViewBinding.tvContent.setText(spannableString);

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
                this.dismiss();

                if (mCallBack != null) {
                    mCallBack.callBack(true);
                }
                break;

        }
    }
}
