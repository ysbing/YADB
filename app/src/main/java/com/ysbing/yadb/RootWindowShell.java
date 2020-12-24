package com.ysbing.yadb;

import android.app.UiAutomation;
import android.app.UiAutomationConnection;
import android.os.HandlerThread;
import android.view.accessibility.AccessibilityNodeInfo;

public class RootWindowShell {
    private static final String TAG = "RootWindowShell";
    private static final String HANDLER_THREAD_NAME = "MyUiAutomatorHandlerThread";

    private final HandlerThread mHandlerThread = new HandlerThread(HANDLER_THREAD_NAME);

    private UiAutomation mUiAutomation;

    public void connect() {
        if (mHandlerThread.isAlive()) {
            throw new IllegalStateException("Already connected!");
        }
        mHandlerThread.start();
        mUiAutomation = new UiAutomation(mHandlerThread.getLooper(),
                new UiAutomationConnection());
        mUiAutomation.connect();
    }

    public void disconnect() {
        if (!mHandlerThread.isAlive()) {
            throw new IllegalStateException("Already disconnected!");
        }
        mUiAutomation.disconnect();
        mHandlerThread.quit();
    }

    public UiAutomation getUiAutomation() {
        return mUiAutomation;
    }

    public static void get() {
        RootWindowShell shell = new RootWindowShell();
        shell.connect();
        UiAutomation uiAutomation = shell.getUiAutomation();
        AccessibilityNodeInfo info = uiAutomation.getRootInActiveWindow();
        while (info == null) {
            info = uiAutomation.getRootInActiveWindow();
        }
        System.out.println("RootWindowShell,info:" + info);
        shell.disconnect();
    }
}
