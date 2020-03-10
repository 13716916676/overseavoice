package com.aispeech.hotwords.action.system;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;

import com.aispeech.hotwords.App;
import com.aispeech.hotwords.action.IAction;
import com.blankj.utilcode.util.LogUtils;

public class ScreenOn implements IAction {
    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void execute() {
        LogUtils.d("execute ScreenOn");
        PowerManager powerManager = (PowerManager) App.getApp().getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "");
        wakeLock.acquire();
    }
}
