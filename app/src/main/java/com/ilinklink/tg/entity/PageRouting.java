package com.ilinklink.tg.entity;

import java.io.Serializable;

/**
 * PageRouting
 * 界面跳转实体
 * Created By:Chuck
 * Des:
 * on 2018/10/17 15:00
 */
public class PageRouting implements Serializable {

    private static final long serialVersionUID = 1L;



    private int type;
    private int typeId;
    private int typeFlag;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getTypeFlag() {
        return typeFlag;
    }

    public void setTypeFlag(int typeFlag) {
        this.typeFlag = typeFlag;
    }

    @Override
    public String toString() {
        return "PageRouting{" +
                "type=" + type +
                ", typeId=" + typeId +
                ", typeFlag=" + typeFlag +
                '}';
    }
}
