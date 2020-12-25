package com.ysbing.yadb;

import android.os.SystemClock;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;

import com.ysbing.yadb.wrappers.ClipboardManager;
import com.ysbing.yadb.wrappers.DisplayManager;
import com.ysbing.yadb.wrappers.InputManager;
import com.ysbing.yadb.wrappers.ServiceManager;

import java.util.concurrent.atomic.AtomicBoolean;

public final class Device {

    private final ServiceManager serviceManager = new ServiceManager();

    private final AtomicBoolean isSettingClipboard = new AtomicBoolean();

    public boolean injectEvent(InputEvent inputEvent, int mode) {
        return serviceManager.getInputManager().injectInputEvent(inputEvent, mode);
    }

    public boolean injectEvent(InputEvent event) {
        return injectEvent(event, InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);
    }

    public boolean injectKeyEvent(int action, int keyCode, int repeat, int metaState) {
        long now = SystemClock.uptimeMillis();
        KeyEvent event = new KeyEvent(now, now, action, keyCode, repeat, metaState, KeyCharacterMap.VIRTUAL_KEYBOARD, 0, 0,
                InputDevice.SOURCE_KEYBOARD);
        return injectEvent(event);
    }

    public boolean injectKeycode(int keyCode) {
        return injectKeyEvent(KeyEvent.ACTION_DOWN, keyCode, 0, 0) && injectKeyEvent(KeyEvent.ACTION_UP, keyCode, 0, 0);
    }

    public String getClipboardText() {
        ClipboardManager clipboardManager = serviceManager.getClipboardManager();
        if (clipboardManager == null) {
            return null;
        }
        CharSequence s = clipboardManager.getText();
        if (s == null) {
            return null;
        }
        return s.toString();
    }

    public boolean setClipboardText(String text) {
        ClipboardManager clipboardManager = serviceManager.getClipboardManager();
        if (clipboardManager == null) {
            return false;
        }

        String currentClipboard = getClipboardText();
        if (currentClipboard != null && currentClipboard.equals(text)) {
            // The clipboard already contains the requested text.
            // Since pasting text from the computer involves setting the device clipboard, it could be set twice on a copy-paste. This would cause
            // the clipboard listeners to be notified twice, and that would flood the Android keyboard clipboard history. To workaround this
            // problem, do not explicitly set the clipboard text if it already contains the expected content.
            return false;
        }

        isSettingClipboard.set(true);
        boolean ok = clipboardManager.setText(text);
        isSettingClipboard.set(false);
        return ok;
    }


    public DisplayInfo getDisplayInfo() {
        DisplayManager displayManager = serviceManager.getDisplayManager();
        if (displayManager == null) {
            return null;
        }
        return displayManager.getDisplayInfo();
    }
}
