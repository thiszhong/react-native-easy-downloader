# React-native 调用系统下载管理器 --安卓
- 出发点是这样一种情形，app的更新是一项不可缺少的功能。当有新版本时，用户会收到提示，用户确认更新后，在系统通知栏显示apk的下载进度，下载完成后安装此apk。

- 目前我知道的已经有此项功能的包是react-native-fetch-blob，项目中用到此包的还有其他文件类操作，但出现了一些小问题，最终选择了react-native-fs。但是react-native-fs好像并没有调用系统downloadManager、installApk的方法。折腾了一圈，想自己封装一个，祝我成功。