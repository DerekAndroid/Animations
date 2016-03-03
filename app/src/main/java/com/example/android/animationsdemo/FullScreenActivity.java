package com.example.android.animationsdemo;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import java.io.IOException;

/**
 * Created by derekchang on 2015/8/17.
 */
public class FullScreenActivity extends Activity {
    public static final String TAG = "FullScreenActivity";
    private SurfaceView mSurfaceView;
    private MediaPlayer mMediaPlayer;
    private SurfaceHolder mSurfaceHolder;
    private Intent intent;
    String filePath;
    int playPos = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fullscreen);
        intent = getIntent();
        mSurfaceView    = (SurfaceView) this.findViewById(R.id.fullscreen_surfaceView);
        mSurfaceHolder  = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(mSurfaceHolderCallback);

        filePath    = intent.getStringExtra("FILE_PATH");
        DKLog.d(TAG, Trace.getCurrentMethod() + "FILE_PATH: " + filePath);
        playPos     = intent.getIntExtra("PLAY_POS", 0);
        DKLog.d(TAG, Trace.getCurrentMethod() + "PLAY_POS: " + playPos);
    }


    SurfaceHolder.Callback mSurfaceHolderCallback = new SurfaceHolder.Callback(){
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            DKLog.d(TAG, Trace.getCurrentMethod());
            initMediaPlayer(filePath);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            DKLog.d(TAG, Trace.getCurrentMethod());
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            DKLog.d(TAG, Trace.getCurrentMethod());
            if(mMediaPlayer != null){
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }
    };

    public synchronized void initMediaPlayer(String filepath){
        try {
            DKLog.d(TAG, Trace.getCurrentMethod());
            if(mMediaPlayer == null){
                long startTime = System.currentTimeMillis();
                //http://stackoverflow.com/questions/7588584/android-asynctask-check-status
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setDataSource(filepath);
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
            DKLog.e(TAG, Trace.getCurrentMethod() + e.toString());
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener(){
        @Override
        public void onPrepared(MediaPlayer mp) {
            DKLog.d(TAG, Trace.getCurrentMethod() + playPos);
            mMediaPlayer.seekTo(playPos);
            mMediaPlayer.start();
        }
    };
}
