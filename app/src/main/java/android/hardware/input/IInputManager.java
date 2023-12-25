package android.hardware.input;

import android.view.InputEvent;

import com.ysbing.yrouter.api.YRouterSystem;

@YRouterSystem
public interface IInputManager extends android.os.IInterface {
    boolean injectInputEvent(InputEvent ev, int mode);
}