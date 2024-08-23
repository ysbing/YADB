package android.hardware.input;

import android.content.IClipboard;
import android.os.IBinder;
import android.view.InputEvent;

public interface IInputManager {
    boolean injectInputEvent(InputEvent ev, int mode);

    class Stub {
        public static IInputManager asInterface(IBinder obj) {
            return null;
        }
    }
}