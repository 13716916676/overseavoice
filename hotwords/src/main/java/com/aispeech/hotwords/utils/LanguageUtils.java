package com.aispeech.hotwords.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;

import androidx.annotation.RequiresApi;

import java.util.Locale;

/**
 * @author aispeech
 */
public class LanguageUtils {

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Context attachBaseContext(Context context, Locale locale) {
        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        LocaleList localeList = new LocaleList(locale);
        LocaleList.setDefault(localeList);
        configuration.setLocales(localeList);
        return context.createConfigurationContext(configuration);
    }

    public static void applyLanguage(Context context, Locale locale) {
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        conf.setLayoutDirection(locale);
        res.updateConfiguration(conf, dm);
    }
}
