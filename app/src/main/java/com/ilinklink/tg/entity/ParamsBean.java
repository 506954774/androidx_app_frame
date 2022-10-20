package com.ilinklink.tg.entity;

import java.io.Serializable;

/**
 * FileName: ParamsBean
 * <p>
 * Author: WuJH
 * <p>
 * Date: 2020/9/27 18:09
 * <p>
 * Description:
 */
public class ParamsBean implements Serializable{

    private String paramId;
    private String paramValue;

    public String getParamId() {
        return paramId;
    }

    public void setParamId(String paramId) {
        this.paramId = paramId;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }
}
