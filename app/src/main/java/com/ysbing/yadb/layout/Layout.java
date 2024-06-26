package com.ysbing.yadb.layout;

import android.content.Context;
import android.hardware.display.IDisplayManager;
import android.view.Display;
import android.view.DisplayInfo;

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
        IDisplayManager clipboard = IDisplayManager.Stub.asInterface(android.os.ServiceManager.getService(Context.DISPLAY_SERVICE));
        if (clipboard == null) {
            return null;
        }
        return clipboard.getDisplayInfo(Display.DEFAULT_DISPLAY);
    }
}
