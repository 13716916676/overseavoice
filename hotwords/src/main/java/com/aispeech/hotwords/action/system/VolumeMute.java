package com.aispeech.hotwords.action.system;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;

import com.aispeech.hotwords.App;
import com.aispeech.hotwords.action.IAction;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;

import static android.Manifest.permission.ACCESS_NOTIFICATION_POLICY;

public class VolumeMute implements IAction {
    @Override
    public void execute() {
        LogUtils.d("execute VolumeMute");
        mute(true);
    }

    /**
     * 设置是否禁音
     */
    @SuppressLint("WrongConstant")
    public static void mute(final boolean mute) {
        PermissionUtils.permission(ACCESS_NOTIFICATION_POLICY)
                .callback(new PermissionUtils.SimpleCallback() {
                    @Override
                    public void onGranted() {
                        LogUtils.d("execute Volume permission onGranted.mute=" + mute);
                        try {
                            AudioManager mAudioManager = (AudioManager) App.getApp().getSystemService(Context.AUDIO_SERVICE);
//                            mAudioManager.setRingerMode(mute ? AudioManager.RINGER_MODE_SILENT : AudioManager.RINGER_MODE_NORMAL);
                            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, mute ? AudioManager.ADJUST_UNMUTE : AudioManager.ADJUST_MUTE, 0);//设为静音
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onDenied() {
                        LogUtils.w("execute Volume permission onDenied!" + mute);
                    }
                })
                .request();


    }
}
