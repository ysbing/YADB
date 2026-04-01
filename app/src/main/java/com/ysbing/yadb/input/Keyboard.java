package com.ysbing.yadb.input;

import android.content.Context;
import android.content.IClipboard;
import android.hardware.input.IInputManager;
import android.os.Build;
import android.os.Bundle;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.ysbing.yadb.layout.LayoutShell;

public class Keyboard {
    private static final String FLAG_ENTER = "\\n";
    private static final int META_CTRL = KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON;
    private static final IClipboard clipboard = IClipboard.
            Stub.asInterface(ServiceManager.getService(Context.CLIPBOARD_SERVICE));

    public static void clear() {
        if (setTextByAccessibility("", false)) {
            return;
        }
        selectAll();
        deleteSelection();
    }

    public static void text(String text) {
        text = text.replace("\\\\n", "\0")
                .replace(FLAG_ENTER, "\n")
                .replace("\0", "\\n");
        if (setTextByAccessibility(text, true)) {
            return;
        }
        if (setClipboardText(text)) {
            pasteClipboard();
        }
    }

    private static boolean setTextByAccessibility(String text, boolean append) {
        LayoutShell shell = new LayoutShell();
        try {
            shell.connect();
            AccessibilityNodeInfo root = null;
            long startTime = System.currentTimeMillis();
            while (root == null && System.currentTimeMillis() - startTime < 5000) {
                root = shell.getUiAutomation().getRootInActiveWindow();
                if (root == null) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
            if (root != null) {
                AccessibilityNodeInfo focused = root.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
                if (focused != null) {
                    String finalDoc = text;
                    if (append) {
                        CharSequence current = focused.getText();
                        // On API 26+, AccessibilityNodeInfo.getText() may return
                        // hint/placeholder text (e.g. X/Twitter's "有什么新鲜事？").
                        // Treat hint text as empty to avoid prepending it to input.
                        if (current != null
                                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                                && focused.isShowingHintText()) {
                            current = null;
                        }
                        finalDoc = (current != null ? current.toString() : "") + text;
                    }
                    Bundle arguments = new Bundle();
                    arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, finalDoc);
                    boolean success = focused.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                    focused.recycle();
                    return success;
                }
                root.recycle();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            try {
                shell.disconnect();
            } catch (Throwable e) {
            }
        }
        return false;
    }

    private static void selectAll() {
        injectKeyCombo(KeyEvent.KEYCODE_A);
    }

    private static void deleteSelection() {
        injectKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL, 0);
        injectKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL, 0);
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
