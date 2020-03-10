package com.aispeech.hotwords.utils;


import androidx.collection.ArrayMap;

import java.util.Map;

public class Language {
    public static ArrayMap<String, String> languageMaps = new ArrayMap<>();

    static {
        languageMaps.put("中文", "zh");
        languageMaps.put("English(United States)", "en");
        languageMaps.put("русский", "ru");
        languageMaps.put("Português", "pt");//葡语
        languageMaps.put("Espanol", "es");//葡萄牙语
        languageMaps.put("Deutsch", "de");//德语
        languageMaps.put("日本語", "ja");
        languageMaps.put("Tiếng Việt", "vi");//越南语
        languageMaps.put("ภาษาไทย", "th");//泰语
    }


    /**
     * 通过简称获取显示的显示语言
     *
     * @param shortName 语言简称
     */
    public static String getDisplayNameByShort(String shortName) {
        if (languageMaps.containsValue(shortName)) {
            for (Map.Entry<String, String> entry : languageMaps.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (value.equals(shortName)) {
                    return key;
                }
            }
        }
        return getDisplayNameByShort(getDefaultLanguage());
    }

    /**
     * 通过显示的文本获取语言简称
     *
     * @param displayName 显示的语言名称，如：中文、English 等
     */
    public static String getShorNameByDisplayName(String displayName) {
        if (languageMaps.containsKey(displayName)) {
            return languageMaps.get(displayName);
        } else {
            return getDefaultLanguage();
        }
    }

    public static String getDefaultLanguage() {
        return "en";
    }
}
