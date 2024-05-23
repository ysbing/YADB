package com.ysbing.yadb.input;

import android.os.SystemClock;
import android.view.InputDevice;
import android.view.MotionEvent;

public class Touch {
    public static void run(float x, float y, long pressedTime) throws InterruptedException {
        long downTime = SystemClock.uptimeMillis();
        MotionEvent.PointerProperties[] properties = new MotionEvent.PointerProperties[1];
        properties[0] = new MotionEvent.PointerProperties();
        properties[0].id = 0;
        properties[0].toolType = MotionEvent.TOOL_TYPE_FINGER;
        MotionEvent.PointerCoords[] coords = new MotionEvent.PointerCoords[1];
        coords[0] = new MotionEvent.PointerCoords();
        coords[0].x = x;
        coords[0].y = y;
        coords[0].pressure = 1;
        MotionEvent clickEvent = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(),
                MotionEvent.ACTION_DOWN, 1, properties, coords, 0,
                0, 1f, 1f, -1, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);
        Keyboard.injectEvent(clickEvent);
        if (pressedTime >= 0L) {
            Thread.sleep(pressedTime);
        }
        coords[0].pressure = 0;
        MotionEvent clickEvent2 = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(),
                MotionEvent.ACTION_UP, 1, properties, coords, 0,
                1, 1f, 1f, -1, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);
        Keyboard.injectEvent(clickEvent2);
    }
}
