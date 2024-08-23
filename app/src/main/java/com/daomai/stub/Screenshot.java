package com.daomai.stub;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.hardware.display.IDisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.IBinder;
import android.view.Display;
import android.view.DisplayInfo;
import android.view.Surface;
import android.view.SurfaceControl;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeoutException;
import android.util.Log;

import android.util.Base64;
import java.io.ByteArrayOutputStream;
import org.json.JSONObject;

public class Screenshot {
    public static void run(String path) throws Exception {
        long startTime = System.currentTimeMillis(); // Bắt đầu đo thời gian
        
        DisplayInfo displayInfo = getDisplayInfo();
        if (displayInfo == null) {
            throw new Exception("Unable to get display info.");
        }

        boolean secure = Build.VERSION.SDK_INT < Build.VERSION_CODES.R
                || (Build.VERSION.SDK_INT == Build.VERSION_CODES.R && !"S".equals(Build.VERSION.CODENAME));
        IBinder iBinder = SurfaceControl.createDisplay("daomai", secure);
        Rect rect = new Rect(0, 0, displayInfo.logicalWidth, displayInfo.logicalHeight);
        int imageWidth, imageHeight;
        int rotation = displayInfo.rotation;

        if (rotation == 1 || rotation == 3) {
            imageWidth = displayInfo.logicalHeight;
            imageHeight = displayInfo.logicalWidth;
        } else {
            imageWidth = displayInfo.logicalWidth;
            imageHeight = displayInfo.logicalHeight;
        }

        ImageReader imageReader = ImageReader.newInstance(imageWidth, imageHeight, PixelFormat.RGBA_8888, 1);
        Surface surface = imageReader.getSurface();

        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                SurfaceControl.openTransaction();
                try {
                    SurfaceControl.setDisplaySurface(iBinder, surface);
                    SurfaceControl.setDisplayProjection(iBinder, displayInfo.rotation, rect, rect);
                    SurfaceControl.setDisplayLayerStack(iBinder, displayInfo.layerStack);
                } finally {
                    SurfaceControl.closeTransaction();
                }
            } else {
                DisplayManager.createVirtualDisplay("daomai", imageWidth, imageHeight, Display.DEFAULT_DISPLAY, surface);
            }

            Image image;
            long startCaptureTime = System.currentTimeMillis(); // Bắt đầu chụp màn hình
            do {
                if (System.currentTimeMillis() - startCaptureTime >= 3000) {
                    throw new TimeoutException("Timed out waiting for image.");
                }
                image = imageReader.acquireLatestImage();
            } while (image == null);

            try {
                Image.Plane[] planes = image.getPlanes();
                ByteBuffer buffer = planes[0].getBuffer();
                int width = image.getWidth();
                int height = image.getHeight();

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

                int pixelStride = planes[0].getPixelStride();
                int rowStride = planes[0].getRowStride();
                int rowPadding = rowStride - pixelStride * width;

                Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
                bitmap.copyPixelsFromBuffer(buffer);

                Bitmap resultBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

                // Convert Bitmap to Base64
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                resultBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] byteArray = baos.toByteArray();
                String base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);


                resultBitmap.recycle();

                // Output JSON
                System.out.println(base64Image.replace("\n","")); // Indented output

            } finally {
                image.close();
            }

        Log.i("AccessibilityNodeDumper", "Fetch time: " + (System.currentTimeMillis() - startTime) + "ms");

        } finally {
            SurfaceControl.destroyDisplay(iBinder);
            surface.release();
        }
    }

    private static DisplayInfo getDisplayInfo() {
        IDisplayManager clipboard = IDisplayManager.Stub.asInterface(android.os.ServiceManager.getService(Context.DISPLAY_SERVICE));
        if (clipboard == null) {
            return null;
        }
        try {
            return clipboard.getDisplayInfo(Display.DEFAULT_DISPLAY);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}