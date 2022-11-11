package com.ilinklink.tg.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ilinklink.tg.communal.AppLoader;
import com.qdong.communal.library.util.BitmapUtil;
import com.ilinklink.app.fw.R;

/**
 * FileName: CourseView
 * <p>
 * Author: WuJH
 * <p>
 * Date: 2020/9/7 15:11
 * <p>
 * Description:
 */
public class CourseView extends RelativeLayout{

    private ImageView iv_cover;
    private TextView tv_content;

    public CourseView(Context context) {
        this(context,null,0);
    }

    public CourseView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public CourseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        
        initView(context);
    }

    private void initView(Context context) {

        View view = LayoutInflater.from(context).inflate(R.layout.view_course, this, true);
        iv_cover = view.findViewById(R.id.iv_cover);
        tv_content = view.findViewById(R.id.tv_content);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //处理wrap_contentde情况
        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(300, 300);
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(300, heightSize);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSize, 300);
        }
    }

    public void setIv_cover(String url){
        BitmapUtil.loadPhoto(AppLoader.getInstance(),url,iv_cover);
    }

    public void setText(String content){
        tv_content.setText(content);
    }
}
