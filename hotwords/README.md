# 热词外语版
## 背景
本工程在 duilite-sdk-android 的[本地热词引擎](http://car.aispeech.com/duilite/docs/duilite/yu-yin-huan-xing/suo-jian-ji-ke-shuo.html)上开发，旨在对外提供给客户的演示版本。当前已支持语种：
- 中文
- 英语
- 葡萄牙语
- 西班牙语
- 俄语

其他语种：日语、泰语、越南语、阿拉伯语等正在紧急测试中~

## 代码结构
![](https://tva1.sinaimg.cn/large/00831rSTgy1gcnrm35hs1j31bq0u04gk.jpg)

代码结构主要分为 4 部分：

- 配置文件
- 动作执行
- 热词识别
- UI展示

### 1. 配置文件
以上代码都是结合配置文件来使用，具体配置文件位于 **@dest/conf**。英文 en.json 配置示例：
```json
{
  "vad": {
    "res": "vad_aicar_v0.15.bin",
    "enable": true
  },
  "audio": {
    "srcPath": "/sdcard/lyra/audio_en",
    "customFeed": false,
    "feedSize": 6400,
    "feedIntervalTime": 100
  },
  "res": "ebnfr.en_wakeup.bin",
  "thresh": 0.54,
  "words": [
    {
      "command": "TURN ON NAVIGATION",
      "commandTips": "turn on navigation",
      "action": "action.navi.TurnOnNavigation"
    },
    {
      "command": "TURN OFF NAVIGATION",
      "commandTips": "turn off navigation",
      "action": "action.navi.TurnOffNavigation"
    }
  ]
}

```

#### vad

- **res**: vad 资源

- **enable**: 是否开启 vad，正常录音模式下，必须开启检测人声

#### audio
- **srcPath**：测试时 feed 音频模式，音频目录

- **customFeed**：是否开启 feed 音频到热词引擎

- **feedIntervalTime**：每次 feed 的音频间隔

#### res
当前语种的识别资源，必须和语种对应上。当前 app 会优先寻找 /sdcard/lyra/res下的资源，如果没有找到，则会去 assets目录寻找

#### thresh
当前语种设置的阈值，当热词引擎 ASR.CALLBACK 中的置信度 conf > thresh 时，判定为有效的识别。

- 此阈值为当前演示版测试参考，具体项目可能需要结合实际效果可做微调。

- 需要说明的是：阈值过高，识别率降低；阈值过低，误识别率会增加。故该字段是**热词识别效果的非常重要的关键指标**

#### words
- **command**：注册到热词引擎的指令词，请特别注意事项：
    - **英语、葡语、俄语、西班牙语等指令词，必须大写，日语部分指令需要空格分词**
    - **英语、葡语、俄语、西班牙语等指令词，必须大写，日语部分指令需要空格分词**
    - **英语、葡语、俄语、西班牙语等指令词，必须大写，日语部分指令需要空格分词**
- **commandTips**： 展示在 UI 界面的指令词，根据具体 ui 设计需求，有些要求小写，有些要求首字母大写，故特意增加此字段
- **action**：具体执行动作的映射，可通过此字符串反射到对应的动作实现执行类

### 2. 动作执行

代码位于 com.aispeech.hotwords.action 报下，通过反射实例化具体执行类，根据指令类型，分为 4 类：
- 系统控制
- 多媒体控制
- 导航控制
- 车身控制 

实现示例：

![](https://tva1.sinaimg.cn/large/00831rSTgy1gcns4hiu1jj31cb0u0agp.jpg)

```java
public class TurnOnNavigation implements IAction {
    @Override
    public void execute() {
        LogUtils.d("execute TurnOnNavigation");

        // 默认打开高德地图
        AppUtils.launchApp(PKG_NAVI_AMAP);
    }
}
```
具体的指令请参考对应的需求表，客户可参考此方式：
- **必须在对应的热词指令 action 对应的类，实现具体的执行动作**
- **必须在对应的热词指令 action 对应的类，实现具体的执行动作**
- **必须在对应的热词指令 action 对应的类，实现具体的执行动作**

### 3. 热词识别
代码位于 com.aispeech.hotwords.speech 包下，为了保持在后台热词识别，以 service 服务单独一个进程运行。
- **LiteService**，主要包含初始化授权，以及 与 UI 消息交互

- **Hotwords**，热词引擎封装，通过解析配置文件形式，注册指令到热词引擎，引擎回调识别结果。不同语种需要加载不同的识别资源，位于 **@dest/res** 目录：
  
  - ebnfr.dymc.char.v02.bin，中文
  - ebnfr.en_wakeup.bin，英文
  - ebnfr_pt_v03.3.bin.bin，葡萄牙语
  - ebnfr_russian_v03.1.binn，俄语
  - ebnfr_spanish_82words_v02.bin，西班牙语
  
  ![](https://tva1.sinaimg.cn/large/00831rSTgy1gcns62ts5sj31cu0pujwg.jpg)
  
- **SpeechController**，在配置文件中，不同语言的热词指令定义统一的 action,通过反射映射到具体的执行类，如：
	
	> action.navi.TurnOnNavigation


- *FeedAudioHelper*，测试送音频的工具类，默认线上版本应该录音模式，一般用不到此工具。



### 4. UI 展示

![](https://tva1.sinaimg.cn/large/00831rSTgy1gcnrwvagyvj30si0rgac2.jpg)

代码位于 com.aispeech.hotwords.ui 包下，主要功能： 

- 语音设置
  - 唤醒开关设置
  - 语言切换
- 语音指令：展示当前注册的热词指令
- 关于：版本介绍