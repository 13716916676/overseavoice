package com.aispeech.hotwords;

import android.app.Application;
import android.text.TextUtils;
import android.view.Gravity;

import com.aispeech.hotwords.utils.Language;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;

public class App extends Application {

    private static App app;

    public static App getApp() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        app = this;
        Utils.init(app);

        LogUtils.getConfig()
                .setBorderSwitch(false)
                .setLogHeadSwitch(false)
                .setGlobalTag("Hotwords");

        ToastUtils.setGravity(Gravity.CENTER, 0, 200);
        ToastUtils.setMsgTextSize(22);
        ToastUtils.setBgColor(getResources().getColor(R.color.colorAccent));
        ToastUtils.setMsgColor(getResources().getColor(R.color.text_main));
    }

    private String currentLanguage;

    public String getCurrentLanguage() {
        if (TextUtils.isEmpty(currentLanguage)) {
            currentLanguage = SPUtils.getInstance().getString(Constants.SP_KEY_LANGUAGE, Language.getDefaultLanguage());
        }
        return currentLanguage;
    }

    public void setCurrentLanguage(String currentLanguage) {
        this.currentLanguage = currentLanguage;
    }


    private boolean currentWakeupEnable = true;

    public boolean isCurrentWakeupEnable() {
        return currentWakeupEnable;
    }

    public void setCurrentWakeupEnable(boolean currentWakeupEnable) {
        this.currentWakeupEnable = currentWakeupEnable;
    }

}
