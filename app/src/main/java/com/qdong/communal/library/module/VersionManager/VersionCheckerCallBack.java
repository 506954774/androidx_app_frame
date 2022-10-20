package com.qdong.communal.library.module.VersionManager;

/**
 * LoadingViewProvider
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2017/6/13  19:44
 * Copyright : 2014-2016 深圳令令科技有限公司-版权所有
 */
public interface VersionCheckerCallBack {
    void showLoadingView();//转圈,检查升级时转圈
    void dismissLoadingView();
    void onError();
    void noNewVersion();//无新版本时回调
    void forceCloseApp();//强制关闭app
    void onDismissed();
    void onProgressUpdate(String... values);//下载进度
}
