package com.ilinklink.tg.entity;

import java.io.Serializable;

/**
 * BaiduPicResponse
 * Created By:Chuck
 * Des:
 * on 2020/11/2 17:11
 */
public class SysConfig implements Serializable {

    private static final long serialVersionUID = 1L;


    private String host;
    private String fileHost;
    private String baiduFaceKey;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getFileHost() {
        return fileHost;
    }

    public void setFileHost(String fileHost) {
        this.fileHost = fileHost;
    }

    public String getBaiduFaceKey() {
        return baiduFaceKey;
    }

    public void setBaiduFaceKey(String baiduFaceKey) {
        this.baiduFaceKey = baiduFaceKey;
    }

    @Override
    public String toString() {
        return "SysConfig{" +
                "host='" + host + '\'' +
                ", fileHost='" + fileHost + '\'' +
                ", baiduFaceKey='" + baiduFaceKey + '\'' +
                '}';
    }
}
