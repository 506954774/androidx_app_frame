package com.ilinklink.tg.base;

import android.app.Activity;
import android.os.Bundle;

import com.ilinklink.tg.mvp.BasePresenter;
import com.ilinklink.tg.mvp.BaseView;
import com.ilinklink.tg.utils.ToastHelper;

import java.util.ArrayList;

import androidx.databinding.ViewDataBinding;

/**
 * BaseMvpActivity
 * * mvp activity界面的父类
 * 这个类,只是为了释放rxJava,还有添加了基本的界面操作,例如loading画面,等
 * Created By:Chuck
 * Des:
 * on 2018/12/7 16:20
 */
public  abstract class BaseMvpActivity<T extends ViewDataBinding> extends BaseActivity <T>  implements BaseView {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try {
            mBasePresenters.addAll(initPresenters());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
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
        return this;
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


    //初始化百度人脸检测模型
    protected void initBaiduFaceModel() {


    }

}
