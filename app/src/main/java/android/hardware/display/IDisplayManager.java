package android.hardware.display;

import android.view.DisplayInfo;

import com.ysbing.yrouter.api.YRouterSystem;

@YRouterSystem
public interface IDisplayManager extends android.os.IInterface {
    DisplayInfo getDisplayInfo(int displayId);
}