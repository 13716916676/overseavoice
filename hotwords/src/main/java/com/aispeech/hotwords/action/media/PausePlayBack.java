package com.aispeech.hotwords.action.media;

import android.content.Context;
import android.media.AudioManager;
import android.view.KeyEvent;

import com.aispeech.hotwords.App;
import com.aispeech.hotwords.action.IAction;
import com.blankj.utilcode.util.LogUtils;

public class PausePlayBack implements IAction {
    @Override
    public void execute() {
        LogUtils.d("execute PausePlayBack");

        AudioManager mAudioManager = (AudioManager) App.getApp().getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.dispatchMediaKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE));
        mAudioManager.dispatchMediaKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PAUSE));
    }
}
