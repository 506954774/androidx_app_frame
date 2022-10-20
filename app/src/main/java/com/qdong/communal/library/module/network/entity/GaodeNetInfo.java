package com.qdong.communal.library.module.network.entity;

import com.google.gson.JsonElement;

import java.io.Serializable;

/**
 * QDongNetInfo
 * 接口返回数据的总模型
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/6/28  11:22
 * Copyright : 趣动智能科技有限公司-版权所有
 **/
public class GaodeNetInfo implements Serializable {

    private static final long serialVersionUID = 19840902L;
    private static final String SUCESS_CODE = "200";
    /**
     * success : false
     * code : 010035
     * msg : 请登录！
     * data : null
     */

    private String errcode;
    private String errmsg;
    private String errdetail;
    private JsonElement data;

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getErrdetail() {
        return errdetail;
    }

    public void setErrdetail(String errdetail) {
        this.errdetail = errdetail;
    }

    public JsonElement getData() {
        return data;
    }

    public void setData(JsonElement data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "GaodeNetInfo{" +
                "errcode='" + errcode + '\'' +
                ", errmsg='" + errmsg + '\'' +
                ", errdetail='" + errdetail + '\'' +
                ", data=" + data +
                '}';
    }
}
