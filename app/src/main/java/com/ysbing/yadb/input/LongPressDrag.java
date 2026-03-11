package com.ysbing.yadb.input;

import android.os.SystemClock;
import android.view.InputDevice;
import android.view.MotionEvent;

public class LongPressDrag {
    public static void run(float x1, float y1, float x2, float y2, long pressDuration, long dragDuration) {
        long downTime = SystemClock.uptimeMillis();
        long eventTime = downTime;

        injectMotionEvent(InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.ACTION_DOWN, downTime, eventTime, x1, y1);

        if (pressDuration > 0) {
            try {
                Thread.sleep(pressDuration);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        eventTime = SystemClock.uptimeMillis();

        int steps = (int) (dragDuration / 10);
        if (steps <= 0) steps = 1;
        for (int i = 0; i <= steps; i++) {
            float alpha = (float) i / steps;
            float currentX = lerp(x1, x2, alpha);
            float currentY = lerp(y1, y2, alpha);
            injectMotionEvent(InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.ACTION_MOVE, downTime, eventTime, currentX, currentY);

            try {
                Thread.sleep(dragDuration / steps);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            eventTime = SystemClock.uptimeMillis();
        }
        injectMotionEvent(InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.ACTION_UP, downTime, eventTime, x2, y2);
    }

    private static float lerp(float x1, float x2, float alpha) {
        return x1 + (x2 - x1) * alpha;
    }


    private static void injectMotionEvent(int source, int action, long downTime, long eventTime, float x, float y) {
        MotionEvent.PointerProperties[] properties = new MotionEvent.PointerProperties[1];
        properties[0] = new MotionEvent.PointerProperties();
        properties[0].id = 0;
        properties[0].toolType = MotionEvent.TOOL_TYPE_FINGER;

        MotionEvent.PointerCoords[] coords = new MotionEvent.PointerCoords[1];
        coords[0] = new MotionEvent.PointerCoords();
        coords[0].x = x;
        coords[0].y = y;
        coords[0].pressure = 1;
        coords[0].size = 1;

        MotionEvent event = MotionEvent.obtain(downTime, eventTime, action, 1, properties, coords, 0,
                0, 1f, 1f, -1, 0, source, 0);
        Keyboard.injectEvent(event);
        event.recycle();
    }
}
