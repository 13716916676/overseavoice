package com.example.hotwordsample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class ACT extends Activity implements View.OnClickListener {

    private static final String TAG = "ACT";
    EditText textview;
    Button button1, button2, button3;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);

        textview = findViewById(R.id.show);
        button1 = findViewById(R.id.start);
        button2 = findViewById(R.id.stop);
        button3 = findViewById(R.id.btn_switch);

//        language_choice.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        Log.i(TAG, "onClick:  v.getid() ==" + view.getId()+ "----start  == "+ R.id.start + "---stop  == "+ R.id.stop + "---btn_switch  == "+ R.id.btn_switch );
//        switch (v.getId()) {
//            case R.id.start:
//                textview.setText("kaishi ");
//            case R.id.stop:
//                textview.setText("jieshu");
//            case R.id.btn_switch:
//                textview.setText("qiehuan ");
//        }

        if (view == button1) {
            textview.setText("录音已开始" + "\n");
        } else if (view == button2) {
            textview.setText("已取消" + "\n");
        }
    }
}
