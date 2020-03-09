package com.example.hotwordsample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aispeech.AIError;
import com.aispeech.AIResult;
import com.aispeech.DUILiteSDK;
import com.aispeech.common.AIConstant;
import com.aispeech.export.engines.AILocalHotWordsEngine;
import com.aispeech.export.listeners.AIASRListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, AIASRListener, EasyPermissions.PermissionCallbacks {

    private static final String TAG = "MainActivity";

    private static boolean haveAuth = false;
    ListView leftListView;

    ScrollView rightScrollView;

    CheckBox checkBox;
    Button buttonstart, buttonstop;

    Spinner spinner;

    EditText textView;

    private List<ListViewBean> listViewBeanList;
    private ListviewAdapter leftListViewAdapter;

    String[] mPermissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.READ_CONTACTS
            , Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.RECORD_AUDIO
    };

    AILocalHotWordsEngine mEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions();

        leftListView = findViewById(R.id.listview);
        rightScrollView = findViewById(R.id.scrollView);
        checkBox = findViewById(R.id.checkbox);
//
        buttonstart = findViewById(R.id.start);
        buttonstop = findViewById(R.id.stop);

        spinner = findViewById(R.id.spinner);

        textView = findViewById(R.id.show);

        listViewBeanList = new ArrayList<>();
        listViewBeanList.add(new ListViewBean(getResources().getDrawable(R.drawable.pi), "语音控制"));
        listViewBeanList.add(new ListViewBean(getResources().getDrawable(R.drawable.pi), "语音指令"));
        listViewBeanList.add(new ListViewBean(getResources().getDrawable(R.drawable.pi), "关于"));


        leftListViewAdapter = new ListviewAdapter(MainActivity.this, listViewBeanList);
        leftListView.setAdapter(leftListViewAdapter);

        leftListView.setOnItemClickListener(this);

        checkBox.setClickable(true);
        checkBox.setOnClickListener(this);

        buttonstop.setEnabled(false);
        buttonstart.setEnabled(false);
        buttonstart.setOnClickListener(this);
        buttonstop.setOnClickListener(this);

        //imageView.setOnClickListener(this);
//        scrollView.setOnClickListener(this);
//        line1.setOnClickListener(this);
//        line2.setOnClickListener(this);
//        language_choice.setOnClickListener(this);
//        button1.setOnClickListener(this);
//        button2.setOnClickListener(this);

        Resources resources = getResources();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(this, R.array.languege,
                        android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.
                R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Toast.makeText(MainActivity.this, "position == 0", Toast.LENGTH_SHORT).show();
                    SharedPreUtils.getInstance(MainActivity.this).commitIntValue("languechoice",0);
                }
                if (position == 1) {
                    Toast.makeText(MainActivity.this, "position == 1", Toast.LENGTH_SHORT).show();
                    SharedPreUtils.getInstance(MainActivity.this).commitIntValue("languechoice",1);
                }
                if (position == 2) {
                    Toast.makeText(MainActivity.this, "position == 2", Toast.LENGTH_SHORT).show();
                    SharedPreUtils.getInstance(MainActivity.this).commitIntValue("languechoice",2);
                }

                if (position == 3) {
                    Toast.makeText(MainActivity.this, "position == 3", Toast.LENGTH_SHORT).show();
                    SharedPreUtils.getInstance(MainActivity.this).commitIntValue("languechoice",3);
                }

                restartEngine(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (checkBox.isChecked()) {
            auth();
        }

        if (!AILocalHotWordsEngine.checkLibValid()) {
            Toast.makeText(MainActivity.this, "so加载失败", Toast.LENGTH_SHORT).show();
        } else {
            int whichLaunguage = SharedPreUtils.getInstance(MainActivity.this).getIntValue("whichLaunguage");
            mEngine = restartEngine(whichLaunguage);
        }
    }

    private AILocalHotWordsEngine restartEngine(int position) {
        mEngine = AILocalHotWordsEngine.createInstance();
        if (position == 0) {
            mEngine.setResBin("ebnfr.dymc.char.v02.bin");
        }
        if (position == 1) {
            mEngine.setResBin("ebnfr_spanish_82words_v02.bin");
        }
        if (position == 2) {
            mEngine.setResBin("ebnfr_pt_v03.3.bin");
        }
        if (position == 3) {
            mEngine.setResBin("ebnfr_russian_v03.1.bin");
        }

//        if (position ==4){
//            mEngine.setResBin("ebnfr.dymc.char.v02.bin");
//        }
        mEngine.setVadEnable(true);
        mEngine.setVadRes(SampleConstants.VAD_RES);
        mEngine.setSaveAudioPath("/sdcard/speech/hotwordsample");
        mEngine.setMaxSpeechTimeS(5);
        mEngine.setThreshold(0.2);//设置识别结果采信置信度标准，默认0.60
        mEngine.setUseContinuousRecognition(true);//设置连续识别
        //mEngine.setResBin(SampleConstants.HOT_WORDS_EBNFR_RES); //设置识别资源
        mEngine.init(this);
//            mEngine.setUseCustomFeed(true);


        return mEngine;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (checkBox.isChecked()) {
            //Toast.makeText(this, "唤醒开启", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(this, "唤醒关闭", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(998, mPermissions, grantResults, this);
    }

    private void requestPermissions() {
        if (EasyPermissions.hasPermissions(this, mPermissions)) {
            Toast.makeText(getApplicationContext(), "茄子~~~~", Toast.LENGTH_SHORT).show();
        } else {
            EasyPermissions.requestPermissions(this, "请给权限", 998, mPermissions);
        }
    }

    @AfterPermissionGranted(100)
    private void methodRequiresTwoPermission() {
        if (EasyPermissions.hasPermissions(this, mPermissions)) {
            // Already have permission, do the thing
            // ...
        } else {
            Log.i(TAG, "methodRequiresTwoPermission: ");
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "请给权限", 998, mPermissions);
        }
    }

    @SuppressLint("ShowToast")
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Log.i(TAG, "position----- " + position + "-----id -----" + id);
//        String  itemName = null;
//
//        if (bean instanceof ListViewBean){
//            itemName = ((ListViewBean) bean).getBeanName();
//        }

        switch (parent.getId()) {
            case R.id.listview:
                if (position == 0) {
                    Log.i(TAG, "position == 0: ");
                    Toast.makeText(this, "语音控制", Toast.LENGTH_SHORT).show();
                }
                if (position == 1) {
                    Log.i(TAG, "position == 1: ");
                    Toast.makeText(this, "语音指令", Toast.LENGTH_SHORT).show();
                }

                if (position == 2) {
                    Log.i(TAG, "position == 2: ");
                    Toast.makeText(this, "关于", Toast.LENGTH_LONG);
                }
            default:
                break;
        }

    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.checkbox:
//                if (checkBox.isChecked()) {
//                    Toast.makeText(this, "唤醒开启", Toast.LENGTH_SHORT).show();
//                    auth();
//                    break;
//                } else {
//                    Toast.makeText(this, "唤醒关闭", Toast.LENGTH_SHORT).show();
//                    break;
//                }
//            case R.id.start:
//                Log.i(TAG, "录音已开始: ");
//               // Toast.makeText(this, "开始录音", Toast.LENGTH_SHORT).show();
//                textView.setText("录音已开始" + "\n");
//                mEngine.start(words, blackList);
//            case R.id.stop:
//                Log.i(TAG, "已取消Luyin: ");
//               // Toast.makeText(this, "结束录音", Toast.LENGTH_SHORT).show();
//                mEngine.cancel();
//                textView.setText("已取消" + "\n");
//        }

        if (v == buttonstart) {
            Log.i(TAG, "录音已开始: ");
            // Toast.makeText(this, "开始录音", Toast.LENGTH_SHORT).show();
            textView.setText("录音已开始" + "\n");
            mEngine.start(words, blackList);
        }
        if (v == buttonstop) {
            Log.i(TAG, "已取消Luyin: ");
            // Toast.makeText(this, "结束录音", Toast.LENGTH_SHORT).show();
            mEngine.cancel();
            textView.setText("已取消" + "\n");
        }
        if (v == checkBox) {
            if (checkBox.isChecked()) {
                Toast.makeText(this, "唤醒开启", Toast.LENGTH_SHORT).show();
                auth();
            } else {
                Toast.makeText(this, "唤醒关闭", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void auth() {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("授权中...")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        dialog.show();

        DUILiteSDK.setParameter(DUILiteSDK.KEY_AUTH_TIMEOUT, "5000");//设置授权连接超时时长，默认5000ms
//        DUILiteSDK.setParameter(DUILiteSDK.KEY_DEVICE_PROFILE_PATH, "/sdcard/speech");//自定义设置授权文件的保存路径,需要确保该路径事先存在
        boolean isAuthorized = DUILiteSDK.isAuthorized(getApplicationContext());//查询授权状态，DUILiteSDK.init之后随时可以调
        Log.d(TAG, "DUILite SDK is isAuthorized ？ " + isAuthorized);

        String core_version = DUILiteSDK.getCoreVersion();//获取内核版本号
        Log.d(TAG, "core version is: " + core_version);

//        DUILiteSDK.setParameter(DUILiteSDK.KEY_SET_THREAD_AFFINITY, 3);//绑定第三个核，降低CPU占用
        DUILiteSDK.setParameter(DUILiteSDK.KEY_UPLOAD_AUDIO_LEVEL, DUILiteSDK.UPLOAD_AUDIO_LEVEL_NONE);//默认不上传预唤醒和唤醒音频

        // 设置延迟上传时间，需要打开上传唤醒音频功能
        // DUILiteSDK.setParameter(DUILiteSDK.KEY_UPLOAD_AUDIO_LEVEL, DUILiteSDK.UPLOAD_AUDIO_LEVEL_ALL);
        // DUILiteSDK.setParameter(DUILiteSDK.KEY_UPLOAD_AUDIO_DELAY_TIME, 5 * 60 * 1000);  // 功能默认打开，默认5分钟

        //设置SDK录音模式
        DUILiteSDK.setAudioRecorderType(DUILiteSDK.TYPE_COMMON_MIC);//单麦模式
//        DUILiteSDK.setAudioRecorderType(DUILiteSDK.TYPE_COMMON_DUAL);//线性双麦模式
//        DUILiteSDK.setAudioRecorderType(DUILiteSDK.TYPE_COMMON_LINE4);//线性四麦模式
//        DUILiteSDK.setMaxVolumeMode(true);
//        DUILiteSDK.setAudioRecorderType(DUILiteSDK.TYPE_COMMON_CIRCLE4);//环形四麦模式
//        DUILiteSDK.setAudioRecorderType(DUILiteSDK.TYPE_COMMON_CIRCLE6);//环形六麦模式

//        DUILiteSDK.setAudioRecorderType(DUILiteSDK.TYPE_COMMON_ECHO);//echo模式
//        DUILiteSDK.setEchoResName(SampleConstants.ECHO__RES);// 设置echo的资源文件
//        DUILiteSDK.setEchoChannels(2);//音频总的通道数
//        DUILiteSDK.setEchoMicNum(1);//真实mic数
//        DUILiteSDK.setEchoSavedPath("/sdcard/aispeech/aecPcmFile/");//设置保存的aec原始输入和aec之后的音频文件路径
//        DUILiteSDK.setRecChannel(1);// 默认为1,即左通道为rec录音音频,右通道为play参考音频（播放音频）若设置为2,
//         通道会互换，即左通道为play参考音频（播放音频）,右通道为rec录音音频

//        DUILiteSDK.setAudioRecorderType(DUILiteSDK.TYPE_COMMON_FDM);//fdm模式
//        DUILiteSDK.setFdmResName(SampleConstants.FDM_RES);// 设置fdm的资源文件
//        DUILiteSDK.setFdmSavedPath("/sdcard/aispeech/fdmPcmFile/");//设置保存的fdm原始输入和fdm之后的音频文件路径
//        DUILiteSDK.setDefaultDriveMode(AIConstant.DRIVE_MODE_ALL);//设置fdm的默认驾驶模式,可选为全模式(AIConstant.DRIVE_MODE_ALL)
        // 和主驾模式(DRIVE_MODE_MAIN).默认值为AIConstant.DRIVE_MODE_ALL
        //        DUILiteSDK.setParameter(DUILiteSDK.KEY_TTS_CACHE_DIR , "/sdcard/speech/");//设置TTS cache目录

        DUILiteSDK.openLog();//须在init之前调用.同时会保存日志文件在/sdcard/duilite/DUILite_SDK.log

        DUILiteSDK.init(getApplicationContext(), "1048d2ff6b3524370ac6cda55e6339c8"
                , "278589885"
                , "fa05fd78a7573252c53985e90de69732"
                , "a4a6165f77e8ca49ce6e8a4fb3fa408b"
                , new DUILiteSDK.InitListener() {
                    @Override
                    public void success() {
                        Log.i(TAG, "DUILiteSDK.init success: ");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.setMessage("授权成功!");
                                haveAuth = true;
                            }
                        });
                    }

                    @Override
                    public void error(final String errorCode, final String errorInfo) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.setMessage("授权失败\n\nErrorCode：" + errorCode + "\n\nErrorInfo：" + errorInfo);
                                haveAuth = false;
                            }
                        });

                    }
                });

    }

    final Resources resource = MyApplication.getContext().getResources();

    private List<String> words = new ArrayList<String>() {{
        add("打开车窗");
        add(resource.getString(R.string.openCondition));
        add(resource.getString(R.string.openScreen));

    }};


    private List<String> blackList = new ArrayList<String>() {{
        add("第十一个");
    }};

    @Override
    public void onResults(AIResult aiResult) {
        textView.append(aiResult + "\n");
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
    public void onInit(int status) {
        Log.i(TAG, "Init result " + status);
        if (status == AIConstant.OPT_SUCCESS) {
            textView.setText("初始化成功!");
            buttonstart.setEnabled(true);
            buttonstop.setEnabled(true);
        } else {
            textView.setText("初始化失败!code:" + status);
        }
    }

    @Override
    public void onError(AIError aiError) {

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mEngine != null) {
            mEngine.destroy();
            mEngine = null;
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }
}
