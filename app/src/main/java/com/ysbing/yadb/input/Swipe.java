package com.ysbing.yadb.input;

import android.os.SystemClock;
import android.view.InputDevice;
import android.view.MotionEvent;

public class Swipe {
    public static void run(float x1, float y1, float x2, float y2, long duration) {
        long downTime = SystemClock.uptimeMillis();
        long eventTime = downTime;

        injectMotionEvent(InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.ACTION_DOWN, downTime, eventTime, x1, y1);

        // 先沿滑动方向移动一小段距离(5%)，使系统脱离长按判定区域，避免被识别为长按手势
        float escapeRatio = 0.05f;
        float escapeX = lerp(x1, x2, escapeRatio);
        float escapeY = lerp(y1, y2, escapeRatio);

        eventTime += 10;
        injectMotionEvent(InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.ACTION_MOVE, downTime, eventTime, escapeX, escapeY);

        int steps = (int) (duration / 5);
        if (steps <= 0) steps = 1;


        for (int i = 1; i <= steps; i++) {
            float linearProgress = (float) i / steps;

            float realProgress = escapeRatio + (1.0f - escapeRatio) * linearProgress;

            // 减速插值
            float interpolated = 1.0f - (1.0f - realProgress) * (1.0f - realProgress);

            float currentX = lerp(x1, x2, interpolated);
            float currentY = lerp(y1, y2, interpolated);

            injectMotionEvent(InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.ACTION_MOVE, downTime, eventTime, currentX, currentY);

            try {
                Thread.sleep(duration / steps);
            } catch (InterruptedException e) {}
            eventTime = SystemClock.uptimeMillis();
        }

        injectMotionEvent(InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.ACTION_UP, downTime, eventTime, x2, y2);
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
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

        MotionEvent event = MotionEvent.obtain(downTime, eventTime, action, 1, properties, coords, 0, 0, 1.0f, 1.0f, 0, 0, source, 0);
        Keyboard.injectEvent(event);
        event.recycle();
    }
}
