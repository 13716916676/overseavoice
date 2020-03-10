package com.aispeech.hotwords.utils;

import android.view.View;

public class AntiShakeUtils {

    /**
     * UI 控件防止快速点击导致部分逻辑问题
     */
    public static void set(final View view, long timeMills) {
        view.setEnabled(false);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, timeMills);
    }
}
