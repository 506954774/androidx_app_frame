package com.ilinklink.tg.interfaces;

/**
 * GetVerifyCodeCallback
 * Created By:WuJH
 * Des:获取验证码的回调
 * on 2018/12/13 10:37
 */
public interface GetVerifyCodeCallback<LinkLinkNetInfo> {

    void getVerifyCodeSuccess(LinkLinkNetInfo linkLinkNetInfo);
    void getVerifyCodeFail(String message);

}
