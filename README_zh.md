# YADB

**YADB** 是基于原生 ADB (Android Debug Bridge) 进行功能扩展的工具，提供了一些原生 ADB
不支持的实用功能。这些功能包括中文输入、截屏、布局抓取和长按屏幕操作。

## 功能介绍

### 中文输入

通过 YADB，您可以轻松输入中文，解决了使用 `adb shell input text` 输入中文时出现的乱码问题。

```bash
adb push yadb /data/local/tmp & adb shell app_process -Djava.class.path=/data/local/tmp/yadb /data/local/tmp com.ysbing.yadb.Main -keyboard 你好，世界
```

### 读取粘贴板

```bash
adb push yadb /data/local/tmp & adb shell app_process -Djava.class.path=/data/local/tmp/yadb /data/local/tmp com.ysbing.yadb.Main -readClipboard
```

### 截屏

YADB 允许您在不考虑活动 (Activity) 禁止截屏的情况下进行屏幕截图。

```bash
adb push yadb /data/local/tmp & adb shell app_process -Djava.class.path=/data/local/tmp/yadb /data/local/tmp com.ysbing.yadb.Main -screenshot
```

### 布局抓取

相比 `adb shell uiautomator dump`，YADB 提供了更为高效的布局抓取功能，尤其在某些界面 uiautomator
无法获取时。

```bash
adb push yadb /data/local/tmp & adb shell app_process -Djava.class.path=/data/local/tmp/yadb /data/local/tmp com.ysbing.yadb.Main -layout
```

### 长按屏幕

该功能可用于自动化测试，实现屏幕的长按操作。

```bash
adb push yadb /data/local/tmp & adb shell app_process -Djava.class.path=/data/local/tmp/yadb /data/local/tmp com.ysbing.yadb.Main -touch 500 500 2000
```

## 许可证

本项目采用 [LGPLv3](https://opensource.org/licenses/LGPL-3.0) 许可证发布。
