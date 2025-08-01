package com.ysbing.yadb.input;

import android.content.ClipData;
import android.content.IClipboard;
import android.os.Build;

public class ClipboardManager {
    private static final String PACKAGE_NAME = "com.android.shell";
    private static final int USER_ID = 0;
    private final IClipboard manager;

    public ClipboardManager(IClipboard manager) {
        this.manager = manager;
    }

    private static ClipData getPrimaryClip(IClipboard manager) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return manager.getPrimaryClip(PACKAGE_NAME);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return manager.getPrimaryClip(PACKAGE_NAME, USER_ID);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            try {
                return manager.getPrimaryClip(PACKAGE_NAME, null, USER_ID);
            } catch (NoSuchMethodError e) {
                return manager.getPrimaryClip(PACKAGE_NAME, USER_ID);
            }
        } else {
            try {
                return manager.getPrimaryClip(PACKAGE_NAME, null, USER_ID, 0);
            } catch (NoSuchMethodError e) {
                try {
                    return manager.getPrimaryClip(PACKAGE_NAME, null, USER_ID, 0, null);
                } catch (NoSuchMethodError error) {
                    return manager.getPrimaryClip(PACKAGE_NAME, null, null, null, USER_ID, 0, false);
                }
            }
        }
    }

    private static void setPrimaryClip(IClipboard manager, ClipData clipData) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            manager.setPrimaryClip(clipData, PACKAGE_NAME);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            manager.setPrimaryClip(clipData, PACKAGE_NAME, USER_ID);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            try {
                manager.setPrimaryClip(clipData, PACKAGE_NAME, null, USER_ID);
            } catch (NoSuchMethodError e) {
                manager.setPrimaryClip(clipData, PACKAGE_NAME, USER_ID);
            }
        } else {
            try {
                manager.setPrimaryClip(clipData, PACKAGE_NAME, null, USER_ID, 0);
            } catch (NoSuchMethodError e) {
                manager.setPrimaryClip(clipData, PACKAGE_NAME, null, USER_ID, 0, false);
            }
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