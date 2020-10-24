package com.ysbing.yadb.wrappers;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.IInterface;
import android.view.InputEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@TargetApi(Build.VERSION_CODES.KITKAT)
public final class InputManager {

    public static final int INJECT_INPUT_EVENT_MODE_ASYNC = 0;
    public static final int INJECT_INPUT_EVENT_MODE_WAIT_FOR_RESULT = 1;
    public static final int INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH = 2;

    private final IInterface manager;
    private Method injectInputEventMethod;

    private static Method setDisplayIdMethod;

    public InputManager(IInterface manager) {
        this.manager = manager;
    }

    private Method getInjectInputEventMethod() throws NoSuchMethodException {
        if (injectInputEventMethod == null) {
            injectInputEventMethod = manager.getClass().getMethod("injectInputEvent", InputEvent.class, int.class);
        }
        return injectInputEventMethod;
    }

    public boolean injectInputEvent(InputEvent inputEvent, int mode) {
        try {
            Method method = getInjectInputEventMethod();
            return (boolean) method.invoke(manager, inputEvent, mode);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            return false;
        }
    }

    private static Method getSetDisplayIdMethod() throws NoSuchMethodException {
        if (setDisplayIdMethod == null) {
            setDisplayIdMethod = InputEvent.class.getMethod("setDisplayId", int.class);
        }
        return setDisplayIdMethod;
    }

    public static boolean setDisplayId(InputEvent inputEvent, int displayId) {
        try {
            Method method = getSetDisplayIdMethod();
            method.invoke(inputEvent, displayId);
            return true;
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            return false;
        }
    }
}
