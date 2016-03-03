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
import android.app.Fragment;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy title indicating
 * the page number, along with some dummy text.
 *
 * <p>This class is used by the {@link CardFlipActivity} and {@link
 * ScreenSlideActivity} samples.</p>
 */
public class ScreenSlidePageFragment extends Fragment implements View.OnClickListener{
    public static final String TAG = "ScreenSlidePageFragment";
    /**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";
    public static final String ARG_PATH = "path";
    private int         mPageNumber;
    private String      mVideoPath;
    private VideoView   mVideoView;
    private SurfaceView mSurfaceView;
    private MediaPlayer mMediaPlayer;
    private SurfaceHolder mSurfaceHolder;
    private ProgressBar mProgressBar;
    private TextView    mTextView;
    private FrameLayout mPreview;
    private ImageView   mFullScreen;
    AsyncSetLoadVideoTask mAsyncSetLoadVideoTask;
    private boolean     isFocus = false;


    public static ScreenSlidePageFragment create(int pageNumber, String path) {
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        args.putString(ARG_PATH, path);
        fragment.setArguments(args);
        return fragment;
    }

    public static ScreenSlidePageFragment create(int pageNumber) {
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        args.putString(ARG_PATH, "");
        fragment.setArguments(args);
        return fragment;
    }

    public ScreenSlidePageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
        mVideoPath  = getArguments().getString(ARG_PATH);
        DKLog.d(TAG, Trace.getCurrentMethod() + mPageNumber);
        DKLog.d(TAG, Trace.getCurrentMethod() + mVideoPath);
        //DownloadService.downloadVideo(getActivity(), mVideoPath, new DownloadReceiver(new Handler()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        ViewGroup rootView = null;
/*
        if(mPageNumber == 0) {
            // Inflate the layout containing a title and body text.
            rootView = (ViewGroup) inflater
                    .inflate(R.layout.fragment_screen_slide_page, container, false);

            // Set the title view to show the page number.
            ((TextView) rootView.findViewById(android.R.id.text1)).setText(
                    getString(R.string.title_template_step, mPageNumber + 1));
        }else
*/
        if(mPageNumber < 2){
            rootView = (ViewGroup) inflater
                    .inflate(R.layout.fragment_screen_slide_page_surface, container, false);
            mSurfaceView    = (SurfaceView) rootView.findViewById(R.id.banner_sruface);
            mTextView       = (TextView) rootView.findViewById(R.id.video_info);
            mProgressBar    = (ProgressBar) rootView.findViewById(R.id.video_progress);
            mProgressBar.setVisibility(View.GONE);
            mPreview        = (FrameLayout) rootView.findViewById(R.id.video_preview);
            mFullScreen     = (ImageView) rootView.findViewById(R.id.fullscreen_imageview);
            mFullScreen.setOnClickListener(this);
            mSurfaceHolder  = mSurfaceView.getHolder();
            mSurfaceHolder.addCallback(mSurfaceHolderCallback);
            // Download video
            mAsyncSetLoadVideoTask = new AsyncSetLoadVideoTask(mVideoPath);
            mAsyncSetLoadVideoTask.execute();
        } else if(mPageNumber == 8){
            // VIDEO VIEW
            // Inflate the layout containing a title and body text.
//            rootView = (ViewGroup) inflater
//                    .inflate(R.layout.fragment_screen_slide_page_video, container, false);
//            mVideoView = (VideoView) rootView.findViewById(R.id.banner_video);
//            mVideoView.setVideoURI(Uri.parse(VIDEO_PATH));
//            MediaController mMediaController = new MediaController(getActivity());
//            mMediaController.setAnchorView(mVideoView);
//            mVideoView.setMediaController(mMediaController);
//            mVideoView.requestFocus();
//            //mVideoView.start();
//            mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mp) {
//                    mp.seekTo(0);
//                    mp.start();
//                }
//            });
//            rootView.setOnClickListener(new View.OnClickListener(){
//                @Override
//                public void onClick(View v) {
//                    mVideoView.seekTo(0);
//                    mVideoView.start();
//                }
//            });
        } else {
            // Inflate the layout containing a title and body text.
            rootView = (ViewGroup) inflater
                    .inflate(R.layout.fragment_screen_slide_page, container, false);

            // Set the title view to show the page number.
            ((TextView) rootView.findViewById(android.R.id.text1)).setText(
                    getString(R.string.title_template_step, mPageNumber + 1));
        }
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }



    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }

    public String getVideoPath() {
        return mVideoPath;
    }

    private class AsyncSetLoadVideoTask extends AsyncTask<Void,Integer,Void> {
        String          videoPath = "";
        InputStream     input = null;
        OutputStream    output = null;
        URLConnection   connection;
        URL             url;
        int             fileLength;
        String          filepath = "";
        File file;
        public AsyncSetLoadVideoTask(String videoPath){
            DKLog.d(TAG, Trace.getCurrentMethod());

            try {
                this.videoPath = videoPath;
                // init view
                mTextView.setText(videoPath);
                mProgressBar.setProgress(0);
                mPreview.setVisibility(View.VISIBLE);
                // init file
                url = new URL(videoPath);
                String fileLink = url.toString();
                String fileName = fileLink.substring(fileLink.lastIndexOf('/') + 1, fileLink.length());
                filepath = DownloadService.getBanerVideoFolder(getActivity()) + File.separator + fileName;
            } catch (MalformedURLException e) {
                DKLog.e(TAG, Trace.getCurrentMethod() + e.toString());
            }
        }
        protected Void doInBackground(Void... params) {
            DKLog.i(TAG, Trace.getCurrentMethod());
            try {
                DKLog.d(TAG, Trace.getCurrentMethod() + "Ready to download URL: " + url);
                file = new File(filepath);
                if (!file.exists()) {
                    connection = url.openConnection();
                    connection.connect();
                    // this will be useful so that you can show a typical 0-100% progress bar
                    fileLength = connection.getContentLength();

                    // download the file
                    input = new BufferedInputStream(connection.getInputStream());

                    DKLog.d(TAG, Trace.getCurrentMethod() + "Save to: " + filepath);
                    DKLog.d(TAG, Trace.getCurrentMethod() + "FileLength: " + fileLength);
                    createBannerVideoFolder();

                    output = new FileOutputStream(filepath);
                    byte data[] = new byte[1024];
                    int total = 0;
                    int count;
                    int progress = 0;
                    int lastPushPregoress = 0;

                    // Escape early if cancel() is called
                    while ((count = input.read(data)) != -1 && !isCancelled()) {
                        total += count;
                        // publishing the progress....
                        if (fileLength > 0) {
                            progress = total * 100 / fileLength;
                            if (progress != lastPushPregoress) {
                                //DKLog.d(TAG, Trace.getCurrentMethod());
                                publishProgress(progress);
                            }
                            lastPushPregoress = progress;
                        } else {
                            //can't get file length from server
                            //DKLog.d(TAG, Trace.getCurrentMethod());
                            publishProgress(total);
                        }
                        output.write(data, 0, count);

                    }
                    close();
                }else{
                    DKLog.w(TAG, Trace.getCurrentMethod() +
                            "Page: " + mPageNumber + " File exists!" + file.getAbsolutePath());
                }
            }catch(Exception e){
                DKLog.e(TAG, Trace.getCurrentMethod() + "Page: " + mPageNumber + " : " + e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            DKLog.e(TAG, Trace.getCurrentMethod() + "FINISH: " + url);
            mPreview.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    initMediaPlayer();
                }
            },500);

        }

        @Override
        protected void onCancelled(Void aVoid) {
            DKLog.i(TAG, Trace.getCurrentMethod());
            super.onCancelled(aVoid);
        }

        @Override
        protected void onCancelled() {
            DKLog.i(TAG, Trace.getCurrentMethod());
            super.onCancelled();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            //DKLog.d(TAG, Trace.getCurrentMethod() + progress[0]);
            if (fileLength > 100) {
                mProgressBar.setMax(fileLength);
            } else if(fileLength < 0){
                mProgressBar.setMax(1024 * 1024);
            } else {
                mProgressBar.setMax(100);
            }

            mProgressBar.setProgress(progress[0]);
            mTextView.setText(String.valueOf(progress[0]));
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        private void close(){
            try {
                if(output != null) {
                    output.flush();
                    output.close();
                }
                if(input != null) {
                    input.close();
                }
            } catch (Exception e) {
                DKLog.e(TAG, Trace.getCurrentMethod() + e.toString());
            }
        }
    }


    SurfaceHolder.Callback mSurfaceHolderCallback = new SurfaceHolder.Callback(){
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            DKLog.d(TAG, Trace.getCurrentMethod() + mPageNumber);
            initMediaPlayer();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            DKLog.d(TAG, Trace.getCurrentMethod() + mPageNumber);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            DKLog.d(TAG, Trace.getCurrentMethod() + mPageNumber);
            if(mMediaPlayer != null){
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }
    };

    MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener(){
        @Override
        public void onPrepared(MediaPlayer mp) {
            DKLog.d(TAG, Trace.getCurrentMethod() + mPageNumber);
            if(isFocus)mMediaPlayer.start();
        }
    };

    public synchronized void initMediaPlayer(){
        try {
            DKLog.d(TAG, Trace.getCurrentMethod() + mPageNumber);
            if(mMediaPlayer == null){
                long startTime = System.currentTimeMillis();
                //http://stackoverflow.com/questions/7588584/android-asynctask-check-status
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setDataSource(mAsyncSetLoadVideoTask.filepath);
                mMediaPlayer.setDisplay(mSurfaceHolder);
                mMediaPlayer.prepareAsync();
                mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
                mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        DKLog.e(TAG, Trace.getCurrentMethod() + what);
                        return false;
                    }
                });
                //mMediaPlayer.setWakeMode(getActivity(), PowerManager.PARTIAL_WAKE_LOCK);
                DKLog.d(TAG, Trace.getCurrentMethod() + (System.currentTimeMillis() - startTime) + " ms");
            }
        } catch (IOException e) {
            DKLog.e(TAG, Trace.getCurrentMethod() + e.toString() + mPageNumber);
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void isFocus(boolean focus){
        DKLog.d(TAG,Trace.getCurrentMethod() + mPageNumber + " : " + focus);
        isFocus = focus;
        if(mMediaPlayer == null) return;
        if(isFocus){
            DKLog.w(TAG, Trace.getCurrentMethod() + "Start: " + mPageNumber);
            mMediaPlayer.start();
        }else{
            DKLog.e(TAG, Trace.getCurrentMethod() + "Pause: " + mPageNumber);
            mMediaPlayer.pause();
        }
    }


    @Override
    public void onDestroy() {
        DKLog.d(TAG, Trace.getCurrentMethod() + mPageNumber);
        if(mAsyncSetLoadVideoTask != null) {
            if (!mAsyncSetLoadVideoTask.isCancelled()) {
                mAsyncSetLoadVideoTask.cancel(true);
            }
        }
        if(mMediaPlayer != null){
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        super.onDestroy();
    }


    public void createBannerVideoFolder(){
        File folder = new File(getActivity().getCacheDir() + File.separator + DownloadService.FILE_FOLDER);
        if(!folder.exists() && !folder.isDirectory()) {
            folder.mkdir();
        }
    }

    private class DownloadReceiver extends ResultReceiver {
        public DownloadReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == DownloadService.UPDATE_PROGRESS) {
                int progress = resultData.getInt(DownloadService.PROGRESS);
                DKLog.w(TAG, Trace.getCurrentMethod() + progress);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fullscreen_imageview:
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString(
                        "FILE_PATH",
                        mAsyncSetLoadVideoTask.filepath);
                if(mMediaPlayer != null){
                    bundle.putInt(
                        "PLAY_POS",
                        mMediaPlayer.getCurrentPosition());
                }
                intent.putExtras(bundle);
                intent.setClass(getActivity(), FullScreenActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onPause() {
        DKLog.d(TAG, Trace.getCurrentMethod());
        if(mMediaPlayer != null) {
            mMediaPlayer.seekTo(0);
            mMediaPlayer.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        DKLog.d(TAG, Trace.getCurrentMethod());
        if(mMediaPlayer != null) {
            mMediaPlayer.start();
        }
        super.onResume();
    }

}
