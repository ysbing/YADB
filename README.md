# YADB - ADB Function Enhancement Tool

**YADB** is a utility tool that extends the capabilities of native ADB (Android Debug Bridge). It addresses limitations in areas like text input, screenshot capturing, and UI layout extraction, providing more efficient and precise operations.

---

## Features

### Chinese Text Input

Fixes the issue where `adb shell input text` fails to input Chinese characters. Supports input of any text string.

```bash
adb push yadb /data/local/tmp
adb shell app_process -Djava.class.path=/data/local/tmp/yadb /data/local/tmp com.ysbing.yadb.Main -keyboard Hello, World
```

---

### Read Clipboard

Reads the current clipboard content from the device.

```bash
adb push yadb /data/local/tmp
adb shell app_process -Djava.class.path=/data/local/tmp/yadb /data/local/tmp com.ysbing.yadb.Main -readClipboard
```

---

### Write Clipboard

Writes the specified text to the device clipboard.

```bash
adb push yadb /data/local/tmp
adb shell app_process -Djava.class.path=/data/local/tmp/yadb /data/local/tmp com.ysbing.yadb.Main -writeClipboard TextContent
```

---

### Forced Screenshot

Captures the current screen content, bypassing any application-level screenshot restrictions.

```bash
adb push yadb /data/local/tmp
adb shell app_process -Djava.class.path=/data/local/tmp/yadb /data/local/tmp com.ysbing.yadb.Main -screenshot
```

---

### Efficient Layout Dump

An alternative to `uiautomator dump`, enabling layout extraction even on pages where uiautomator fails to retrieve UI elements.

```bash
adb push yadb /data/local/tmp
adb shell app_process -Djava.class.path=/data/local/tmp/yadb /data/local/tmp com.ysbing.yadb.Main -layout
```

---

### Long Press Simulation

Simulates a long-press touch event on the screen, useful for automated testing scenarios.

```bash
adb push yadb /data/local/tmp
adb shell app_process -Djava.class.path=/data/local/tmp/yadb /data/local/tmp com.ysbing.yadb.Main -touch 500 500 2000
```

---

## License

This project is licensed under the [LGPLv3 License](https://opensource.org/licenses/LGPL-3.0).

---
