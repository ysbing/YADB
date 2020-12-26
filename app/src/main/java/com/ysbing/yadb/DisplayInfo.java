package com.ysbing.yadb;

import android.util.Size;

public final class DisplayInfo {
    private final Size size;
    private final int rotation;
    private final int layerStack;
    private final int flags;

    public DisplayInfo(Size size, int rotation, int layerStack, int flags) {
        this.size = size;
        this.rotation = rotation;
        this.layerStack = layerStack;
        this.flags = flags;
    }

    public Size getSize() {
        return size;
    }

    public int getRotation() {
        return rotation;
    }

    public int getLayerStack() {
        return layerStack;
    }

    public int getFlags() {
        return flags;
    }
}

