package com.aispeech.hotwords.speech;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.aispeech.DUILiteSDK;
import com.aispeech.hotwords.BuildConfig;
import com.aispeech.hotwords.Constants;
import com.aispeech.hotwords.R;
import com.aispeech.hotwords.utils.Language;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.MessengerUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;

/**
 * @author aispeech
 */
public class LiteService extends Service {

    private Hotwords hotwords;
    private boolean isAuth;

    @Override
    public void onCreate() {
        super.onCreate();

        hotwords = new Hotwords();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());

        initSDK();

        MessengerUtils.register();

        MessengerUtils.subscribe(Constants.KEY_MSG, new MessengerUtils.MessageCallback() {
            @Override
            public void messageCall(Bundle data) {
                LogUtils.d("LiteService,messageCall=" + data.toString());
                if (data.containsKey(Constants.MSG_START_ENGINE)) {
                    if (hotwords != null) {
                        hotwords.setHotwordsEnable(true);
                        hotwords.start();
                    }
                    SPUtils.getInstance().put(Constants.SP_KEY_WAKEUP, true);
                } else if (data.containsKey(Constants.MSG_STOP_ENGINE)) {
                    if (hotwords != null) {
                        hotwords.setHotwordsEnable(false);
                        hotwords.stop();
                    }
                    SPUtils.getInstance().put(Constants.SP_KEY_WAKEUP, false);
                } else if (data.containsKey(Constants.MSG_CHANGE_LANGUAGE)) {
                    String shortName = data.getString(Constants.MSG_CHANGE_LANGUAGE, Language.getDefaultLanguage());
                    SPUtils.getInstance().put(Constants.SP_KEY_LANGUAGE, shortName);

                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.MSG_CURRENT_LANGUAGE, shortName);
                    MessengerUtils.post(Constants.KEY_MSG, bundle);

                    if (hotwords != null) {
                        hotwords.changeEngine(shortName);
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
//        MessengerUtils.unsubscribe(Constants.KEY_MSG);
        MessengerUtils.unregister();

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, Service.START_FLAG_REDELIVERY, startId);

        boolean wakeupEnable = SPUtils.getInstance().getBoolean(Constants.SP_KEY_WAKEUP, true);
        String language = SPUtils.getInstance().getString(Constants.SP_KEY_LANGUAGE, Language.getDefaultLanguage());
        hotwords.setHotwordsEnable(wakeupEnable);
        hotwords.setLanguage(language);

        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.MSG_WAKEUP_STATE, wakeupEnable);
        bundle.putString(Constants.MSG_CURRENT_LANGUAGE, language);
        MessengerUtils.post(Constants.KEY_MSG, bundle);
        LogUtils.e("onStartCommand,isAuth= " + isAuth + ",wakeupEnable=" + wakeupEnable + ",language=" + language);


        return START_STICKY;
    }


    /**
     * 启动时自动授权
     */
    private void initSDK() {
        DUILiteSDK.setParameter(DUILiteSDK.KEY_AUTH_TIMEOUT, "5000");//设置授权连接超时时长，默认5000ms
        final boolean isAuthorized = DUILiteSDK.isAuthorized(getApplicationContext());//查询授权状态，DUILiteSDK.init之后随时可以调
        LogUtils.d("DUILite SDK is isAuthorized ？ " + isAuthorized);

        String core_version = DUILiteSDK.getCoreVersion();//获取内核版本号
        LogUtils.d("core version is: " + core_version);

        DUILiteSDK.setParameter(DUILiteSDK.KEY_UPLOAD_AUDIO_LEVEL, DUILiteSDK.UPLOAD_AUDIO_LEVEL_NONE);//默认不上传预唤醒和唤醒音频

        DUILiteSDK.setParameter("DEVICE_ID", Build.SERIAL);

        //设置SDK录音模式
        DUILiteSDK.setAudioRecorderType(DUILiteSDK.TYPE_COMMON_MIC);//单麦模式
//        if (BuildConfig.DEBUG) {
        DUILiteSDK.openLog();//须在init之前调用.同时会保存日志文件在/sdcard/duilite/DUILite_SDK.log
//        }

        DUILiteSDK.init(getApplicationContext(),
                BuildConfig.apiKey, BuildConfig.productId, BuildConfig.productKey, BuildConfig.productSecret,
                new DUILiteSDK.InitListener() {
                    @Override
                    public void success() {
                        isAuth = SPUtils.getInstance().getBoolean(Constants.SP_KEY_AUTHORIZED, false);
                        if (!isAuth) {
                            ToastUtils.showShort("授权成功!");
                            SPUtils.getInstance().put(Constants.SP_KEY_AUTHORIZED, true);
                        }
                        isAuth = true;
                        LogUtils.w("auth: success,授权成功!");

                        hotwords.initEngine();
                    }

                    @Override
                    public void error(final String errorCode, final String errorInfo) {
                        ToastUtils.showShort("授权失败!" + errorCode);
                        LogUtils.w("auth: error,errorCode=" + errorCode + ",errorInfo=" + errorInfo);
                    }
                });

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = getPackageName();
        String channelName = LiteService.class.getName();
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("service is running...")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }
}
