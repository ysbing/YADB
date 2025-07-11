package com.ysbing.yadb.screenshot;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.hardware.display.IDisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.IBinder;
import android.view.Display;
import android.view.DisplayInfo;
import android.view.Surface;
import android.view.SurfaceControl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeoutException;

public class Screenshot {
    private static final File SCREENSHOT_DEFAULT_FILE = new File("/data/local/tmp", "yadb_screenshot.png");

    public static void run(String path) throws Exception {
        DisplayInfo displayInfo = getDisplayInfo();
        if (displayInfo == null) {
            return;
        }
        Rect rect = new Rect(0, 0, displayInfo.logicalWidth, displayInfo.logicalHeight);
        int imageWidth, imageHeight;
        int rotation = displayInfo.rotation;
        //横屏
        if (rotation == 1 || rotation == 3) {
            imageWidth = displayInfo.logicalHeight;
            imageHeight = displayInfo.logicalWidth;
        } else {
            imageWidth = displayInfo.logicalWidth;
            imageHeight = displayInfo.logicalHeight;
        }
        ImageReader imageReader = ImageReader.newInstance(imageWidth, imageHeight, PixelFormat.RGBA_8888, 1);
        Surface surface = imageReader.getSurface();
        IBinder display = null;
        VirtualDisplay virtualDisplay = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            boolean secure = Build.VERSION.SDK_INT < Build.VERSION_CODES.R
                    || (Build.VERSION.SDK_INT == Build.VERSION_CODES.R && !"S".equals(Build.VERSION.CODENAME));
            display = SurfaceControl.createDisplay("yadb", secure);
            SurfaceControl.openTransaction();
            try {
                SurfaceControl.setDisplaySurface(display, surface);
                SurfaceControl.setDisplayProjection(display, displayInfo.rotation, rect, rect);
                SurfaceControl.setDisplayLayerStack(display, displayInfo.layerStack);
            } finally {
                SurfaceControl.closeTransaction();
            }
        } else {
            virtualDisplay = DisplayManager.createVirtualDisplay("yadb", imageWidth, imageHeight, Display.DEFAULT_DISPLAY, surface);
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
        if (display != null) {
            SurfaceControl.destroyDisplay(display);
        }
        if (virtualDisplay != null) {
            virtualDisplay.release();
        }
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

    private static DisplayInfo getDisplayInfo() {
        IDisplayManager clipboard = IDisplayManager.Stub.asInterface(android.os.ServiceManager.getService(Context.DISPLAY_SERVICE));
        if (clipboard == null) {
            return null;
        }
        return clipboard.getDisplayInfo(Display.DEFAULT_DISPLAY);
    }
}
