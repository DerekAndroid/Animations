package com.example.android.animationsdemo;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by derekchang on 2015/8/13.
 */
public class DownloadService extends IntentService {
    private static final String TAG = "DownloadService";
    public static final int UPDATE_PROGRESS = 0x1000;
    private Context context = DownloadService.this;
    public static final String URL = "DownloadService.URL";
    public static final String RECEIVER = "DownloadService.RECEIVER";
    public static final String PROGRESS = "DownloadService.PROGRESS";
    public static final String STOP = "DownloadService.STOP";
    public static final String FILE_FOLDER = "BannerVideos";
    private boolean isStop = false;


    public DownloadService() {
        super("DownloadService");
    }

//    public File getDiskCacheDir(Context context, String uniqueName) {
//        String cachePath;
//        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
//                || !Environment.isExternalStorageRemovable()) {
//            cachePath = context.getExternalCacheDir().getPath();
//        } else {
//            cachePath = context.getCacheDir().getPath();
//        }
//        return new File(cachePath + File.separator + uniqueName);
//    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DKLog.d(TAG, Trace.getCurrentMethod());
        isStop = intent.getBooleanExtra(STOP, false);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DKLog.d(TAG, Trace.getCurrentMethod());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String urlToDownload = intent.getStringExtra(URL);
        ResultReceiver receiver = (ResultReceiver) intent.getParcelableExtra(RECEIVER);
        if(!isStop) {
            try {
                URL url = new URL(urlToDownload);
                DKLog.d(TAG, Trace.getCurrentMethod() + "Ready to download URL: " + url);

                URLConnection connection = url.openConnection();
                connection.connect();
                // this will be useful so that you can show a typical 0-100% progress bar
                int fileLength = connection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(connection.getInputStream());
                String fileLink = url.toString();
                String fileName = fileLink.substring(fileLink.lastIndexOf('/') + 1, fileLink.length());
                String path = getBanerVideoFolder(context) + File.separator + fileName;

                DKLog.d(TAG, Trace.getCurrentMethod() + "Save to: " + path);
                DKLog.d(TAG, Trace.getCurrentMethod() + "FileLength: " + fileLength);
                createBannerVideoFolder();

                File file = new File(path);
                if (!file.exists()) {
                    OutputStream output = new FileOutputStream(path);
                    byte data[] = new byte[1024];
                    int total = 0;
                    int count;
                    int progress = 0;
                    int lastPushPregoress = 0;
                    while ((count = input.read(data)) != -1) {
                        if(isStop) throw new Exception("Cancel download task: " + url);
                        total += count;
                        // publishing the progress....
                        Bundle resultData = new Bundle();
                        if (fileLength > 0) {
                            progress = total * 100 / fileLength;
                            if (progress != lastPushPregoress) {
                                resultData.putInt(PROGRESS, progress);
                                //DKLog.d(TAG, Trace.getCurrentMethod() + "Prgoress: " + progress);
                                receiver.send(UPDATE_PROGRESS, resultData);
                            }
                            lastPushPregoress = progress;
                        } else {
                            //can't get file length from server
                            resultData.putInt(PROGRESS, total);
                            //DKLog.d(TAG, Trace.getCurrentMethod() + "Prgoress: " + total);
                            receiver.send(UPDATE_PROGRESS, resultData);
                        }
                        output.write(data, 0, count);
                    }

                    output.flush();
                    output.close();
                    input.close();

                    // Download completed!
                    Bundle resultData = new Bundle();
                    resultData.putInt(PROGRESS, 0);
                    receiver.send(UPDATE_PROGRESS, resultData);
                } else {
                    DKLog.w(TAG, Trace.getCurrentMethod() + "File exists!");
                }


            } catch (Exception e) {
                DKLog.e(TAG, Trace.getCurrentMethod() + e.toString());
                Bundle resultData = new Bundle();
                resultData.putInt(PROGRESS, -1);
                receiver.send(UPDATE_PROGRESS, resultData);
            }

        }else{
            DKLog.e(TAG, Trace.getCurrentMethod() + "STOP DOWNLOAD: " + urlToDownload);
        }
    }

    public void createBannerVideoFolder(){
        File folder = new File(context.getCacheDir() + File.separator + DownloadService.FILE_FOLDER);
        if(!folder.exists() && !folder.isDirectory()) {
            folder.mkdir();
        }
    }

    public static void deleteBannerVideo(Context context) {
        File folder = new File(context.getCacheDir() + File.separator + DownloadService.FILE_FOLDER);
        DKLog.d(TAG, Trace.getCurrentMethod() + "Delete folder " + folder.getAbsolutePath());
        deleteBannerVideoFolder(folder);
    }

    private static void deleteBannerVideoFolder(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteBannerVideoFolder(child);

        fileOrDirectory.delete();

    }

    public static void downloadVideo(Context context, String videoPath, ResultReceiver mDownloadReceiver){
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(DownloadService.STOP, false);
        intent.putExtra(DownloadService.URL, videoPath);
        intent.putExtra(DownloadService.RECEIVER, mDownloadReceiver);
        context.startService(intent);
    }

    public static void stopDownloadvideo(Context context){
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(DownloadService.STOP, true);
        context.startService(intent);
    }

    public static String getBanerVideoFolder(Context context){
        return context.getCacheDir() + File.separator + DownloadService.FILE_FOLDER;
    }

//    private class DownloadReceiver extends ResultReceiver {
//        public DownloadReceiver(Handler handler) {
//            super(handler);
//        }
//
//        @Override
//        protected void onReceiveResult(int resultCode, Bundle resultData) {
//            super.onReceiveResult(resultCode, resultData);
//            if (resultCode == DownloadService.UPDATE_PROGRESS) {
//                int progress = resultData.getInt(DownloadService.PROGRESS);
//                DKLog.w(TAG, Trace.getCurrentMethod() + progress);
//            }
//        }
//    }
}