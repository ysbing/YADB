package com.ysbing.yadb.screenshot;

import android.hardware.display.IDisplayManager;
import android.os.IInterface;
import android.view.Display;
import android.view.DisplayInfo;


public final class DisplayManager {
    private final IDisplayManager manager;

    public DisplayManager(IInterface manager) {
        this.manager = (IDisplayManager) manager;
    }

    public DisplayInfo getDisplayInfo() {
        return manager.getDisplayInfo(Display.DEFAULT_DISPLAY);
    }
}
