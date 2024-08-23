package com.daomai.stub;
import com.daomai.stub.Main;

import android.content.ClipData;
import android.content.IClipboard;
import android.os.Build;
import android.content.Context;
import android.hardware.input.IInputManager;
import android.os.SystemClock;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;

public class DMClipboard {
    private static final String FLAG_ENTER = "\\n";
    private static final String FLAG_CLEAR = "~CLEAR~";


    public static void run(String text) {
        boolean clearFlags = text.contains(FLAG_CLEAR);
        if (clearFlags) {
            injectKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_CTRL_LEFT, KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON);
            injectKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_A, KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON);
            injectKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_A, KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON);
            injectKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_CTRL_LEFT, KeyEvent.META_NUM_LOCK_ON);
            injectKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL, KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON);
            injectKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL, KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON);
        } else {
            text = text.replace(FLAG_ENTER, "\n");
            boolean ok = setClipboardText(text);
            injectKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_CTRL_LEFT, KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON);
            injectKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_V, KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON);
            injectKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_V, KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON);
            injectKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_CTRL_LEFT, KeyEvent.META_NUM_LOCK_ON);
            System.out.println("Copy text:" + ok);
        }
    }

    private static void injectKeyEvent(int action, int keyCode, int metaState) {
        long now = SystemClock.uptimeMillis();
        KeyEvent event = new KeyEvent(now, now, action, keyCode, 0, metaState, KeyCharacterMap.VIRTUAL_KEYBOARD, 0, 0,
                InputDevice.SOURCE_KEYBOARD);
        injectEvent(event);
    }

    private static boolean setClipboardText(String text) {
        IClipboard clipboard = IClipboard.Stub.asInterface(android.os.ServiceManager.getService(Context.CLIPBOARD_SERVICE));
        if (clipboard == null) {
            return false;
        }
        ClipboardManager clipboardManager = new ClipboardManager(clipboard);
        String currentClipboard = getClipboardText();
        if (currentClipboard != null && currentClipboard.equals(text)) {
            return true;
        }

        return clipboardManager.setText(text);
    }

    private static String getClipboardText() {
        IClipboard clipboard = IClipboard.Stub.asInterface(android.os.ServiceManager.getService(Context.CLIPBOARD_SERVICE));
        if (clipboard == null) {
            return null;
        }
        ClipboardManager clipboardManager = new ClipboardManager(clipboard);
        CharSequence s = clipboardManager.getText();
        if (s == null) {
            return null;
        }
        return s.toString();
    }

    public static void injectEvent(InputEvent inputEvent) {
        IInputManager inputManager = IInputManager.Stub.asInterface(android.os.ServiceManager.getService(Context.INPUT_SERVICE));
        if (inputManager == null) {
            return;
        }
        inputManager.injectInputEvent(inputEvent, 0);
    }

    public static void readClipboard() {
        String clipboard = getClipboardText();
        System.out.println(clipboard);
    }
}

class ClipboardManager {
    private final IClipboard manager;

    public ClipboardManager(IClipboard manager) {
        this.manager = manager;
    }

    private static ClipData getPrimaryClip(IClipboard manager) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return manager.getPrimaryClip(Main.PACKAGE_NAME);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return manager.getPrimaryClip(Main.PACKAGE_NAME, Main.USER_ID);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            try {
                return manager.getPrimaryClip(Main.PACKAGE_NAME, null, Main.USER_ID);
            } catch (NoSuchMethodError e) {
                return manager.getPrimaryClip(Main.PACKAGE_NAME, Main.USER_ID);
            }
        } else {
            try {
                return manager.getPrimaryClip(Main.PACKAGE_NAME, null, Main.USER_ID, 0);
            } catch (NoSuchMethodError e) {
                return manager.getPrimaryClip(Main.PACKAGE_NAME, null, Main.USER_ID, 0, null);
            }
        }
    }

    private static void setPrimaryClip(IClipboard manager, ClipData clipData) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            manager.setPrimaryClip(clipData, Main.PACKAGE_NAME);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            manager.setPrimaryClip(clipData, Main.PACKAGE_NAME, Main.USER_ID);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            try {
                manager.setPrimaryClip(clipData, Main.PACKAGE_NAME, null, Main.USER_ID);
            } catch (NoSuchMethodError e) {
                manager.setPrimaryClip(clipData, Main.PACKAGE_NAME, Main.USER_ID);
            }
        } else {
            manager.setPrimaryClip(clipData, Main.PACKAGE_NAME, null, Main.USER_ID, 0);
        }
    }

    public CharSequence getText() {
        try {
            ClipData clipData = getPrimaryClip(manager);
            if (clipData == null || clipData.getItemCount() == 0) {
                return null;
            }
            return clipData.getItemAt(0).getText();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean setText(CharSequence text) {
        try {
            ClipData clipData = ClipData.newPlainText(null, text);
            setPrimaryClip(manager, clipData);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}