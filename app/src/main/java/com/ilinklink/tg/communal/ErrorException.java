package com.ilinklink.tg.communal;

/**
 * YpErrorException
 * Created By:WuJH
 * Des:
 * on 2019/1/31 15:34
 */
public class ErrorException extends Exception{

    private String errorCode;
    private String errorMsg;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
