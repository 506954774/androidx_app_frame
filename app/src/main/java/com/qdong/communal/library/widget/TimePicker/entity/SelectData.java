package com.qdong.communal.library.widget.TimePicker.entity;

import java.io.Serializable;

/**
 * SelectData
 * Created By:Chuck
 * Des:
 * on 2018/9/18 16:49
 */
public class SelectData implements Serializable {

    private static final long serialVersionUID = 1L;


    private String name;//展示名
    private String code;//id 或 code

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return name;
    }
}
