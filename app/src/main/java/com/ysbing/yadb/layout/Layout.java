package com.ysbing.yadb.layout;

import android.view.DisplayInfo;

import com.ysbing.yadb.screenshot.DisplayManager;
import com.ysbing.yadb.screenshot.ServiceManager;

import java.io.File;

public class Layout {
    private static final File LAYOUT_DEFAULT_FILE = new File("/data/local/tmp", "yadb_layout_dump.xml");

    public static void run(String path) throws Exception {
        DisplayInfo displayInfo = getDisplayInfo();
        if (displayInfo == null) {
            return;
        }
        if (path == null) {
            LayoutShell.get(LAYOUT_DEFAULT_FILE, displayInfo);
        } else {
            LayoutShell.get(new File(path), displayInfo);
        }
    }

    private static DisplayInfo getDisplayInfo() {
        DisplayManager displayManager = ServiceManager.instance.getDisplayManager();
        if (displayManager == null) {
            return null;
        }
        try {
            return displayManager.getDisplayInfo();
        } catch (Exception e) {
            return null;
        }
    }
}
