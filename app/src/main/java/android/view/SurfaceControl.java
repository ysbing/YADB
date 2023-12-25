package android.view;

import android.graphics.Rect;
import android.os.IBinder;

import com.ysbing.yrouter.api.YRouterSystem;

@YRouterSystem
public final class SurfaceControl {
    public static void openTransaction() {
    }

    public static void closeTransaction() {
    }

    public static void setDisplayProjection(IBinder displayToken,
                                            int orientation, Rect layerStackRect, Rect displayRect) {
    }

    public static void setDisplayLayerStack(IBinder displayToken, int layerStack) {
    }

    public static void setDisplaySurface(IBinder displayToken, Surface surface) {
    }

    public static IBinder createDisplay(String name, boolean secure) {
        return null;
    }

    public static void destroyDisplay(IBinder displayToken) {
    }
}