package com.aispeech.hotwords.action.navi;

import com.aispeech.hotwords.action.IAction;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;

import static com.aispeech.hotwords.action.navi.TurnOffNavigation.PKG_NAVI_AMAP;


public class TurnOnNavigation implements IAction {
    @Override
    public void execute() {
        LogUtils.d("execute TurnOnNavigation");

        // 默认打开高德地图
        AppUtils.launchApp(PKG_NAVI_AMAP);
    }
}
