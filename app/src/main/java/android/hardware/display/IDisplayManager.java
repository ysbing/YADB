package android.hardware.display;

import android.content.IClipboard;
import android.os.IBinder;
import android.view.DisplayInfo;


public interface IDisplayManager {
    DisplayInfo getDisplayInfo(int displayId);

    class Stub {
        public static IDisplayManager asInterface(IBinder obj) {
            return null;
        }
    }
}