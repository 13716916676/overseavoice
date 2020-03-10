package com.aispeech.hotwords.speech;

import android.os.Bundle;

import com.aispeech.export.engines.AILocalHotWordsEngine;
import com.aispeech.hotwords.BuildConfig;
import com.aispeech.hotwords.Constants;
import com.aispeech.hotwords.utils.Language;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.MessengerUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.aispeech.hotwords.speech.Hotwords.LYRA_PATH;

public class FeedAudioHelper {
    private volatile boolean isFeeding;
    private volatile String currentFilename = "recorder";
    private volatile static String startFeedTime = "";
    private volatile static String endFeedTime = "";

    public void setFeeding(boolean isFeeding) {
        this.isFeeding = isFeeding;
    }

    /**
     * 加载本地音频
     */
    public void loadAudio(final AILocalHotWordsEngine engine, final JSONObject audioConf, final List<String> wordsList) {
        ThreadUtils.executeBySingle(new ThreadUtils.SimpleTask<String>() {
            @Override
            public String doInBackground() {
                setFeeding(true);

                // 送音频
                String audioPath = audioConf.optString("srcPath");
                LogUtils.d("isCustomFeed,start loadAudio from:" + audioPath);

                File audioDir = new File(audioPath);
                File[] files = audioDir.listFiles();
                for (int j = 0; files != null && j < files.length; j++) {
                    if (!isFeeding) {
                        LogUtils.w("loadAudio,isFeeding=" + isFeeding);
                        break;
                    }
                    final File f = files[j];
                    currentFilename = f.getName();
                    LogUtils.d("loadAudio filename:" + currentFilename + ",j=" + j);
                    int feedSize = audioConf.optInt("feedSize", 3200);
                    int feedIntervalTime = audioConf.optInt("feedIntervalTime", 100);
                    feedAudio(engine, f, feedSize, feedIntervalTime, wordsList);
                }

                return null;
            }

            @Override
            public void onSuccess(String ret) {
                LogUtils.d("feed audio onSuccess!");
                if (isFeeding) {

                    Bundle bundle = new Bundle();
                    bundle.putBoolean(Constants.MSG_STOP_ENGINE, true);
                    bundle.putBoolean(Constants.MSG_WAKEUP_STATE, false);
                    MessengerUtils.post(Constants.KEY_MSG, bundle);
                }
            }
        });
    }

    /**
     * feed 音频到识别引擎
     *
     * @param engine
     * @param file             音频文件
     * @param feedSize         每次 feed 的音频大小
     * @param feedIntervalTime 每次 feed 的音频的间隔
     * @param wordsList        热词列表
     */
    private void feedAudio(AILocalHotWordsEngine engine, File file, int feedSize, int feedIntervalTime, List<String> wordsList) {
        LogUtils.d("feed start,filename=" + file.getName());
        startFeedTime = getCurrentTime();
        engine.start(wordsList);
        try {
            byte[] data = FileIOUtils.readFile2BytesByStream(file);

            if (data != null && data.length > 0) {
                if (engine != null) {
                    int i = 0;
                    int j = 0;
                    int dataLength = data.length;
                    int rawLen = 0;
                    while (i < dataLength) {
                        j += feedSize;
                        if (j > dataLength) {
                            j = dataLength;
                        }

                        byte[] buffer = new byte[feedSize];
                        if ((j - i) < feedSize) {
                            buffer = new byte[j - i];
                        }
                        System.arraycopy(data, i, buffer, 0, buffer.length);
                        // 自行feed数据
                        engine.feedData(buffer);
                        i += feedSize;
                        rawLen += buffer.length;

                        Thread.sleep(feedIntervalTime);
                    }

                    engine.stop();

                    endFeedTime = getCurrentTime();
                    LogUtils.d("feed end,filename=" + file.getName() + ",data.size=" + data.length + ",file.length=" + file.length() + ",rawLen=" + rawLen);
                    Thread.sleep(500);
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * 识别结果保存本地 xml，以便导出统计次数
     *
     * @param recText  识别结果
     * @param realConf 引擎返回的置信度
     */
    public void dealRecText(String recText, double realConf) {
        ToastUtils.showShort(recText);

        String language = SPUtils.getInstance().getString(Constants.SP_KEY_LANGUAGE, Language.getDefaultLanguage());
        Set<String> countSet = SPUtils.getInstance(language).getStringSet(recText, new HashSet());
        // 当前识别结果的时间点
        String time = startFeedTime + "|" + endFeedTime + "|" + getCurrentTime();
        LogUtils.d("dealRecText: " + time);
        if (countSet == null) {
            countSet = new HashSet();
        }
        countSet.add(currentFilename + ":" + time + "$" + realConf);

        SPUtils.getInstance(language).put(recText, countSet);
    }

    /**
     * 保存统计结果到 sdcard/lyra/xxx-result.json中
     *
     * @param wordsList xxx.jon 中配置的热词，格式如下：
     *                  {
     *                  "TURN ON":{
     *                  "1.wav":[
     *                  "20191010-131212$0.612"
     *                  "20191010-131212"
     *                  "20191010-131212"
     *                  ],
     *                  "2.wav":[
     *                  "20191010-131212"
     *                  ]
     *                  }
     *                  }
     */
    public void saveResultToJson(final List<String> wordsList) {
        ThreadUtils.executeBySingle(new ThreadUtils.SimpleTask<String>() {

            @Override
            public String doInBackground() {
                // 读取 xml 中 统计的热词次数
                try {

                    JSONObject datas = new JSONObject();
                    String language = SPUtils.getInstance().getString(Constants.SP_KEY_LANGUAGE, Language.getDefaultLanguage());
                    for (int i = 0; i < wordsList.size(); i++) {
                        String recText = wordsList.get(i);
                        Set<String> countSet = SPUtils.getInstance(language).getStringSet(recText, new HashSet());

                        JSONObject countObj = new JSONObject();

                        for (String value : countSet) {
                            String[] strs = value.split(":");
                            String audioName = strs[0];
                            String times = strs[1];
                            JSONArray timeArrays;
                            if (countObj.isNull(audioName)) {
                                timeArrays = new JSONArray();
                            } else {
                                timeArrays = countObj.optJSONArray(audioName);
                            }
                            timeArrays.put(times);
                            countObj.put(audioName, timeArrays);
                        }
                        datas.put(recText, countObj);
                    }

                    // 汇总结果并保存 json 到文件
                    String json = datas.toString(4);
                    String filename = language + "_result_" + (new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date())) + ".json";
                    String destPath = LYRA_PATH + filename;
                    FileIOUtils.writeFileFromString(destPath, json);
                    if (BuildConfig.FLAVOR.equals("demo")) {
                        ToastUtils.showLong("拷贝数据到" + destPath);
                    }

                    return json;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            public void onSuccess(String result) {
                LogUtils.json(result);
            }
        });

    }

    private String getCurrentTime() {
        return new SimpleDateFormat("yyyyMMdd-HHmmss.SSS").format(new Date());
    }
}
