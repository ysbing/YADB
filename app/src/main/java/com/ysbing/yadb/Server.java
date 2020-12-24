package com.ysbing.yadb;

import android.os.SystemClock;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

import java.util.Arrays;

public class Server {
    private static final String ARG_KEY_BOARD = "-keyboard";
    private static final String ARG_TOUCH = "-touch";
    private static final String FLAG_SPACE = "~SPACE~";
    private static final String FLAG_ENTER = "~ENTER~";
    private static final String FLAG_CLEAR = "~CLEAR~";

    public static void main(String[] args) {
        try {
            System.out.println("yadb:" + Arrays.toString(args));
            if (check(args[0])) {
                Device device = new Device();
                switch (args[0]) {
                    case ARG_KEY_BOARD:
                        String text = args[1];
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
                        break;
                    case ARG_TOUCH:
                        long downTime = SystemClock.uptimeMillis();
                        MotionEvent.PointerProperties[] properties = new MotionEvent.PointerProperties[1];
                        properties[0] = new MotionEvent.PointerProperties();
                        properties[0].id = 0;
                        properties[0].toolType = MotionEvent.TOOL_TYPE_FINGER;
                        MotionEvent.PointerCoords[] coords = new MotionEvent.PointerCoords[1];
                        coords[0] = new MotionEvent.PointerCoords();
                        coords[0].x = Float.parseFloat(args[1]);
                        coords[0].y = Float.parseFloat(args[2]);
                        coords[0].pressure = 1;
                        MotionEvent click_event = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(),
                                MotionEvent.ACTION_DOWN, 1, properties, coords, 0,
                                0, 1f, 1f, -1, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);
                        device.injectEvent(click_event);
                        if (args.length == 4) {
                            try {
                                Thread.sleep(Long.parseLong(args[3]));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        coords[0].pressure = 0;
                        MotionEvent click_event2 = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(),
                                MotionEvent.ACTION_UP, 1, properties, coords, 0,
                                1, 1f, 1f, -1, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);
                        device.injectEvent(click_event2);
                        break;
                    default:
                        break;
                }
            } else {
                System.out.println("参数不对");
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static boolean check(String arg) {
        return arg.equals(ARG_KEY_BOARD) || arg.equals(ARG_TOUCH);
    }
}