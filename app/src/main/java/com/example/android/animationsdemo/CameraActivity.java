/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.animationsdemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.android.animationsdemo.camera.CameraHelper;
import com.example.android.animationsdemo.camera.CameraPreview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CameraActivity extends FragmentActivity implements View.OnClickListener{
    public static final String TAG = "CameraActivity";
    private Context context = CameraActivity.this;
    private Camera mCamera;
    private CameraPreview mPreview;
    private View focusView;
    private ImageView flashSwitchImageView;
    private ImageView cameraSwitchImageView;
    private ImageView previewImageView;
    private Button captureButton;
    private Handler handler = new Handler();
    private Bundle bundle = null;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DKLog.d(TAG, Trace.getCurrentMethod());
        // 2.setView
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_camera);

        initViews();
    }

    private void initViews(){
        // find view
        focusView = findViewById(R.id.focus_index);
        flashSwitchImageView = (ImageView) findViewById(R.id.flash_switch);
        cameraSwitchImageView = (ImageView) findViewById(R.id.camera_switch);
        previewImageView = (ImageView) findViewById(R.id.picture_preview);
        captureButton = (Button) findViewById(R.id.button_capture);

        // set listener
        flashSwitchImageView.setOnClickListener(this);
        cameraSwitchImageView.setOnClickListener(this);
        captureButton.setOnClickListener(this);
        previewImageView.setOnClickListener(this);

        // set status
        previewImageView.setVisibility(View.GONE);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.flash_switch:

                break;
            case R.id.camera_switch:
                switchCamera();
                break;
            case R.id.button_capture:
                takePicture();
                break;
            case R.id.picture_preview:
                previewImageView.setVisibility(View.GONE);
                break;
        }
    }

    //切换前后置摄像头
    private void switchCamera() {
        try {
            if(mCamera != null){
                // stop
                mCamera.stopPreview();
                releaseCamera();

                // get new camera
                mCamera = CameraHelper.getSwitchCamera();
                if(mCamera != null){
                    // bind holder and re-start
                    mCamera.setPreviewDisplay(mPreview.getHolder());
                    CameraHelper.setCameraDisplayOrientation(CameraActivity.this,
                            CameraHelper.getCurrectCameraID(),
                            mCamera);
                    mCamera.startPreview();
                }
            }
        } catch (Exception e) {
            DKLog.e(TAG, Trace.getCurrentMethod() + e.toString());
        }
    }

    private void initCamera(){
        if(CameraHelper.checkCameraHardware(context)) {
            long startTime = System.currentTimeMillis();
            // Create an instance of Camera
            mCamera = CameraHelper.getDefaultCameraInstance();

            // Create our Preview view and set it as the content of our activity.
            mPreview = new CameraPreview(this, mCamera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            mPreview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    DKLog.d(TAG, Trace.getCurrentMethod() + event.toString());
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            int x = (int) event.getX();
                            int y = (int) event.getY();
                            DKLog.d(TAG, Trace.getCurrentMethod() +
                                    String.format("( %d , %d )", x, y));
                            startFocusAniamtion(x, y);
                            startFocus(x, y);
                            return true;
                    }
                    return false;
                }
            });
            preview.addView(mPreview);

            DKLog.d(TAG, Trace.getCurrentMethod() + "take: "
                    + (System.currentTimeMillis() - startTime) + " ms");
        }
    }

    private void startFocusAniamtion(int pointX, int pointY){
        RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(focusView.getLayoutParams());
        layout.setMargins(pointX - 60, pointY - 60, 0, 0);
        focusView.setLayoutParams(layout);
        focusView.setVisibility(View.VISIBLE);
        ScaleAnimation sa = new ScaleAnimation(3f, 1f, 3f, 1f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(800);
        focusView.startAnimation(sa);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                focusView.setVisibility(View.INVISIBLE);
            }
        }, 800);
    }

    private void startFocus(int x, int y){
        if (mCamera != null) {
            mCamera.cancelAutoFocus();
            Rect focusRect = calculateTapArea(x, y);

            Camera.Parameters parameters = mCamera.getParameters();
            if (parameters.getFocusMode() != Camera.Parameters.FOCUS_MODE_AUTO) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
            if (parameters.getMaxNumFocusAreas() > 0) {
                List<Camera.Area> mylist = new ArrayList<Camera.Area>();
                mylist.add(new Camera.Area(focusRect, 1000));
                parameters.setFocusAreas(mylist);
            }

            try {
                mCamera.cancelAutoFocus();
                mCamera.setParameters(parameters);
                mCamera.startPreview();
                mCamera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (camera.getParameters().getFocusMode() != Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) {
                            Camera.Parameters parameters = camera.getParameters();
                            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                            if (parameters.getMaxNumFocusAreas() > 0) {
                                parameters.setFocusAreas(null);
                            }
                            camera.setParameters(parameters);
                            camera.startPreview();
                        }
                    }
                });
            } catch (Exception e) {
                DKLog.e(TAG, Trace.getCurrentMethod() + e.toString());
            }
        }
    }

    /**
     * Convert touch position x:y to {@link android.hardware.Camera.Area} position -1000:-1000 to 1000:1000.
     */
    private Rect calculateTapArea(float x, float y) {

        // define focus area (36dp)
        Display display = ((WindowManager)getSystemService(Activity.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int areaSize = Float.valueOf(36 * metrics.density).intValue();
        DKLog.d(TAG, String.format("x=%f, y=%f, areaSize=%d", x, y, areaSize));

        int width = mPreview.getWidth();
        int height = mPreview.getHeight();

        // Convert touch area to new area -1000:-1000 to 1000:1000.
        float left   = ((x - areaSize) / width)  * 2000 - 1000;
        float top    = ((y - areaSize) / height) * 2000 - 1000;
        float right  = ((x + areaSize) / width)  * 2000 - 1000;
        float bottom = ((y + areaSize) / height) * 2000 - 1000;

        // adjust boundary
        if(left < -1000){
            right += ((-1000) - left);
            left = (-1000);
        }
        if(top < -1000){
            bottom += ((-1000) - top);
            top = (-1000);
        }
        if(right > 1000){
            left -= (right - 1000);
            right = 1000;
        }
        if(bottom > 1000){
            top -= (bottom - 1000);
            bottom = 1000;
        }

        // rotate matrix if portrait
        RectF rectF = new RectF(left, top, right, bottom);
        Matrix matrix = new Matrix();
        int degree = (display.getRotation()== Surface.ROTATION_0) ? (-90) : (0);
        matrix.setRotate(degree);
        matrix.mapRect(rectF);
        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

    private void takePicture(){
        try {
            if(mCamera != null) {
                // get an image from the camera
                mCamera.takePicture(null, null, pictureCallback);
            }
        } catch (Exception e) {
            Toast.makeText(context, "Take picture failed....", Toast.LENGTH_SHORT).show();
            DKLog.e(TAG, Trace.getCurrentMethod() + e.toString());
        } finally {
            try {
                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




    private void releaseCamera(){
        if (mCamera != null){
            mCamera.setPreviewCallback(null);
            mPreview.getHolder().removeCallback(mPreview);
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        DKLog.d(TAG, Trace.getCurrentMethod() +
                String.format("requestCode: %s, resultCode: %s",
                        requestCode, resultCode));
        super.onActivityResult(requestCode, resultCode, data);
    }

    public  Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            DKLog.d(TAG, Trace.getCurrentMethod());
            // save pic byte array for transitive
            bundle = new Bundle();
            bundle.putByteArray("bytes", data);
            new AsyncSavePicTask(data).execute();

        }
    };

    private class AsyncSavePicTask extends AsyncTask<Void, Void, Void> {
        private byte[] data;
        private ProgressDialog mProgressDialog;
        private File pictureFile;
        protected void onPreExecute() {
            //showProgressDialog("saving...");
            mProgressDialog = ProgressDialog.show(context, "ProgressDialog", "saving...");
        }

        AsyncSavePicTask(byte[] data) {
            this.data = data;
        }

        @Override
        protected Void doInBackground(Void... params) {
            pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            DKLog.d(TAG, Trace.getCurrentMethod() + pictureFile.getAbsolutePath());
            if (pictureFile == null){
                DKLog.e(TAG, "Error creating media file, check storage permissions: ");
                return null;
            }
            try {
                // start Rotation
                int orientation;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 6;
                options.inDither = false; // Disable Dithering mode
                options.inPurgeable = true; // Tell to gc that whether it needs free
                // memory, the Bitmap can be cleared
                options.inInputShareable = true; // Which kind of reference will be
                // used to recover the Bitmap
                // data after being clear, when
                // it will be used in the future
                options.inTempStorage = new byte[32 * 1024];
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                Bitmap bMap = BitmapFactory.decodeByteArray(data, 0, data.length, options);


                if(CameraHelper.getCurrectCameraID() == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    // others devices
                    if(bMap.getHeight() < bMap.getWidth()){
                        orientation = 90;
                    } else {
                        orientation = 0;
                    }
                } else {
                    orientation = 270;
                }


                Bitmap bMapRotate;
                if (orientation != 0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(orientation);
                    bMapRotate = Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(),
                            bMap.getHeight(), matrix, true);
                } else {
                    bMapRotate = Bitmap.createScaledBitmap(bMap, bMap.getWidth(),
                            bMap.getHeight(), true);
                }

                FileOutputStream fos = new FileOutputStream(pictureFile);
                bMapRotate.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                if (bMapRotate != null) {
                    bMapRotate.recycle();
                    bMapRotate = null;
                }

                // normal save
//                fos.write(data);
//                DKLog.d(TAG, Trace.getCurrentMethod() + "save " + data.length + " bytes");
//                fos.flush();
//                fos.close();
            } catch (FileNotFoundException e) {
                DKLog.e(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                DKLog.e(TAG, "Error accessing file: " + e.getMessage());
            } catch (Exception e) {
                DKLog.e(TAG, e.getMessage());
            }

            return null;
        }

        private void galleryAddPic(File f) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            sendBroadcast(mediaScanIntent);
        }

        private void setPic(File f) {
            // show view
            previewImageView.setVisibility(View.VISIBLE);

            // Get the dimensions of the View
            int targetW = previewImageView.getWidth();
            int targetH = previewImageView.getHeight();

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(f.getAbsolutePath(), bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            //int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            //bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bmOptions);
            previewImageView.setImageBitmap(bitmap);

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mProgressDialog.dismiss();
            galleryAddPic(pictureFile);
            setPic(pictureFile);
        }
    }

    /** Create a File for saving an image or video */
    private File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "GamaniaApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                DKLog.d(TAG, "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }


}
