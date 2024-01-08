package com.ysbing.yadb.wrappers;

import android.content.ClipData;
import android.content.IClipboard;
import android.os.Build;
import android.os.IInterface;

public class ClipboardManager {
    private final IClipboard manager;

    public ClipboardManager(IInterface manager) {
        this.manager = (IClipboard) manager;
    }

    private static ClipData getPrimaryClip(IClipboard manager) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return manager.getPrimaryClip(ServiceManager.PACKAGE_NAME);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            return manager.getPrimaryClip(ServiceManager.PACKAGE_NAME, ServiceManager.USER_ID);
        } else {
            return manager.getPrimaryClip(ServiceManager.PACKAGE_NAME, null, ServiceManager.USER_ID, 0);
        }
    }

    private static void setPrimaryClip(IClipboard manager, ClipData clipData) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            manager.setPrimaryClip(clipData, ServiceManager.PACKAGE_NAME);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            manager.setPrimaryClip(clipData, ServiceManager.PACKAGE_NAME, ServiceManager.USER_ID);
        } else {
            manager.setPrimaryClip(clipData, ServiceManager.PACKAGE_NAME, null, ServiceManager.USER_ID, 0);
        }
    }

    public CharSequence getText() {
        try {
            ClipData clipData = getPrimaryClip(manager);
            if (clipData == null || clipData.getItemCount() == 0) {
                return null;
            }
            return clipData.getItemAt(0).getText();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean setText(CharSequence text) {
        try {
            ClipData clipData = ClipData.newPlainText(null, text);
            setPrimaryClip(manager, clipData);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}