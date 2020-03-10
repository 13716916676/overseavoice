package com.aispeech.hotwords.action.system;

import com.aispeech.hotwords.action.IAction;
import com.blankj.utilcode.util.LogUtils;

public class CloseTheBluetooth implements IAction {
    @Override
    public void execute() {
        LogUtils.d("execute CloseTheBluetooth");
    }
}
