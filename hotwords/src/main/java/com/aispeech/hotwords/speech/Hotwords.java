package com.aispeech.hotwords.speech;

import android.os.Environment;
import android.text.TextUtils;

import com.aispeech.AIError;
import com.aispeech.AIResult;
import com.aispeech.export.engines.AILocalHotWordsEngine;
import com.aispeech.export.listeners.AIASRListener;
import com.aispeech.hotwords.App;
import com.aispeech.hotwords.utils.Language;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 热词业务类
 *
 * @author aispeech
 */
public class Hotwords {
    //    public static final String LYRA_PATH = "/sdcard/lyra/";
    public static final String LYRA_PATH = Environment.getExternalStorageDirectory() + "/lyra/";

    // 热词引擎
    private AILocalHotWordsEngine mEngine;
    // 从 json 中读取的总配置项
    private JSONObject confObj;
    // 注册到引擎的命令词，外语的必须必须大写，英文部分缩写需要字母空格
    private List<String> wordsList = new ArrayList<>();
    private boolean customFeed;
    private String language;

    // 热词是否打开
    private boolean hotwordsEnable;

    private FeedAudioHelper feedAudioHelper;

    public Hotwords() {
        feedAudioHelper = new FeedAudioHelper();
        language = Language.getDefaultLanguage();

    }

    public void setHotwordsEnable(boolean hotwordsEnable) {
        this.hotwordsEnable = hotwordsEnable;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * 释放资源
     */
    public void release() {
        LogUtils.d("release: ");
        if (mEngine != null) {
            mEngine.stop();
            mEngine.destroy();
        }
        confObj = null;
    }

    /**
     * 暂停热词引擎
     */
    public void stop() {
        LogUtils.d("stop: ");
        if (mEngine != null) {
            mEngine.cancel();
        }

        feedAudioHelper.saveResultToJson(wordsList);
        // 如果是自动 feed 音频模式，此 stop 有可能会触发 2 次：
        // 1. 手动关闭唤醒按钮，会发送 STOP_ENGINE；
        // 2. feed 音频的线程，检测到停止了，会触发 STOP_ENGINE.
        // 故下面代码setFeeding(false)必须放在saveResultToJson，防止触发 2 次STOP_ENGINE
        feedAudioHelper.setFeeding(false);
    }

    /**
     * 开始热词引擎
     */
    public void start() {
        LogUtils.d("start: ");
        if (mEngine != null) {
            // 清除之前的统计数据和音频备份
            SPUtils.getInstance(language).clear();
            // 初始化 xxx.xml
            for (int i = 0; i < wordsList.size(); i++) {
                Set<String> valueSets = new HashSet<>();
                SPUtils.getInstance(language).put(wordsList.get(i), valueSets);
            }


            JSONObject audioConf = confObj.optJSONObject("audio");
            String backupPath = audioConf.optString("backupPath");
            FileUtils.deleteFilesInDir(backupPath);

            if (customFeed) {
                feedAudioHelper.loadAudio(mEngine, audioConf, wordsList);
            } else {
                mEngine.start(wordsList);
            }
        }
    }

    /**
     * 加载配置文件
     */
    private JSONObject loadConf() {
        wordsList.clear();

        try {
            File confFile = new File(LYRA_PATH + "/conf/" + language + ".json");
            String json;
            //1. 优先从 sdcard 读取配置
            if (confFile.exists()) {
                json = FileIOUtils.readFile2String(confFile);
            } else {
                //2. 从 assets 目录读取配置
                String confPath = "conf/" + language + ".json";
                json = ResourceUtils.readAssets2String(confPath);
            }

            LogUtils.json(json);
            return new JSONObject(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据不同的语言，初始化不同配置的热词引擎
     */
    public void initEngine() {
        ThreadUtils.executeBySingle(new ThreadUtils.SimpleTask<String>() {
            @Override
            public String doInBackground() {
                confObj = loadConf();
                if (confObj == null) {
                    throw new IllegalArgumentException("加载配置文件失败！");
                }

                JSONObject vadConf = confObj.optJSONObject("vad");
                JSONObject audioConf = confObj.optJSONObject("audio");

                mEngine = AILocalHotWordsEngine.createInstance();
                mEngine.setVadEnable(vadConf.optBoolean("enable"));

                final String vadResName = vadConf.optString("res");
                File vadResFile = new File(LYRA_PATH + "/res/" + vadResName);
                if (vadResFile.exists()) {
                    mEngine.setVadResPath(vadResFile.getAbsolutePath());
                } else {
                    mEngine.setVadRes(vadResName);
                }

                String backupPath = audioConf.optString("backupPath");
                if (!TextUtils.isEmpty(backupPath)) {
                    mEngine.setSaveAudioPath(backupPath);
                }
                customFeed = audioConf.optBoolean("customFeed");
                mEngine.setUseCustomFeed(customFeed);

                //设置连续识别
                mEngine.setUseContinuousRecognition(true);
                //设置识别结果采信置信度标准，默认0.60
                mEngine.setThreshold(confObj.optDouble("thresh"));
                //资源文件
                String resName = confObj.optString("res");
                File resFile = new File(LYRA_PATH + "/res/" + resName);
                if (resFile.exists()) {
                    mEngine.setResBinPath(resFile.getAbsolutePath());
                } else {
                    mEngine.setResBin(resName);
                }

                // ui 上显示的词，和注册到引擎的命令词有区别：1.大小写，英文缩写需分开等
                StringBuilder systemControlWords = new StringBuilder();
                StringBuilder mediaControlWords = new StringBuilder();
                StringBuilder naviControlWords = new StringBuilder();
                StringBuilder carControlWords = new StringBuilder();
                JSONArray arrays = confObj.optJSONArray("words");
                for (int j = 0; j < arrays.length(); j++) {
                    JSONObject wordsObj = arrays.optJSONObject(j);
                    String command = wordsObj.optString("command");
                    String commandTips = wordsObj.optString("commandTips");
                    String action = wordsObj.optString("action");

                    //中文、日语等体系下，命令词和ui 显示的一致，配置文件中commandTips可能为空
                    if (TextUtils.isEmpty(commandTips)) {
                        commandTips = command;
                    }
                    // 葡语、西语、俄语等，command是commandTips的大写形式，command可能为空
                    if (TextUtils.isEmpty(command)) {
                        command = commandTips.toUpperCase();
                    }

                    if (action.contains("system")) {
                        systemControlWords.append(commandTips + ",");
                    } else if (action.contains("media")) {
                        mediaControlWords.append(commandTips + ",");
                    } else if (action.contains("navi")) {
                        naviControlWords.append(commandTips + ",");
                    } else if (action.contains("car")) {
                        carControlWords.append(commandTips + ",");
                    }
                    wordsList.add(command);
                }

                commandTipsWriteToFile(systemControlWords, "systemControlWords");
                commandTipsWriteToFile(mediaControlWords, "mediaControlWords");
                commandTipsWriteToFile(naviControlWords, "naviControlWords");
                commandTipsWriteToFile(carControlWords, "carControlWords");

                mEngine.init(aiasrListener);
                return null;
            }

            @Override
            public void onSuccess(String ret) {

            }
        });
    }

    /**
     * 切换语言时，重新初始化引擎
     *
     * @param languageShortName 语言
     */
    public void changeEngine(String languageShortName) {
        LogUtils.w("changeEngine language = " + languageShortName);
        this.language = languageShortName;
        release();
        initEngine();
    }


    private AIASRListener aiasrListener = new AIASRListener() {

        @Override
        public void onInit(int i) {
            LogUtils.d("onInit=" + i + ",customFeed=" + customFeed + ",hotwordsEnable=" + hotwordsEnable);
            if (i == -1) {
                ToastUtils.showShort("初始化失败，请检查资源路径是否正确！");
                return;
            }
            if (hotwordsEnable) {
                start();
            }
        }

        @Override
        public void onResults(AIResult result) {
            LogUtils.d("onResults=" + result);
            try {
                JSONObject jsonObject = new JSONObject(result.toString());
                String recText = jsonObject.optString("rec");

                if (TextUtils.isEmpty(recText)) {
                    LogUtils.w("recText=" + recText);
                    return;
                }

                JSONArray arrays = confObj.optJSONArray("words");
                // 配置文件的指定阈值
                double targetThresh = 0;
                // 对应的 action
                String action = null;
                for (int j = 0; j < arrays.length(); j++) {
                    JSONObject wordsObj = arrays.optJSONObject(j);
                    String command = wordsObj.optString("command");
                    String commandTips = wordsObj.optString("commandTips");
                    if (recText.equals(command) || recText.equals(commandTips.toUpperCase())) {
                        targetThresh = wordsObj.optDouble("thresh");
                        action = wordsObj.optString("action");
                        break;
                    }
                }

                // 引擎返回的真实的置信度
                double realConf = jsonObject.optDouble("conf");
                if (realConf < targetThresh) {
                    LogUtils.w("recText=" + recText + ",realConf:" + realConf + " < targetThresh:" + targetThresh + ",drop it!");
                    return;
                }
                if (!TextUtils.isEmpty(action)) {
                    SpeechController.getInstance().handle(recText, action);
                } else {
                    LogUtils.e("recText=" + recText + ",action=null!!!");
                }

                feedAudioHelper.dealRecText(recText, realConf);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onNotOneShot() {

        }

        @Override
        public void onError(AIError aiError) {
            LogUtils.e("onError: " + aiError.getError() + ",recordId=" + aiError.getRecordId());
        }

        @Override
        public void onReadyForSpeech() {

        }

        @Override
        public void onResultDataReceived(byte[] bytes, int i, int i1) {

        }

        @Override
        public void onRawDataReceived(byte[] bytes, int i) {

        }
    };

    private void commandTipsWriteToFile(StringBuilder tips, String filename) {
        String s0 = tips.substring(0, tips.length() - 1);
        tips.delete(0, tips.length());
        String[] s0Tmp = s0.split(",");
        for (int i = 0; i < s0Tmp.length; i++) {
            if (i != s0Tmp.length - 1) {
                s0Tmp[i] += ", ";
                if ((i + 1) % 2 == 0) {
                    s0Tmp[i] += "\n";
                }
            }

            tips.append(s0Tmp[i]);
        }

        FileIOUtils.writeFileFromString(new File(App.getApp().getFilesDir() + "/" + filename), tips.toString());
    }
}
