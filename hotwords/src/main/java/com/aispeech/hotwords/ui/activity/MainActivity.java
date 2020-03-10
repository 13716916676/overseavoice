package com.aispeech.hotwords.ui.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aispeech.hotwords.App;
import com.aispeech.hotwords.Constants;
import com.aispeech.hotwords.R;
import com.aispeech.hotwords.ui.fragment.AboutFragment;
import com.aispeech.hotwords.ui.fragment.CommandsFragment;
import com.aispeech.hotwords.ui.fragment.SettingsFragment;
import com.aispeech.hotwords.speech.LiteService;
import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.MessengerUtils;
import com.blankj.utilcode.util.PermissionUtils;

import java.util.List;

/**
 * @author aispeech
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_tab_settings, tv_tab_commands, tv_tab_about;
    private Fragment settingsFragment, commandsFragment, aboutFragment;
    private int current_tab_index = R.id.tv_tab_settings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initView();

        checkPermissions();

        MessengerUtils.register();
    }

    private void initView() {
        tv_tab_settings = findViewById(R.id.tv_tab_settings);
        tv_tab_commands = findViewById(R.id.tv_tab_commands);
        tv_tab_about = findViewById(R.id.tv_tab_about);

        tv_tab_settings.setOnClickListener(this);
        tv_tab_commands.setOnClickListener(this);
        tv_tab_about.setOnClickListener(this);

        settingsFragment = new SettingsFragment();
        commandsFragment = new CommandsFragment();
        aboutFragment = new AboutFragment();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_tab_settings:
            case R.id.tv_tab_commands:
            case R.id.tv_tab_about:
                switchTab(v.getId());
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        MessengerUtils.subscribe(Constants.KEY_MSG, new MessengerUtils.MessageCallback() {
            @Override
            public void messageCall(Bundle data) {
                LogUtils.d("MainActivity,messageCall = " + data.toString());
                if (data.containsKey(Constants.MSG_CHANGE_LANGUAGE)) {
                    // 重启当前 activity
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(App.getApp(), MainActivity.class));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    );
                    App.getApp().startActivity(intent);
                }

                if (data.containsKey(Constants.MSG_WAKEUP_STATE)) {
                    final boolean isEnable = data.getBoolean(Constants.MSG_WAKEUP_STATE, true);
                    App.getApp().setCurrentWakeupEnable(isEnable);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (settingsFragment != null) {
                                ((SettingsFragment) settingsFragment).setWakeupSwitch(isEnable);
                            }
                        }
                    });

                }

                if (data.containsKey(Constants.MSG_CURRENT_LANGUAGE)) {
                    final String language = data.getString(Constants.MSG_CURRENT_LANGUAGE);
                    App.getApp().setCurrentLanguage(language);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (settingsFragment != null) {
                                ((SettingsFragment) settingsFragment).updateLanguage(language);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            switchTab(current_tab_index);
        }
    }

    /**
     * 切换左侧选项卡
     *
     * @param index 选项卡下标
     */
    private void switchTab(int index) {
        current_tab_index = index;
        resetTabs();

        switch (index) {
            case R.id.tv_tab_settings:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fl_container, settingsFragment)
                        .commit();
                selectedTab(tv_tab_settings);
                break;
            case R.id.tv_tab_commands:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fl_container, commandsFragment)
                        .commit();
                selectedTab(tv_tab_commands);
                break;
            case R.id.tv_tab_about:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fl_container, aboutFragment)
                        .commit();
                selectedTab(tv_tab_about);
                break;
            default:
                break;
        }
    }

    private void selectedTab(final TextView tabView) {
        tabView.setBackgroundResource(R.drawable.rect_4_corner_tab);
        tabView.postDelayed(new Runnable() {
            @Override
            public void run() {
                tabView.setSelected(true);
            }
        }, 1000);
    }

    private void resetTabs() {
        tv_tab_settings.setSelected(false);
        tv_tab_commands.setSelected(false);
        tv_tab_about.setSelected(false);
        tv_tab_settings.setBackgroundColor(Color.TRANSPARENT);
        tv_tab_commands.setBackgroundColor(Color.TRANSPARENT);
        tv_tab_about.setBackgroundColor(Color.TRANSPARENT);
    }

    /**
     * 检查动态权限
     */
    private void checkPermissions() {
        PermissionUtils.permission(PermissionConstants.PHONE,
                PermissionConstants.MICROPHONE,
                PermissionConstants.CONTACTS,
                PermissionConstants.STORAGE)
                .callback(new PermissionUtils.FullCallback() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onGranted(List<String> permissionsGranted) {
                        LogUtils.d("checkPermissions,onGranted:" + permissionsGranted.toString());

                        Intent intent = new Intent(MainActivity.this, LiteService.class);
                        startService(intent);
                    }

                    @Override
                    public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied) {
                        LogUtils.d("checkPermissions,onDenied:" + permissionsDenied.toString());
                    }
                }).request();
    }

    @Override
    protected void onDestroy() {
        MessengerUtils.unregister();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }
}
