package com.aispeech.hotwords.action.navi;

import android.annotation.SuppressLint;

import com.aispeech.hotwords.action.IAction;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ProcessUtils;

import static android.Manifest.permission.KILL_BACKGROUND_PROCESSES;

public class TurnOffNavigation implements IAction {
    public static final String PKG_NAVI_AMAP = "com.autonavi.minimap";

    @SuppressLint("WrongConstant")
    @Override
    public void execute() {
        LogUtils.d("execute TurnOffNavigation");

        PermissionUtils.permission(KILL_BACKGROUND_PROCESSES)
                .callback(new PermissionUtils.SimpleCallback() {
                    @Override
                    public void onGranted() {
                        LogUtils.d("execute TurnOffNavigation permission onGranted.");
                        // 默认关闭高德地图
                        ProcessUtils.killBackgroundProcesses(PKG_NAVI_AMAP);
                    }

                    @Override
                    public void onDenied() {
                        LogUtils.w("execute TurnOffNavigation permission onDenied!");
                    }
                })
                .request();

    }

}
