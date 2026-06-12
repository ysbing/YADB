package com.ysbing.yadb.layout;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

class AccessibilityNodeInfoHelper {
    AccessibilityNodeInfoHelper() {
    }

    static Rect getVisibleBoundsInScreen(AccessibilityNodeInfo node ) {
        if (node == null) {
            return null;
        }
        Rect nodeRect = new Rect();
        node.getBoundsInScreen(nodeRect);
        return nodeRect;
    }
}