package com.qdong.communal.library.widget.custommask;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.spc.pose.demo.R;



public class CustomMaskLayerView2 extends RelativeLayout {


    private final Context mContext;
    //是否显示
    protected boolean isShowing = false;


    protected TextView mTitle;
    private ImageView mImageViewLoading;//



    public CustomMaskLayerView2(Context context) {
        this(context,null);

    }

    public CustomMaskLayerView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomMaskLayerView2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        initView();
    }

    /**
     * 初始化界面布局
     */
    protected void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.custom_mask_layout2,
                this, true);

        mTitle = ((TextView)findViewById(R.id.tv_msg));
        mImageViewLoading = (ImageView) findViewById(R.id.iv_loading);


        Glide.with(mContext)
                .load(R.mipmap.loading)
                .asGif()
                .into(mImageViewLoading);

    }

    /**
     * 显示加载框
     */
    private void show() {
        if (!isShowing) {
            //设置透明模式



            isShowing = true;
            this.setVisibility(View.VISIBLE);

        }
    }

    /**
     * 显示加载框
     */
    private void show(String msg) {
        if(!TextUtils.isEmpty(msg)){
            mTitle.setText(msg);
        }else{
            mTitle.setText(R.string.loading_title);
        }
        show();
    }


    /**
     * 加载框是否显示
     *
     * @return
     */
    public boolean isShowing() {
        return isShowing;
    }


    /**
     * 延迟隐藏对话框
     * @param delayMillis
     */
    public void dismissDelay(long delayMillis){
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, delayMillis);
    }

    /**
     * 隐藏加载框
     */
    public void dismiss() {
        isShowing = false;
//		AlphaAnimation alp = new AlphaAnimation(1f, 0f);
//		alp.setDuration(duration);
//		this.startAnimation(alp);
        this.setVisibility(View.GONE);
    }



    /**
     * @method name:showLoading
     * @des:  展示loading 画面
     * @param :[]
     * @return type:void
     * @date 创建时间:2016/1/26
     * @author Chuck
     **/
    public void showLoading(){
        show();
    }

    /**
     * @method name:showLoading
     * @des:展示loading 画面
     * @param :[loadingMsg] 提示信息
     * @return type:void
     * @date 创建时间:2016/1/26
     * @author Chuck
     **/
    public void showLoading(String loadingMsg){
        show(loadingMsg);
    }



}
