package android.view.accessibility;

import android.accessibilityservice.IAccessibilityServiceConnection;
import android.os.Bundle;

import com.ysbing.yrouter.api.YRouterSystem;

@YRouterSystem
public final class AccessibilityInteractionClient implements IAccessibilityInteractionConnectionCallback {
    public static AccessibilityInteractionClient getInstance() {
        return null;
    }

    public AccessibilityNodeInfo getRootInActiveWindow(int connectionId) {
        return null;
    }

    public static IAccessibilityServiceConnection getConnection(int connectionId) {
        return null;
    }

    public AccessibilityNodeInfo findAccessibilityNodeInfoByAccessibilityId(int connectionId,
                                                                            int accessibilityWindowId, long accessibilityNodeId, boolean bypassCache,
                                                                            int prefetchFlags, Bundle arguments) {
        return null;
    }
}