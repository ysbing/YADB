package com.ysbing.yadb.wrappers.layout;

import android.annotation.SuppressLint;
import android.app.IUiAutomationConnection;
import android.app.UiAutomation;
import android.app.UiAutomationConnection;
import android.os.HandlerThread;
import android.os.Looper;
import android.view.accessibility.AccessibilityNodeInfo;

import com.ysbing.yadb.DisplayInfo;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressLint("SoonBlockedPrivateApi")
public class LayoutShell {
    private static final String HANDLER_THREAD_NAME = "LayoutShellThread";
    private final HandlerThread mHandlerThread = new HandlerThread(HANDLER_THREAD_NAME);
    private Method mConnectMethod = null;
    private Method mDisconnectMethod = null;
    private UiAutomation mUiAutomation = null;

    public static void get(String path, DisplayInfo displayInfo) {
        LayoutShell shell = new LayoutShell();
        try {
            shell.connect();
            AccessibilityNodeInfo info = null;
            while (info == null) {
                info = shell.mUiAutomation.getRootInActiveWindow();
            }
            String content = AccessibilityNodeInfoDumper.getWindowXMLHierarchy(info, displayInfo);
            File file = new File(path);
            FileWriter writer = new FileWriter(file);
            writer.write(content);
            writer.close();
            System.out.println("layout dumped to:" + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                shell.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void connect() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        if (mHandlerThread.isAlive()) {
            throw new IllegalStateException("Already connected!");
        }
        mHandlerThread.start();
        mUiAutomation = UiAutomation.class.getConstructor(Looper.class, IUiAutomationConnection.class).newInstance(mHandlerThread.getLooper(), new UiAutomationConnection());
        getConnectMethod().invoke(mUiAutomation);
    }

    public void disconnect() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (!mHandlerThread.isAlive()) {
            throw new IllegalStateException("Already disconnected!");
        }
        getDisConnectMethod().invoke(mUiAutomation);
        mHandlerThread.quit();
    }

    private Method getConnectMethod() throws NoSuchMethodException {
        if (mConnectMethod == null) {
            mConnectMethod = UiAutomation.class.getDeclaredMethod("connect");
        }
        return mConnectMethod;
    }

    private Method getDisConnectMethod() throws NoSuchMethodException {
        if (mDisconnectMethod == null) {
            mDisconnectMethod = UiAutomation.class.getDeclaredMethod("disconnect");
        }
        return mDisconnectMethod;
    }
}
