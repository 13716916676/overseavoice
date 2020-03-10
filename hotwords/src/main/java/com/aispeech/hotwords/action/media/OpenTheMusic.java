package com.aispeech.hotwords.action.media;

import android.content.Intent;

import com.aispeech.hotwords.App;
import com.aispeech.hotwords.action.IAction;
import com.blankj.utilcode.util.LogUtils;

public class OpenTheMusic implements IAction {
    @Override
    public void execute() {
        LogUtils.d("execute OpenTheMusic");
        Intent intent = new Intent("android.intent.action.MUSIC_PLAYER");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.getApp().startActivity(intent);
    }
}
