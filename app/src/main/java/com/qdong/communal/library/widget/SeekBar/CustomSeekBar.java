/**
 * StarEvaluateView.java
 * Created by doudou on 2015年7月28日
 */
package com.qdong.communal.library.widget.SeekBar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import com.qdong.communal.library.util.DateFormatUtil;
import com.ilinklink.app.fw.R;

/**
 *
 * @des:播放器,自定义进度条
 * @param :
 * @return type:
 * @date 创建时间:2018/12/29
 * @author Chuck
 **/
public class CustomSeekBar extends RelativeLayout implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {


    public static final String TAG="CustomSeekBar";

    private long mDuration;
	private Context mContext;

	private ImageView mImageView;
	private TextView mTvTime;
	private TextView mTvDuration;
	private SeekBar  mSeekbar;


	private CallBack mCallBack;

	public CustomSeekBar(Context context) {
		super(context);
		initView(context);
	}

	public CustomSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);

	}

	public CustomSeekBar(Context context, AttributeSet attrs, int defStyleAttr){
		super(context, attrs, defStyleAttr);
		initView(context);

	}



	/**
	 * @method name:initView
	 * @des:初始化布局
	 * @param :[]
	 * @return type:void
	 * @date 创建时间:2016/8/25
	 * @author Chuck
	 **/
	private void initView(Context context) {
		mContext=context;
		LayoutInflater.from(mContext).inflate(R.layout.custom_seek_bar_1, this, true);
		mImageView =findViewById(R.id.iv_play);
		mTvTime=findViewById(R.id.tv_time);
		mTvDuration=findViewById(R.id.tv_duration);
		mSeekbar=findViewById(R.id.timeline);


		mSeekbar.setOnSeekBarChangeListener(this);

	}

	public void setData(long duration){
	    if(duration<=0)return;
        mDuration =duration;
		mTvDuration.setText(DateFormatUtil.milesecsToDHM(duration));
		mTvTime.setText("00:00");
		mSeekbar.setProgress(0);
	}

	public void stop(){
		mImageView.setImageResource(R.mipmap.icon_video_start);
	}

	public void setProgress(long time){
	    if(time<=0 || time>mDuration){
	        return;
        }


        mTvTime.setText(DateFormatUtil.milesecsToDHM(time));
        mSeekbar.setProgress((int) (100f*time/mDuration));
		mImageView.setImageResource(R.mipmap.icon_video_stop);

    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.i(TAG,"onProgressChanged,fromUser:"+fromUser);
        Log.i(TAG,"onProgressChanged,progress:"+progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
		Log.i(TAG,"onStartTrackingTouch:progress:"+seekBar.getProgress());

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {//离手的时刻回调
		Log.i(TAG,"onStopTrackingTouch:progress:"+seekBar.getProgress());

		if(mCallBack!=null){
			mCallBack.onStopTrackingTouch(seekBar);
		}

    }

    public void setListener(CallBack callBack){
		this.mCallBack=callBack;
		mImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCallBack.onClick2(mImageView);
			}
		});
	}

	@Override
	public void onClick(View v) {

	}

	public interface CallBack{
		void onStopTrackingTouch(SeekBar seekBar);//离手瞬间,事件抛给界面
		void onClick2(View v);//点击播放按钮的事件转交给界面
	}
}
