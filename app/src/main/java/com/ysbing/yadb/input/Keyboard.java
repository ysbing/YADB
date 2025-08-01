package com.ysbing.yadb.input;

import android.content.Context;
import android.content.IClipboard;
import android.hardware.input.IInputManager;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;

public class Keyboard {
    private static final String FLAG_ENTER = "\\n";
    private static final String FLAG_CLEAR = "~CLEAR~";
    private static final int META_CTRL = KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON;

    private static final IClipboard clipboard = IClipboard.Stub.asInterface(ServiceManager.getService(Context.CLIPBOARD_SERVICE));
    private static final IInputManager inputManager = IInputManager.Stub.asInterface(ServiceManager.getService(Context.INPUT_SERVICE));

    public static void run(String text) {
        if (text.contains(FLAG_CLEAR)) {
            selectAll();
            deleteSelection();
        } else {
            text = text.replace(FLAG_ENTER, "\n");
            if (setClipboardText(text)) {
                pasteClipboard();
                System.out.println("Copy text: true");
            } else {
                System.out.println("Copy text: false");
            }
        }
    }

    private static void selectAll() {
        injectKeyCombo(KeyEvent.KEYCODE_A);
    }

    private static void deleteSelection() {
        injectKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL, META_CTRL);
        injectKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL, META_CTRL);
    }

    private static void pasteClipboard() {
        injectKeyCombo(KeyEvent.KEYCODE_V);
    }

    private static void injectKeyCombo(int keyCode) {
        injectKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_CTRL_LEFT, META_CTRL);
        injectKeyEvent(KeyEvent.ACTION_DOWN, keyCode, META_CTRL);
        injectKeyEvent(KeyEvent.ACTION_UP, keyCode, META_CTRL);
        injectKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_CTRL_LEFT, 0);
    }


    private static void injectKeyEvent(int action, int keyCode, int metaState) {
        long now = SystemClock.uptimeMillis();
        KeyEvent event = new KeyEvent(now, now, action, keyCode, 0, metaState, KeyCharacterMap.VIRTUAL_KEYBOARD, 0, 0, InputDevice.SOURCE_KEYBOARD);
        injectEvent(event);
    }

    public static void injectEvent(InputEvent inputEvent) {
        IInputManager inputManager = IInputManager.Stub.asInterface(android.os.ServiceManager.getService(Context.INPUT_SERVICE));
        if (inputManager == null) {
            return;
        }
        inputManager.injectInputEvent(inputEvent, 0);
    }

    private static boolean setClipboardText(String text) {
        if (clipboard == null) return false;
        ClipboardManager manager = new ClipboardManager(clipboard);
        String current = getClipboardText();
        if (text.equals(current)) {
            return true;
        }
        return manager.setText(text);
    }

    private static String getClipboardText() {
        if (clipboard == null) return null;
        ClipboardManager manager = new ClipboardManager(clipboard);
        CharSequence text = manager.getText();
        return text != null ? text.toString() : null;
    }

    public static void readClipboard() {
        System.out.println(getClipboardText());
    }

    public static void writeClipboard(String text) {
        setClipboardText(text);
    }
}
