package com.ilinklink.tg.interfaces;

import com.tbruyelle.rxpermissions2.Permission;

/**
 * PermissionCallBack
 * Created By:WuJH
 * Des:
 * on 2019/4/12 11:20
 */
public interface PermissionCallBack2 {

    /**
     * 权限通过
     */
    void permissionGranted(Permission permission);

    /**
     * 权限被拒绝
     */
    void permissionRefused();


}
