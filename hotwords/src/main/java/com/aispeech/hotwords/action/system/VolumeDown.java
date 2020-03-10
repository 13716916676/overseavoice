package com.aispeech.hotwords.action.system;

import android.content.Context;
import android.media.AudioManager;

import com.aispeech.hotwords.App;
import com.aispeech.hotwords.action.IAction;
import com.blankj.utilcode.util.LogUtils;

import static com.aispeech.hotwords.action.system.VolumeUp.STEP;
import static com.aispeech.hotwords.action.system.VolumeUp.setVolume;

public class VolumeDown implements IAction {
    @Override
    public void execute() {
        LogUtils.d("execute VolumeDown");

        AudioManager mAudioManager = (AudioManager) App.getApp().getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int minVolume = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            minVolume = mAudioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC);
        }
        if (currentVolume <= minVolume) {
            LogUtils.w("VolumeUp,currentVolume is min,minVolume=" + minVolume);
            return;
        }

        int targetVolume = currentVolume - STEP;
        if (targetVolume < minVolume) {
            targetVolume = minVolume;
        }
        setVolume(targetVolume);

    }
}
