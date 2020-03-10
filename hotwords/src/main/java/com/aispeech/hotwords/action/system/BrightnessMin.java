package com.aispeech.hotwords.action.system;

import com.aispeech.hotwords.action.IAction;
import com.blankj.utilcode.util.BrightnessUtils;
import com.blankj.utilcode.util.LogUtils;

public class BrightnessMin implements IAction {
    @Override
    public void execute() {
        LogUtils.d("execute BrightnessMin");
        // 默认 0-255
        BrightnessUtils.setBrightness(0);
    }
}
