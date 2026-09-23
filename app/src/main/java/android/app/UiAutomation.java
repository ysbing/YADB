package android.app;

import android.os.Looper;
import android.view.accessibility.AccessibilityNodeInfo;

import com.ysbing.yrouter.api.YRouterSystem;

@YRouterSystem
public final class UiAutomation {
    public UiAutomation(Looper looper, IUiAutomationConnection connection) {
    }

    public AccessibilityNodeInfo getRootInActiveWindow() {
        return null;
    }

    public int getConnectionId() {
        return 0;
    }

    /**
     * Flag for {@link #connect(int)}: existing accessibility services should continue to
     * run, and new ones may start. Mirrors
     * {@code UiAutomation.FLAG_DONT_SUPPRESS_ACCESSIBILITY_SERVICES} (introduced in API 24).
     */
    public static final int FLAG_DONT_SUPPRESS_ACCESSIBILITY_SERVICES = 0x00000001;

    public void connect() {
    }

    public void connect(int flags) {
    }

    public void disconnect() {
    }
}