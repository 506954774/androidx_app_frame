package com.ilinklink.tg.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ilinklink.app.fw.R;

import androidx.core.content.ContextCompat;


/**
 * EventTagView
 * 发布活动标签
 * 责任人:  fanzeyu
 * 修改人： fanzeyu
 * 创建/修改时间时间: 2018/12/29 5:51 PM
 * Copyright : 全民智慧城市  版权所有
 **/
public class EventTagView extends RelativeLayout {
    private boolean isSelect;
    private int mIndex;//坐标,用于判断是否已选择
    private TextView textView;
    private int tag;//标记
    private String value;//值

    public void setSelect(boolean select) {
        isSelect = select;
        if (isSelect) {
            setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.select_colorful));
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        } else {
            setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.shape_round_white_tag));
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.cor333333));
        }
    }

    public void setText(String text){
        if(textView != null){
            textView.setText(text);
        }
    }

    public boolean isSelect(){
        return isSelect;
    }

    public void setIndex(int index){
        this.mIndex = index;
    }

    public int getIndex(){
        return mIndex;
    }


    public int getCTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public EventTagView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.view_event_tag, this, true);
        init();
    }

    /**
     * @param :[]
     * @return type:void
     * @method name:init
     * @des: 加载样式
     * @date 创建时间:2018/12/29
     * @author fanzeyu
     **/
    private void init() {
        //setTextSize(22);
        //setPadding(30, 15, 30, 15);
        textView = (TextView) getChildAt(0);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8, 8, 8, 8);
        setLayoutParams(layoutParams);
        if (isSelect) {
            setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.select_colorful));
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        } else {
            setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.shape_round_white_tag));
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.cor333333));
        }
    }

}
