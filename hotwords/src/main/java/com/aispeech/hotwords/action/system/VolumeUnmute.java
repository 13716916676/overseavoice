package com.aispeech.hotwords.action.system;

import com.aispeech.hotwords.action.IAction;
import com.blankj.utilcode.util.LogUtils;

import static com.aispeech.hotwords.action.system.VolumeMute.mute;

public class VolumeUnmute implements IAction {
    @Override
    public void execute() {
        LogUtils.d("execute VolumeUnmute");
        mute(false);
    }
}
