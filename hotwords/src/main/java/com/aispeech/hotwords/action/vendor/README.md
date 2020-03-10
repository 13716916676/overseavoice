## 说明
1. 此目录存放各方案商部分 action 不同实现
2. 在 SpeechController.handle() 中，会根据当前打包 apk 的 FLAVOR 字段，来拼接对应的实现类名，并通过反射实例化