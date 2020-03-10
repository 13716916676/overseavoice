package com.aispeech.hotwords.speech;

import com.aispeech.hotwords.App;
import com.aispeech.hotwords.BuildConfig;
import com.aispeech.hotwords.action.IAction;
import com.blankj.utilcode.util.LogUtils;

/**
 * 语音控制器，需与车机对接实现。主要包含 2 个概念：
 * </p> command: 即指令词，每种语言，定义的都不一样。如：打开 WiFi/OPEN THE WIFI
 * </p> action: 即具体执行的动作，所有的语言，定义都一致。如：action.system.OpenTheWiFi
 *
 * @author aispeech
 */
public class SpeechController {
    private static final String TAG = SpeechController.class.getName();

    private static class Holder {
        static SpeechController INSTANCE = new SpeechController();
    }

    private IAction mAction;

    private SpeechController() {
    }

    public static SpeechController getInstance() {
        return Holder.INSTANCE;
    }


    /**
     * 处理命令，并映射对应的动作实现类执行
     *
     * @param commands 识别的命令词
     * @param action 命令词对应的动作
     */
    public void handle(String commands, String action) {
        LogUtils.d(TAG, "handle() called with: command = [" + commands + "],action = [" + action + "]");

        // 默认动作实现类名路径：packageName + action
        String className = App.getApp().getPackageName() + "." + action;
        // 不同方案商的动作实现不同，其类名路径为：packageName + ".vendor." + BuildConfig.FLAVOR + action
        String vendorClassName = className.replace("action", "action.vendor." + BuildConfig.FLAVOR);

        try {
            mAction = (IAction) Class.forName(vendorClassName).newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            LogUtils.w(TAG, "handle(),vendorClassName NotFound,instance default implement:" + className);

            // 找不到对应的方案商实现类，使用默认的实现，一般采纳 android 原生接口实现
            try {
                mAction = (IAction) Class.forName(className).newInstance();
            } catch (IllegalAccessException | ClassNotFoundException | InstantiationException ex) {
                ex.printStackTrace();
            }
        }

        try {
            if (mAction != null) {
                mAction.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
