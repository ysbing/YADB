package android.app;

import android.os.Looper;
import android.view.accessibility.AccessibilityNodeInfo;


public final class UiAutomation {
    public UiAutomation(Looper looper, IUiAutomationConnection connection) {
    }

    public AccessibilityNodeInfo getRootInActiveWindow() {
        return null;
    }

    public int getConnectionId() {
        return 0;
    }

    public void connect() {
    }

    public void disconnect() {
    }
}