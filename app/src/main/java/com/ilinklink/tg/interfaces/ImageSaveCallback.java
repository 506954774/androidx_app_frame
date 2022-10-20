package com.ilinklink.tg.interfaces;

/**
 * ImageSaveCallback
 * Created By:Chuck
 * Des:
 * on 2019/1/23 11:54
 */
public interface ImageSaveCallback {
    void onSuccess(String filePath);
    void onFailed(String message);
}
