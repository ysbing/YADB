# YADB - ADB 功能增强工具

**YADB** 是一个基于原生 ADB (Android Debug Bridge) 进行扩展的实用工具，弥补了 ADB 在输入法、截屏、界面抓取等方面的不足，提供更加高效、精准的操作能力。

---

## 功能简介

### 中文输入

解决 `adb shell input text` 无法输入中文的问题，支持任意文本输入。

```bash
adb push yadb /data/local/tmp
adb shell app_process -Djava.class.path=/data/local/tmp/yadb /data/local/tmp com.ysbing.yadb.Main -keyboard 你好，世界
```

---

### 读取剪贴板

直接读取设备当前剪贴板内容。

```bash
adb push yadb /data/local/tmp
adb shell app_process -Djava.class.path=/data/local/tmp/yadb /data/local/tmp com.ysbing.yadb.Main -readClipboard
```

---

### 写入剪贴板

将指定文本写入设备剪贴板。

```bash
adb push yadb /data/local/tmp
adb shell app_process -Djava.class.path=/data/local/tmp/yadb /data/local/tmp com.ysbing.yadb.Main -writeClipboard 文本
```

---

### 强制截屏

无需考虑应用禁截屏限制，直接获取当前屏幕截图。

```bash
adb push yadb /data/local/tmp
adb shell app_process -Djava.class.path=/data/local/tmp/yadb /data/local/tmp com.ysbing.yadb.Main -screenshot
```

---

### 高效布局抓取

替代 `uiautomator dump`，可获取某些特殊页面的完整界面布局。

```bash
adb push yadb /data/local/tmp
adb shell app_process -Djava.class.path=/data/local/tmp/yadb /data/local/tmp com.ysbing.yadb.Main -layout
```

---

### 屏幕长按操作

实现自动化测试中需要的长按功能。

```bash
adb push yadb /data/local/tmp
adb shell app_process -Djava.class.path=/data/local/tmp/yadb /data/local/tmp com.ysbing.yadb.Main -touch 500 500 2000
```

---

## 许可证

本项目遵循 [LGPLv3 许可证](https://opensource.org/licenses/LGPL-3.0)。

---