package com.qdong.communal.library.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.spc.pose.demo.R;

import com.qdong.communal.library.util.LogUtil;
import com.qdong.communal.library.widget.progressbar.NumberProgressBar;

/**
 * DialogWithProgressbar
 * 带进度条的dialog
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/10/22  11:18
 * Copyright : 趣动智能科技有限公司-版权所有
 **/
public class DialogWithProgressbar extends AlertDialog implements View.OnClickListener{

    private Context mContext;


    private TextView tvTitle;
    private NumberProgressBar progressBar;
    private ProgressBar progressBarRound;

    public DialogWithProgressbar(Context context) {
        super(context, R.style.MyDialog);
        this.mContext=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_progressbar_,null);

        tvTitle =view.findViewById(R.id.tv_title);
        progressBar =view.findViewById(R.id.progressBar);
        progressBarRound=view.findViewById(R.id.progressBarRound);

        view.findViewById(R.id.btn_cancel).setOnClickListener(this);
        view.findViewById(R.id.btn_commit).setOnClickListener(this);

        this.setContentView(view);



        setCancelable(false);//不可消失

        setListener();

    }

    private void setListener() {

    }


    public void setTitleText(String title){
        tvTitle.setText(title);
    }

    public void setProgress(final int percent){

        if(percent>=0&&percent<=progressBar.getMax()){

            LogUtil.e("DialogWithProgressbar","线程id:"+ Thread.currentThread().getId()+",percent:"+percent);


            progressBar.post(new Runnable() {
                @Override
                public void run() {
                    progressBarRound.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(percent);
                }
            });
        }
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_cancel){
            this.dismiss();
        }
        else if(v.getId()==R.id.btn_commit){

        }

    }
}
