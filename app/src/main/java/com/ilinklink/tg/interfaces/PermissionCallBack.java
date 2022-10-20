package com.ilinklink.tg.interfaces;

import com.tbruyelle.rxpermissions2.Permission;

/**
 * PermissionCallBack
 * Created By:WuJH
 * Des:
 * on 2019/4/12 11:20
 */
public interface PermissionCallBack {

    /**
     * 权限通过
     */
    void permissionGranted(Permission permission);

    /**
     * 权限被拒绝,没有勾选不再提示
     */
    void permissionShouldShowRequest(Permission permission);

    /**
     * 权限被拒绝,勾选不再提示
     */
    void permissionNeverAgain(Permission permission);

    void complete();
}
