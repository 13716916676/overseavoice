package com.aispeech.hotwords.ui.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aispeech.hotwords.App;
import com.aispeech.hotwords.utils.LanguageUtils;
import com.blankj.utilcode.util.LogUtils;

import java.util.Locale;

import me.jessyan.autosize.AutoSize;

/**
 * @author aispeech
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            String language = App.getApp().getCurrentLanguage();
            LanguageUtils.applyLanguage(this, new Locale(language));
        }
        AutoSize.autoConvertDensityOfGlobal(this);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        //获取我们存储的语言环境 比如 "en","zh",等等
        String language = App.getApp().getCurrentLanguage();
        LogUtils.d("onActivityCreated,change language=" + language);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            newBase = LanguageUtils.attachBaseContext(newBase, new Locale(language));
        }

        super.attachBaseContext(newBase);

    }

}