package com.ysbing.yadb;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Size;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;

import com.ysbing.yadb.wrappers.SurfaceControl;
import com.ysbing.yadb.wrappers.layout.LayoutShell;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeoutException;

public class Server {

    private static final String FLAG_ENTER = "~ENTER~";
    private static final String FLAG_CLEAR = "~CLEAR~";
    private static final File LAYOUT_DEFAULT_FILE = new File("/data/local/tmp", "yadb_layout_dump.xml");
    private static final File SCREENSHOT_DEFAULT_FILE = new File("/data/local/tmp", "yadb_screenshot.png");

    private static final Device device = new Device();

    public static void keyboard(String text) {
        boolean clearFlags = text.contains(FLAG_CLEAR);
        if (clearFlags) {
            device.injectKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_CTRL_LEFT, 0, KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON);
            device.injectKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_A, 0, KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON);
            device.injectKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_A, 0, KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON);
            device.injectKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_CTRL_LEFT, 0, KeyEvent.META_NUM_LOCK_ON);
            device.injectKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL, 0, KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON);
            device.injectKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL, 0, KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON);
        } else {
            text = text.replace(FLAG_ENTER, "\n");
            boolean ok = device.setClipboardText(text);
            device.injectKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_CTRL_LEFT, 0, KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON);
            device.injectKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_V, 0, KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON);
            device.injectKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_V, 0, KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON);
            device.injectKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_CTRL_LEFT, 0, KeyEvent.META_NUM_LOCK_ON);
            System.out.println("Copy text:" + ok);
        }
    }

    public static void touch(float x, float y, long pressedTime) throws Exception {
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
        device.injectEvent(clickEvent);
        if (pressedTime >= 0L) {
            Thread.sleep(pressedTime);
        }
        coords[0].pressure = 0;
        MotionEvent clickEvent2 = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(),
                MotionEvent.ACTION_UP, 1, properties, coords, 0,
                1, 1f, 1f, -1, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);
        device.injectEvent(clickEvent2);
    }

    public static void layout(String path) throws Exception {
        DisplayInfo displayInfo = device.getDisplayInfo();
        if (displayInfo == null) {
            return;
        }
        if (path == null) {
            LayoutShell.get(LAYOUT_DEFAULT_FILE, displayInfo);
        } else {
            LayoutShell.get(new File(path), displayInfo);
        }
    }

    public static void screenshot(String path) throws Exception {
        Looper.prepareMainLooper();
        DisplayInfo displayInfo = device.getDisplayInfo();
        if (displayInfo == null) {
            return;
        }
        boolean secure = Build.VERSION.SDK_INT < Build.VERSION_CODES.R
                || (Build.VERSION.SDK_INT == Build.VERSION_CODES.R && !"S".equals(Build.VERSION.CODENAME));
        IBinder iBinder = SurfaceControl.createDisplay("yadb", secure);
        Size size = displayInfo.getSize();
        Rect rect = new Rect(0, 0, size.getWidth(), size.getHeight());
        int imageWidth, imageHeight;
        int rotation = displayInfo.getRotation();
        //横屏
        if (rotation == 1 || rotation == 3) {
            imageWidth = size.getHeight();
            imageHeight = size.getWidth();
        } else {
            imageWidth = size.getWidth();
            imageHeight = size.getHeight();
        }
        @SuppressLint("WrongConstant")
        ImageReader imageReader = ImageReader.newInstance(imageWidth, imageHeight, PixelFormat.RGBA_8888, 1);
        Surface surface = imageReader.getSurface();
        SurfaceControl.openTransaction();
        try {
            SurfaceControl.setDisplaySurface(iBinder, surface);
            SurfaceControl.setDisplayProjection(iBinder, displayInfo.getRotation(), rect, rect);
            SurfaceControl.setDisplayLayerStack(iBinder, displayInfo.getLayerStack());
        } finally {
            SurfaceControl.closeTransaction();
        }
        Image image;
        long startTime = System.currentTimeMillis();
        do {
            if (System.currentTimeMillis() - startTime >= 5000) {
                throw new TimeoutException();
            }
            image = imageReader.acquireLatestImage();
        } while (image == null);
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        int width = image.getWidth();
        int height = image.getHeight();
        System.out.println("image:" + image + ",width:" + width + ",height:" + height + ",rotation:" + rotation);
        Matrix matrix = new Matrix();
        switch (rotation) {
            case 0:
                matrix.postRotate(0);
                break;
            case 1:
                matrix.postRotate(270);
                break;
            case 2:
                matrix.postRotate(180);
                break;
            case 3:
                matrix.postRotate(90);
                break;
            default:
                break;
        }
        int pixelStride = planes[0].getPixelStride(), rowStride = planes[0].getRowStride(), rowPadding = rowStride - pixelStride * width;
        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        image.close();
        SurfaceControl.destroyDisplay(iBinder);
        surface.release();
        Bitmap resultBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        bitmap.recycle();
        File file;
        if (path == null) {
            file = SCREENSHOT_DEFAULT_FILE;
        } else {
            file = new File(path);
        }
        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        resultBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        bos.flush();
        bos.close();
        fos.close();
        resultBitmap.recycle();
        System.out.println("screenshot success:" + file.getAbsolutePath());
    }
}
