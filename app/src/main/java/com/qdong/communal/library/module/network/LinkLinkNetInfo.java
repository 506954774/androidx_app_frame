package com.qdong.communal.library.module.network;

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
public class LinkLinkNetInfo implements Serializable {

    private static final long serialVersionUID = 19840902L;
    public static final String SUCESS_CODE = "000000";
    public static final String FAIL_CODE = "000001";
    /**
     * success : false
     * errorCode : 010035
     * message : 请登录！
     * result : null
     */

    private boolean success=true;
    private String errorCode;

    //0表示成功
    private String code;
    private String message;
    private JsonElement data;


    /**客户端定义的属性actionType,这个属性不是服务器定的.是为了给同一个接口赋予不同的action.比如同一个列表请求,
     * 我们可以定义不同的动作,如刷新,加载,过滤刷新等**/
    private int actionType;

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public boolean isSuccess() {
       /* if(TextUtils.isEmpty(errorCode)){
            return true;
        }
        return SUCESS_CODE.equals(errorCode);*/
        //return code==0;
        return SUCESS_CODE.equals(code);
    }


    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }
    public String getMsg() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JsonElement getData() {
        return data;
    }

    public void setData(JsonElement data) {
        this.data = data;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private Object desc=0;

    public Object getDesc() {
        return desc;
    }

    public void setDesc(Object desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "LinkLinkNetInfo{" +
                "success=" + success +
                ", errorCode='" + errorCode + '\'' +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", actionType=" + actionType +
                '}';
    }
}
