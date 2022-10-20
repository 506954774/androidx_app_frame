package com.qdong.communal.library.widget.AdBannerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

import androidx.viewpager.widget.ViewPager;

@SuppressLint("NewApi")
public class ViewPagerScroller extends Scroller
{
	private int mScrollDuration = 1500;


	public void setScrollDuration(int duration)
	{
		this.mScrollDuration = duration;
	}

	public ViewPagerScroller(Context context)
	{
		super(context);
	}

	public ViewPagerScroller(Context context, Interpolator interpolator)
	{
		super(context, interpolator);
	}

	public ViewPagerScroller(Context context, Interpolator interpolator, boolean flywheel)
	{
		super(context, interpolator, flywheel);
	}

	@Override
	public void startScroll(int startX, int startY, int dx, int dy, int duration)
	{
		super.startScroll(startX, startY, dx, dy, mScrollDuration);
	}

	@Override
	public void startScroll(int startX, int startY, int dx, int dy)
	{
		super.startScroll(startX, startY, dx, dy, mScrollDuration);
	}

	public void initViewPagerScroll(ViewPager viewPager)
	{
		try
		{
			Field mScroller = ViewPager.class.getDeclaredField("mScroller");
			mScroller.setAccessible(true);
			mScroller.set(viewPager, this);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
