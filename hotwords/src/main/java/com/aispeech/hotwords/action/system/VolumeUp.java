package com.aispeech.hotwords.action.system;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;

import com.aispeech.hotwords.App;
import com.aispeech.hotwords.action.IAction;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;

import static android.Manifest.permission.ACCESS_NOTIFICATION_POLICY;

public class VolumeUp implements IAction {
    public static final int STEP = 10;

    @Override
    public void execute() {
        LogUtils.d("execute VolumeUp");
        AudioManager mAudioManager = (AudioManager) App.getApp().getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        if (currentVolume >= maxVolume) {
            LogUtils.w("VolumeUp,currentVolume is max,maxVolume = " + maxVolume);
            return;
        }
        int targetVolume = currentVolume + STEP;
        if (targetVolume > maxVolume) {
            targetVolume = maxVolume;
        }
        setVolume(targetVolume);
    }

    /**
     * 设置音量
     *
     * @param targetVolume 目标音量
     */
    @SuppressLint("WrongConstant")
    public static void setVolume(final int targetVolume) {
        PermissionUtils.permission(ACCESS_NOTIFICATION_POLICY)
                .callback(new PermissionUtils.SimpleCallback() {
                    @Override
                    public void onGranted() {
                        LogUtils.d("execute Volume permission onGranted.targetVolume = " + targetVolume);
                        try {
                            AudioManager mAudioManager = (AudioManager) App.getApp().getSystemService(Context.AUDIO_SERVICE);
                            if (mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
                                mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                            }
                            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetVolume, 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onDenied() {
                        LogUtils.w("execute Volume permission onDenied!targetVolume = " + targetVolume);
                    }
                })
                .request();


    }
}
