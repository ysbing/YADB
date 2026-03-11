Language: [English](https://github.com/ysbing/YADB/blob/master/README.md) | [中文简体](https://github.com/ysbing/YADB/blob/master/README_zh.md)

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

### Long Press and Drag

Useful for scenarios where you need to long-press an element to select it, and then drag it to a new position.

```bash
# Args: startX startY endX endY pressDuration(ms) dragDuration(ms)
adb push yadb /data/local/tmp 
adb shell app_process -Djava.class.path=/data/local/tmp/yadb /data/local/tmp com.ysbing.yadb.Main -longPressDrag 500 500 500 1000 2000 1000

```

### Inertia-free Swipe

An alternative to native `input swipe`. It uses decelerate interpolation to eliminate list scrolling inertia, stopping exactly where your finger lifts.

```bash
# Args: startX startY endX endY duration(ms)
adb push yadb /data/local/tmp 
adb shell app_process -Djava.class.path=/data/local/tmp/yadb /data/local/tmp com.ysbing.yadb.Main -swipe 100 1000 100 500 1000
```

### Two-finger Pinch

Simulate two-finger pinch (zoom out) or spread (zoom in) operations.

```bash
# Args: centerX centerY startDistance(px) endDistance(px) duration(ms)
# Zoom Out (Pinch In): Distance from 800px to 200px
adb push yadb /data/local/tmp && adb shell app_process -Djava.class.path=/data/local/tmp/yadb /data/local/tmp com.ysbing.yadb.Main -pinch 540 960 800 200 500

# Zoom In (Pinch Out): Distance from 200px to 800px
adb push yadb /data/local/tmp && adb shell app_process -Djava.class.path=/data/local/tmp/yadb /data/local/tmp com.ysbing.yadb.Main -pinch 540 960 200 800 500
```

## License

This project is licensed under the [LGPLv3 License](https://opensource.org/licenses/LGPL-3.0).

---
