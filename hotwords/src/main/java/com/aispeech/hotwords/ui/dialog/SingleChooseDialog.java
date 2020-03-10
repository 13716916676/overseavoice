package com.aispeech.hotwords.ui.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aispeech.hotwords.App;
import com.aispeech.hotwords.BuildConfig;
import com.aispeech.hotwords.Constants;
import com.aispeech.hotwords.R;
import com.aispeech.hotwords.ui.dialog.adapter.DialogItemAdapter;
import com.aispeech.hotwords.utils.Language;
import com.aispeech.hotwords.utils.ScreenUtils;
import com.blankj.utilcode.util.MessengerUtils;

import java.util.Arrays;


/**
 * @author aispeech
 */
public class SingleChooseDialog extends Dialog implements View.OnClickListener {
    private TextView tvTitle;
    private TextView btLeft, btRight;
    private RecyclerView rvContent;
    private DialogClickListener leftInterface, rightInterface;
    private DialogItemAdapter itemAdapter;

    public SingleChooseDialog(Context context) {
        this(context, R.style.dialog);
    }

    public SingleChooseDialog(Context context, int theme) {
        super(context, theme);
    }

    public SingleChooseDialog initView() {
        setContentView(R.layout.dialog_single_choose);

        tvTitle = findViewById(R.id.choosedialog_title);
        rvContent = findViewById(R.id.dialog_content);
        btLeft = findViewById(R.id.choosedialog_left);
        btRight = findViewById(R.id.choosedialog_right);

        btLeft.setOnClickListener(this);
        btRight.setOnClickListener(this);
        rvContent.setLayoutManager(new LinearLayoutManager(getContext()));
        rvContent.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        itemAdapter = new DialogItemAdapter();
        rvContent.setAdapter(itemAdapter);
        setItems(BuildConfig.languages);

        setCanceledOnTouchOutside(false);

        return this;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.choosedialog_left:
                saveSelectedLanguage();
                if (leftInterface != null) {
                    leftInterface.onclick();
                }
                this.dismiss();
                break;
            case R.id.choosedialog_right:
                if (rightInterface != null) {
                    rightInterface.onclick();
                }
                this.dismiss();
                break;
            default:
                break;
        }
    }


    public SingleChooseDialog setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
        }
        return this;
    }

    public SingleChooseDialog setItems(String... items) {
        itemAdapter.updateData(Arrays.asList(items));
        return this;
    }

    public SingleChooseDialog setItems(ItemSelectedListener selectedListener, String... items) {
        itemAdapter.setItemSelectedListener(selectedListener);
        setItems(items);
        return this;
    }

    public SingleChooseDialog setLeftButton(DialogClickListener l) {
        leftInterface = l;
        btLeft.setVisibility(View.VISIBLE);
        return this;
    }

    public SingleChooseDialog setLeftButton(DialogClickListener l, String content) {
        btLeft.setText(content);
        setLeftButton(l);
        return this;
    }

    public SingleChooseDialog setLeftButton(DialogClickListener l, String content, int textColor) {
        btLeft.setTextColor(textColor);
        setLeftButton(l, content);
        return this;
    }


    public SingleChooseDialog setRightButton(DialogClickListener l) {
        rightInterface = l;
        btRight.setVisibility(View.VISIBLE);
        return this;
    }

    public SingleChooseDialog setRightButton(DialogClickListener l, String content) {
        btRight.setText(content);
        setRightButton(l);
        return this;
    }

    public SingleChooseDialog setRightButton(DialogClickListener l, String content, int textColor) {
        btRight.setTextColor(textColor);
        setRightButton(l, content);
        return this;
    }

    @Override
    public void show() {
        initView();

        // 检查上一次的选择记录，并重置 currentselected
        String language = App.getApp().getCurrentLanguage();
        if (!TextUtils.isEmpty(language)) {
            String displayName = Language.getDisplayNameByShort(language);
            itemAdapter.setCurrentselected(displayName);
        }

        int padding = ScreenUtils.getScreenWidth(getContext()) / 4;
        getWindow().getDecorView().setPadding(padding, 0, padding, 0);
        WindowManager.LayoutParams lp1 = getWindow().getAttributes();
        lp1.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp1.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp1.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp1);

        super.show();
    }

    @Override
    public void onDetachedFromWindow() {
        leftInterface = null;
        rightInterface = null;
        super.onDetachedFromWindow();
    }

    /**
     * 持久化选中语言，保存其简称
     */
    private void saveSelectedLanguage() {
        String lastLanguage = App.getApp().getCurrentLanguage();
        String selectedLanguage = itemAdapter.getCurrentSelected();
        String shortName = Language.getShorNameByDisplayName(selectedLanguage);
        if (!TextUtils.equals(lastLanguage, shortName)) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.MSG_CHANGE_LANGUAGE, shortName);
            MessengerUtils.post(Constants.KEY_MSG, bundle);

        }
    }

}
