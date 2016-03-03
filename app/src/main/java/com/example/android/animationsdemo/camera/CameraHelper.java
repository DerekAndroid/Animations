package com.example.android.animationsdemo.camera;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.view.Surface;

import com.example.android.animationsdemo.DKLog;
import com.example.android.animationsdemo.Trace;

/**
 * Created by derekchang on 2016/2/26.
 */
public class CameraHelper {
    public static final String TAG = "CameraHelper";
    private static int mCurrentCameraId = -1;


    public static int getCurrectCameraID(){
        return mCurrentCameraId;
    }

    /**
     * get back-end > front-end
     * @return camera id
     */
    private static int getDefaultOpenCameraID(){
        int numberOfCameras = Camera.getNumberOfCameras();

        // no camera
        if(numberOfCameras == 0) return mCurrentCameraId = -1;

        // get back-end camera first
        for (int i = 0 ; i < numberOfCameras ; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                return mCurrentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
            }
        }

        // get front-end camera second
        return mCurrentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;

    }

    public static Camera getSwitchCamera(){
        mCurrentCameraId = (mCurrentCameraId + 1) % Camera.getNumberOfCameras();
        return getCameraInstance(mCurrentCameraId);
    }

    /** Check if this device has a camera */
    public static boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getDefaultCameraInstance(){
        return getCameraInstance(getDefaultOpenCameraID());
    }

    public static Camera getCameraInstance(int cameraId){
        Camera c = null;
        try {
            c = Camera.open(cameraId); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            DKLog.e(TAG, Trace.getCurrentMethod() + e.toString());
        }
        return c; // returns null if camera is unavailable
    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        DKLog.d(TAG, Trace.getCurrentMethod() + "Rotate: " + result + " degrees");
        camera.setDisplayOrientation(result);
    }
}
