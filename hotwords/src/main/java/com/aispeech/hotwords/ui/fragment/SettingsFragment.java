package com.aispeech.hotwords.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.aispeech.hotwords.App;
import com.aispeech.hotwords.Constants;
import com.aispeech.hotwords.R;
import com.aispeech.hotwords.ui.dialog.SingleChooseDialog;
import com.aispeech.hotwords.utils.AntiShakeUtils;
import com.aispeech.hotwords.utils.Language;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.MessengerUtils;

/**
 * @author aispeech
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {

    private TextView tv_language_choose;
    private SwitchCompat wakeupSwitch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_settings, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        tv_language_choose = view.findViewById(R.id.tv_language_choose);
        wakeupSwitch = view.findViewById(R.id.sw_wakeup);
        ImageView ivArrow = view.findViewById(R.id.iv_arrow);
        ivArrow.animate().rotation(-90f).setDuration(0).start();
        ivArrow.setOnClickListener(this);

        tv_language_choose.setOnClickListener(this);

        wakeupSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 防止初始化界面时，触发后续逻辑消息
                if (!buttonView.isPressed()) {
                    return;
                }

                LogUtils.d("SettingsFragment,onCheckedChanged=" + isChecked);
                Bundle bundle = new Bundle();
                bundle.putBoolean(isChecked ? Constants.MSG_START_ENGINE : Constants.MSG_STOP_ENGINE, isChecked);
                MessengerUtils.post(Constants.KEY_MSG, bundle);

                AntiShakeUtils.set(wakeupSwitch, 500);
            }
        });

        updateLanguage(App.getApp().getCurrentLanguage());

        setWakeupSwitch(App.getApp().isCurrentWakeupEnable());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_language_choose:
            case R.id.iv_arrow:
                showChooseDialog();
                break;
            default:
                break;
        }
    }

    private void showChooseDialog() {
        new SingleChooseDialog(getActivity())
                .initView()
                .show();
    }

    public void updateLanguage(String language) {
        String diaplayLanguage = Language.getDisplayNameByShort(language);
        if (tv_language_choose != null) {
            tv_language_choose.setText(diaplayLanguage);
        }

    }

    /**
     * 同步界面开关
     *
     * @param toggle 开关
     */
    public void setWakeupSwitch(boolean toggle) {
        if (wakeupSwitch != null) {
            wakeupSwitch.setChecked(toggle);
        }
    }

}
