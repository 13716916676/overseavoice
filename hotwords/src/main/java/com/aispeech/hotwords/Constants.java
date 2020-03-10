package com.aispeech.hotwords;

public class Constants {

    /**
     * 消息相关
     */
    public static final String KEY_MSG = "key_msg_lite";
    // UI 停止热词引擎
    public static final String MSG_STOP_ENGINE = "/msg/engine/stop";
    //  UI 启动热词引擎
    public static final String MSG_START_ENGINE = "/msg/engine/start";
    // UI 切换语言
    public static final String MSG_CHANGE_LANGUAGE = "/msg/language/change";

    // service 通知 UI 当前语言
    public static final String MSG_CURRENT_LANGUAGE = "msg.language.current";
    // service 通知 UI 当前引擎状态
    public static final String MSG_WAKEUP_STATE = "msg.wakeup.state";


    /**
     * SharePref 相关
     */
    public static final String SP_KEY_LANGUAGE = "language";
    public static final String SP_KEY_WAKEUP = "wakeup";
    public static final String SP_KEY_AUTHORIZED = "isAuthorized";
}
