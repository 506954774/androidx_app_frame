package com.ilinklink.tg.interfaces;

import android.graphics.Bitmap;

/**
 * ImageBitmapCallback
 *
 * 责任人:  jibinghao
 * 修改人： jibinghao
 * 创建/修改时间时间: 2019/4/10 4:23 PM
 * Copyright : 全民智慧城市  版权所有
 **/

public interface ImageBitmapCallback {
    void onSuccess(Bitmap bitmap);
    void onFailed(String message);
}
