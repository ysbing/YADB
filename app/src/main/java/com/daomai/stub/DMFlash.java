package com.daomai.stub;

import android.app.Activity;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.widget.Toast;

public class DMFlash extends Activity {

    private CameraManager cameraManager;
    private String cameraId;
    private boolean isFlashOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);

        try {
            // Lấy camera ID
            cameraId = cameraManager.getCameraIdList()[0]; // Sử dụng camera chính

            // Lấy giá trị tham số từ Intent
            Intent intent = getIntent();
            String flashMode = intent.getStringExtra("flash");

            if ("on".equals(flashMode)) {
                turnOnFlash();
                Toast.makeText(this, "Flashlight turned on.", Toast.LENGTH_SHORT).show();
            } else if ("off".equals(flashMode)) {
                turnOffFlash();
                Toast.makeText(this, "Flashlight turned off.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Invalid flash mode.", Toast.LENGTH_SHORT).show();
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to control flashlight.", Toast.LENGTH_SHORT).show();
        }
    }

    private void turnOnFlash() throws CameraAccessException {
        if (!isFlashOn) {
            cameraManager.setTorchMode(cameraId, true);
            isFlashOn = true;
        }
    }

    private void turnOffFlash() throws CameraAccessException {
        if (isFlashOn) {
            cameraManager.setTorchMode(cameraId, false);
            isFlashOn = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (isFlashOn) {
                turnOffFlash();
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}
