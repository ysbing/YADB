package android.hardware.display;

import android.content.IClipboard;
import android.os.IBinder;
import android.view.DisplayInfo;

import com.ysbing.yrouter.api.YRouterSystem;

@YRouterSystem
public interface IDisplayManager {
    DisplayInfo getDisplayInfo(int displayId);

    @YRouterSystem
    class Stub {
        public static IDisplayManager asInterface(IBinder obj) {
            return null;
        }
    }
}