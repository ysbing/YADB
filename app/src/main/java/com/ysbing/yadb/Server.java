package com.ysbing.yadb;

import android.os.SystemClock;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.ysbing.yadb.wrappers.layout.LayoutShell;

public class Server {

    private static final String FLAG_SPACE = "~SPACE~";
    private static final String FLAG_ENTER = "~ENTER~";
    private static final String FLAG_CLEAR = "~CLEAR~";
    private static final String LAYOUT_DEFAULT_PATH = "/sdcard/yadb_layout_dump.xml";

    private static final Device device = new Device();

    public static void keyboard(String text) {
        boolean clearFlags = text.contains(FLAG_CLEAR);
        if (clearFlags) {
            device.injectKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_CTRL_LEFT, 0, KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON);
            device.injectKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_A, 0, KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON);
            device.injectKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_A, 0, KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON);
            device.injectKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_CTRL_LEFT, 0, KeyEvent.META_NUM_LOCK_ON);
            device.injectKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL, 0, KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON);
            device.injectKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL, 0, KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON);
        } else {
            text = text.replace(FLAG_ENTER, "\n").replace(FLAG_SPACE, " ");
            boolean ok = device.setClipboardText(text);
            System.out.println("Copy text:" + ok);
            device.injectKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_CTRL_LEFT, 0, KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON);
            device.injectKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_V, 0, KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON);
            device.injectKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_V, 0, KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON);
            device.injectKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_CTRL_LEFT, 0, KeyEvent.META_NUM_LOCK_ON);
        }
    }

    public static void touch(float x, float y, long pressedTime) {
        long downTime = SystemClock.uptimeMillis();
        MotionEvent.PointerProperties[] properties = new MotionEvent.PointerProperties[1];
        properties[0] = new MotionEvent.PointerProperties();
        properties[0].id = 0;
        properties[0].toolType = MotionEvent.TOOL_TYPE_FINGER;
        MotionEvent.PointerCoords[] coords = new MotionEvent.PointerCoords[1];
        coords[0] = new MotionEvent.PointerCoords();
        coords[0].x = x;
        coords[0].y = y;
        coords[0].pressure = 1;
        MotionEvent clickEvent = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(),
                MotionEvent.ACTION_DOWN, 1, properties, coords, 0,
                0, 1f, 1f, -1, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);
        device.injectEvent(clickEvent);
        if (pressedTime >= 0L) {
            try {
                Thread.sleep(pressedTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        coords[0].pressure = 0;
        MotionEvent clickEvent2 = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(),
                MotionEvent.ACTION_UP, 1, properties, coords, 0,
                1, 1f, 1f, -1, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);
        device.injectEvent(clickEvent2);
    }

    public static void layout(String path) {
        DisplayInfo displayInfo = device.getDisplayInfo();
        if (displayInfo == null) {
            return;
        }
        if (path == null) {
            LayoutShell.get(LAYOUT_DEFAULT_PATH, displayInfo);
        } else {
            LayoutShell.get(path, displayInfo);
        }
    }

    public static void screenshot(String path) {
    }
}