package com.qdong.communal.library.widget.refresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ilinklink.app.fw.R;


import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

public class CGBHeader extends FrameLayout implements PtrUIHandler {

	private ImageView mImageView;
	private TextView mTitleTextView;

	public CGBHeader(Context context) {
		super(context);
		initView();

	}

	public CGBHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();

	}

	public CGBHeader(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView();

	}

	@SuppressLint("NewApi")
	public CGBHeader(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		initView();

	}

	private void initView() {
		View header = LayoutInflater.from(getContext())
				.inflate(R.layout.layout_list_header_anim, this);
		mImageView = (ImageView) header.findViewById(R.id.loadingImageView);
		mTitleTextView = (TextView) header.findViewById(R.id.text);
	}

	@Override
	public void onUIReset(PtrFrameLayout frame) {

	}

	@Override
	public void onUIRefreshPrepare(PtrFrameLayout frame) {
		mImageView.setBackgroundResource(R.drawable.anim_loading_bolck);
		mTitleTextView.setText("下拉刷新");

	}

	@Override
	public void onUIRefreshBegin(PtrFrameLayout frame) {

		mTitleTextView.setVisibility(VISIBLE);
		mTitleTextView.setText("全力加载ing");
		if(mImageView.getBackground() instanceof AnimationDrawable){
			((AnimationDrawable) mImageView.getBackground()).start();
		}


	}

	@Override
	public void onUIRefreshComplete(PtrFrameLayout frame) {
		if(mImageView.getBackground() instanceof AnimationDrawable){
			((AnimationDrawable) mImageView.getBackground()).stop();
		}

		mTitleTextView.setVisibility(VISIBLE);
		mTitleTextView.setText("更新完成");

	}

	@Override
	public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status,
                                   PtrIndicator ptrIndicator) {
		float percent = Math.min(1f, ptrIndicator.getCurrentPercent());
		if (status == PtrFrameLayout.PTR_STATUS_PREPARE) {
			int index = (int) (percent * 9);
			mImageView.setBackgroundResource(drawable[Math.min(9, index)]);
		}

		final int mOffsetToRefresh = frame.getOffsetToRefresh();
		final int currentPos = ptrIndicator.getCurrentPosY();
		final int lastPos = ptrIndicator.getLastPosY();

		if (currentPos < mOffsetToRefresh && lastPos >= mOffsetToRefresh) {
			if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
				mTitleTextView.setVisibility(VISIBLE);
				mTitleTextView.setText("下拉刷新");
			}
		} else if (currentPos > mOffsetToRefresh && lastPos <= mOffsetToRefresh) {
			if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {

				mTitleTextView.setVisibility(VISIBLE);
				mTitleTextView.setText("释放刷新");
			}
		}

	}

	int[] drawable = { R.mipmap.loading_dot39, R.mipmap.loading_dot38,
			R.mipmap.loading_dot37, R.mipmap.loading_dot36, R.mipmap.loading_dot35,
			R.mipmap.loading_dot34, R.mipmap.loading_dot33, R.mipmap.loading_dot32,
			R.mipmap.loading_dot31, R.drawable.anim_loading_bolck };

}
