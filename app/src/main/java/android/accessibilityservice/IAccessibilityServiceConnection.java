package android.accessibilityservice;

import android.os.Bundle;
import android.view.accessibility.IAccessibilityInteractionConnectionCallback;

import com.ysbing.yrouter.api.YRouterSystem;

@YRouterSystem
public interface IAccessibilityServiceConnection {
    String[] findAccessibilityNodeInfoByAccessibilityId(int accessibilityWindowId,
                                                        long accessibilityNodeId, int interactionId,
                                                        IAccessibilityInteractionConnectionCallback callback, int flags, long threadId,
                                                        Bundle arguments);
}