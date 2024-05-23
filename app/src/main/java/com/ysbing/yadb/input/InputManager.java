package com.ysbing.yadb.input;

import android.hardware.input.IInputManager;
import android.os.IInterface;
import android.view.InputEvent;

public final class InputManager {

    public static final int INJECT_INPUT_EVENT_MODE_ASYNC = 0;

    private final IInputManager manager;

    public InputManager(IInterface manager) {
        this.manager = (IInputManager) manager;
    }


    public boolean injectInputEvent(InputEvent inputEvent, int mode) {
        return manager.injectInputEvent(inputEvent, mode);
    }

}
