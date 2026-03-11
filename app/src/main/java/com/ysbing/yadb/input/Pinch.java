package com.ysbing.yadb.input;

import android.os.SystemClock;
import android.view.InputDevice;
import android.view.MotionEvent;

public class Pinch {

    /**
     * @param centerX       pinch center X
     * @param centerY       pinch center Y
     * @param startDistance  start distance between two fingers (pixels)
     * @param endDistance    end distance between two fingers (pixels)
     * @param duration      animation duration (ms)
     */
    public static void run(float centerX, float centerY,
                           float startDistance, float endDistance, long duration) {

        long downTime = SystemClock.uptimeMillis();
        long eventTime = downTime;

        float halfStart = startDistance / 2f;
        float halfEnd = endDistance / 2f;

        float finger1StartY = centerY - halfStart;
        float finger1EndY = centerY - halfEnd;
        float finger2StartY = centerY + halfStart;
        float finger2EndY = centerY + halfEnd;

        // 1. First finger down (ACTION_DOWN)
        injectTwoPointerEvent(MotionEvent.ACTION_DOWN, downTime, eventTime,
                centerX, finger1StartY,
                centerX, finger2StartY,
                1);

        // 2. Second finger down (ACTION_POINTER_DOWN)
        eventTime += 10;
        int pointerDownAction = MotionEvent.ACTION_POINTER_DOWN
                | (1 << MotionEvent.ACTION_POINTER_INDEX_SHIFT);
        injectTwoPointerEvent(pointerDownAction, downTime, eventTime,
                centerX, finger1StartY,
                centerX, finger2StartY,
                2);

        // 3. Both fingers move (ACTION_MOVE)
        int steps = (int) (duration / 10);
        if (steps <= 0) steps = 1;

        for (int i = 1; i <= steps; i++) {
            float alpha = (float) i / steps;
            float curFinger1Y = lerp(finger1StartY, finger1EndY, alpha);
            float curFinger2Y = lerp(finger2StartY, finger2EndY, alpha);

            try {
                Thread.sleep(duration / steps);
            } catch (InterruptedException e) {
            }
            eventTime = SystemClock.uptimeMillis();

            injectTwoPointerEvent(MotionEvent.ACTION_MOVE, downTime, eventTime,
                    centerX, curFinger1Y,
                    centerX, curFinger2Y,
                    2);
        }

        // 4. Second finger up (ACTION_POINTER_UP)
        eventTime = SystemClock.uptimeMillis();
        int pointerUpAction = MotionEvent.ACTION_POINTER_UP
                | (1 << MotionEvent.ACTION_POINTER_INDEX_SHIFT);
        injectTwoPointerEvent(pointerUpAction, downTime, eventTime,
                centerX, finger1EndY,
                centerX, finger2EndY,
                2);

        // 5. First finger up (ACTION_UP)
        eventTime += 10;
        injectTwoPointerEvent(MotionEvent.ACTION_UP, downTime, eventTime,
                centerX, finger1EndY,
                centerX, finger2EndY,
                1);
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private static void injectTwoPointerEvent(int action, long downTime, long eventTime,
                                               float x1, float y1, float x2, float y2,
                                               int pointerCount) {

        MotionEvent.PointerProperties[] properties = new MotionEvent.PointerProperties[2];
        properties[0] = new MotionEvent.PointerProperties();
        properties[0].id = 0;
        properties[0].toolType = MotionEvent.TOOL_TYPE_FINGER;
        properties[1] = new MotionEvent.PointerProperties();
        properties[1].id = 1;
        properties[1].toolType = MotionEvent.TOOL_TYPE_FINGER;

        MotionEvent.PointerCoords[] coords = new MotionEvent.PointerCoords[2];
        coords[0] = new MotionEvent.PointerCoords();
        coords[0].x = x1;
        coords[0].y = y1;
        coords[0].pressure = 1;
        coords[0].size = 1;
        coords[1] = new MotionEvent.PointerCoords();
        coords[1].x = x2;
        coords[1].y = y2;
        coords[1].pressure = 1;
        coords[1].size = 1;

        MotionEvent event = MotionEvent.obtain(
                downTime, eventTime, action,
                pointerCount, properties, coords,
                0, 0, 1.0f, 1.0f, 0, 0,
                InputDevice.SOURCE_TOUCHSCREEN, 0);
        Keyboard.injectEvent(event);
        event.recycle();
    }
}
