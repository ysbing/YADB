package com.ysbing.yadb.wrappers;

import android.annotation.SuppressLint;
import android.content.IClipboard;
import android.hardware.display.IDisplayManager;
import android.hardware.input.IInputManager;
import android.os.IBinder;
import android.os.IInterface;

import java.lang.reflect.Method;

@SuppressLint("PrivateApi,DiscouragedPrivateApi")
public final class ServiceManager {

    public static final String PACKAGE_NAME = "com.android.shell";
    public static final int USER_ID = 0;

    private ClipboardManager clipboardManager;
    private DisplayManager displayManager;
    private InputManager inputManager;

    private IInterface getService(String service, String type) {
        try {
            IBinder binder = android.os.ServiceManager.getService(service);
            Method asInterfaceMethod = Class.forName(type + "$Stub").getMethod("asInterface", IBinder.class);
            return (IInterface) asInterfaceMethod.invoke(null, binder);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    public ClipboardManager getClipboardManager() {
        if (clipboardManager == null) {
            IInterface clipboard = getService("clipboard", IClipboard.class.getName());
            if (clipboard == null) {
                // Some devices have no clipboard manager
                // <https://github.com/Genymobile/scrcpy/issues/1440>
                // <https://github.com/Genymobile/scrcpy/issues/1556>
                return null;
            }
            clipboardManager = new ClipboardManager(clipboard);
        }
        return clipboardManager;
    }

    public DisplayManager getDisplayManager() {
        if (displayManager == null) {
            displayManager = new DisplayManager(getService("display", IDisplayManager.class.getName()));
        }
        return displayManager;
    }

    public InputManager getInputManager() {
        if (inputManager == null) {
            inputManager = new InputManager(getService("input", IInputManager.class.getName()));
        }
        return inputManager;
    }
}