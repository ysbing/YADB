package android.app;

import android.os.Looper;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import com.ysbing.yrouter.api.YRouterSystem;

import java.util.List;

@YRouterSystem
public final class UiAutomation {
    public UiAutomation(Looper looper, IUiAutomationConnection connection) {
    }

    public void connect() {
    }

    public void disconnect() {
    }

    public int getConnectionId() {
        return 0;
    }

    public AccessibilityNodeInfo getRootInActiveWindow() {
        return null;
    }

    public List<AccessibilityWindowInfo> getWindows() {
        return null;
    }
}