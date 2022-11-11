package com.ilinklink.tg.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.ilinklink.app.fw.R;
import com.ilinklink.app.fw.databinding.BaseActivitySimpleListBinding;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;


/**
 * BaseSimpleListActivty
 * 简单的列表基类,默认已经展示出了统一的title,在{@link #initTtitleView()}里具体设置按钮和文本
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/9/13  17:56
 * Copyright : 全民智慧城市 版权所有
 **/
public abstract class BaseSimpleListActivty extends BaseActivity<BaseActivitySimpleListBinding> {


    @Override
    protected boolean showActiveDialog() {
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setIsTitleBar(isHasTitle());
        setContentView(R.layout.base_activity_simple_list);
        initTtitleView();
        Fragment fragment = getmFragment();
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.rl_main_fragment, fragment).commitAllowingStateLoss();
        }
        mViewBind.layoutMain.setFocusable(true);
    }

    /**
     * 提供被填充的碎片
     **/
    public abstract Fragment getmFragment();

    /**
     * title初始化,返回键,title文本,右边按钮的设置
     **/
    public abstract void initTtitleView();

    protected boolean isHasTitle() {
        return true;
    }

    /**
     * @param :[]
     * @return type:int
     *
     * @method name:getStatusBarColor
     * @des:子类可以重写这个,修改状态栏的颜色
     * @date 创建时间:2016/7/7
     * @author Chuck
     **/
    @Override
    protected int getStatusBarColor() {
        return Color.parseColor("#ffffffff");
    }
}
