Language: [English](https://github.com/ysbing/YADB/blob/master/README.md) | [中文简体](https://github.com/ysbing/YADB/blob/master/README_zh.md)

# YADB

**YADB** is a tool that extends the functionality of the native ADB (Android Debug Bridge), offering practical features not supported by the native ADB. These features include Unicode character input, screenshots, layout extraction, and long-press screen operations.

## Features

### Unicode character Input

With YADB, you can easily input Chinese characters, solving the issue of garbled text when using `adb shell input text` to input Chinese.

```bash
adb push yadb /data/local/tmp && adb shell app_process -Djava.class.path=/data/local/tmp/yadb /data/local/tmp com.ysbing.yadb.Main -keyboard 你好，世界
```

### Read Clipboard

```bash
adb push yadb /data/local/tmp && adb shell app_process -Djava.class.path=/data/local/tmp/yadb /data/local/tmp com.ysbing.yadb.Main -readClipboard
```

### Screenshot

YADB allows you to take screenshots without considering whether the Activity forbids screenshots.

```bash
adb push yadb /data/local/tmp && adb shell app_process -Djava.class.path=/data/local/tmp/yadb /data/local/tmp com.ysbing.yadb.Main -screenshot
```

### Layout Extraction

Compared to `adb shell uiautomator dump`, YADB provides more efficient layout extraction, especially in interfaces where uiautomator cannot fetch the layout.

```bash
adb push yadb /data/local/tmp && adb shell app_process -Djava.class.path=/data/local/tmp/yadb /data/local/tmp com.ysbing.yadb.Main -layout
```

### Long Press Screen

This feature can be used for automated testing to achieve long press operations on the screen.

```bash
adb push yadb /data/local/tmp && adb shell app_process -Djava.class.path=/data/local/tmp/yadb /data/local/tmp com.ysbing.yadb.Main -touch 500 500 2000
```

## License

This project is released under the [LGPLv3](https://opensource.org/licenses/LGPL-3.0) license.