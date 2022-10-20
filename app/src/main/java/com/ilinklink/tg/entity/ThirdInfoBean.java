package com.ilinklink.tg.entity;

import java.io.Serializable;

/**
 * ThirdInfoBean
 * Created By:WuJH
 * Des:
 * on 2019/1/2 14:44
 */
public class ThirdInfoBean implements Serializable{

    private String openId;
    private String headerUrl;
    private String nickName;
    private int thirdType;//第三方类型（1：微信，2：微博）

    public int getThirdType() {
        return thirdType;
    }

    public void setThirdType(int thirdType) {
        this.thirdType = thirdType;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getHeaderUrl() {
        return headerUrl;
    }

    public void setHeaderUrl(String headerUrl) {
        this.headerUrl = headerUrl;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
