package com.ilinklink.tg.base;

import android.app.Activity;
import android.os.Bundle;

import com.ilinklink.tg.mvp.BasePresenter;
import com.ilinklink.tg.mvp.BaseView;
import com.ilinklink.tg.utils.ToastHelper;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

/**
 * BaseMvpFragment
 * mvp fragment界面的父类
 * Created By:Chuck
 * Des:
 * on 2018/12/8 15:46
 */
public abstract class BaseMvpFragment <T extends ViewDataBinding> extends BaseFragment <T>  implements BaseView {


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            mBasePresenters.addAll(initPresenters());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            //遍历,释放业务
            if(mBasePresenters!=null){
                for (BasePresenter b:mBasePresenters){
                    b.onDestory();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /************************************************************************************************************
     * mvp 相关
     * @return
     */
    protected ArrayList<BasePresenter> mBasePresenters=new ArrayList<>();

    //子类返回所有的业务处理者
    public abstract  ArrayList<BasePresenter> initPresenters();

    @Override
    public Activity getActivityContext() {
        return getActivity();
    }

    @Override
    public void showLoading(String msg) {
        mLoadingView.showLoading(msg);
    }

    @Override
    public void dismissLoading() {
        mLoadingView.dismiss();
    }

    @Override
    public void showToast(String msg) {
        ToastHelper.showCustomMessage(msg);
    }
}
