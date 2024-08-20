package com.daomai.stub.layout;

import android.content.Context;
import android.hardware.display.IDisplayManager;
import android.view.Display;
import android.view.DisplayInfo;

public class Layout {
    public static void run(String path) throws Exception {
        DisplayInfo displayInfo = getDisplayInfo();
        if (displayInfo == null) {
            return;
        }
        LayoutShell.get(displayInfo);
    }

    private static DisplayInfo getDisplayInfo() {
        IDisplayManager clipboard = IDisplayManager.Stub.asInterface(android.os.ServiceManager.getService(Context.DISPLAY_SERVICE));
        if (clipboard == null) {
            return null;
        }
        return clipboard.getDisplayInfo(Display.DEFAULT_DISPLAY);
    }
}
