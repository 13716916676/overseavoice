package com.aispeech.hotwords.action.system;

import com.aispeech.hotwords.action.IAction;
import com.blankj.utilcode.util.BrightnessUtils;
import com.blankj.utilcode.util.LogUtils;

public class BrightnessDown implements IAction {
    @Override
    public void execute() {
        LogUtils.d("execute BrightnessDown");
        // 默认 0-255，步进 32
        int currentBrightness = BrightnessUtils.getBrightness();
        int targetBrightness = currentBrightness - 32;
        if (targetBrightness < 0) {
            targetBrightness = 0;
        }
        BrightnessUtils.setBrightness(targetBrightness);
    }
}
