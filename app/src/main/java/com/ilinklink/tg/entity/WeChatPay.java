package com.ilinklink.tg.entity;

import java.io.Serializable;

/**
 * WeChatPay
 * Created By:Chuck
 * Des:
 * on 2018/11/26 21:06
 */
public class WeChatPay implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nonce_str;
    private String mch_id;
    private String key;
    private String prepay_id;


    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPrepay_id() {
        return prepay_id;
    }

    public void setPrepay_id(String prepay_id) {
        this.prepay_id = prepay_id;
    }


}
