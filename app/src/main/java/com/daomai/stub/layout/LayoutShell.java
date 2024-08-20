package com.daomai.stub.layout;

import android.app.UiAutomation;
import android.app.UiAutomationConnection;
import android.os.HandlerThread;
import android.view.DisplayInfo;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.concurrent.TimeoutException;

public class LayoutShell {
    private static final String HANDLER_THREAD_NAME = "LayoutShellThread";
    private final HandlerThread mHandlerThread = new HandlerThread(HANDLER_THREAD_NAME);
    private UiAutomation mUiAutomation = null;

    public static void get(DisplayInfo displayInfo) throws Exception {
        LayoutShell shell = new LayoutShell();
        try {
            shell.connect();
            AccessibilityNodeInfo info;
            long startTime = System.currentTimeMillis();
            do {
                if (System.currentTimeMillis() - startTime >= 3000) {
                    throw new TimeoutException();
                }
                info = shell.mUiAutomation.getRootInActiveWindow();
            } while (info == null);
            String content = AccessibilityNodeInfoDumper.getWindowJSONHierarchy(info, displayInfo);
            System.out.println(content);
        } finally {
            shell.disconnect();
        }
    }

    public void connect() {
        if (mHandlerThread.isAlive()) {
            throw new IllegalStateException("Already connected!");
        }
        mHandlerThread.start();
        mUiAutomation = new UiAutomation(mHandlerThread.getLooper(), new UiAutomationConnection());
        mUiAutomation.connect();
    }

    public void disconnect() {
        if (!mHandlerThread.isAlive()) {
            throw new IllegalStateException("Already disconnected!");
        }
        mUiAutomation.disconnect();
        mHandlerThread.quit();
    }
}
