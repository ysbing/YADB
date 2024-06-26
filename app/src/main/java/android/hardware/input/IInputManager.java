package android.hardware.input;

import android.content.IClipboard;
import android.os.IBinder;
import android.view.InputEvent;

import com.ysbing.yrouter.api.YRouterSystem;

@YRouterSystem
public interface IInputManager {
    boolean injectInputEvent(InputEvent ev, int mode);

    @YRouterSystem
    class Stub {
        public static IInputManager asInterface(IBinder obj) {
            return null;
        }
    }
}