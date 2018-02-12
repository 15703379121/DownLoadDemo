package com.download.radish.downloaddemo.util;


import com.download.radish.downloaddemo.listener.OnDownloadListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/2/10 0010.
 */

public class OkHttpUtil {
    private static OkHttpClient mClient;
    private boolean isCanceled = false;
    private boolean isPaused = false;

    static {
        if (mClient == null) {
            mClient = new OkHttpClient.Builder()
                    .build();
        }
    }

    private int mPrePosition;

    public long getContentLength(String downloadUrl) throws IOException {
        Request request =  new Request.Builder().url(downloadUrl).build();
        Response response = mClient.newCall(request).execute();
        if(response != null && response.isSuccessful()){
            long contentLength = response.body().contentLength();
            response.body().close();
            return contentLength;
        }
        return 0;
    }


    /**
     * 实现断点续传且带进度条
     * @param downloadUrl  //url地址
     * @param filePath  //文件存储位置
     * @throws IOException
     */
    public void download(final String downloadUrl,final String filePath, final OnDownloadListener downloadListener){

        new Thread(){
            @Override
            public void run() {
                try {
                    long downloadedLength = 0;
                    String directory = FileUtil.getDirectoryNameByPath(filePath);
                    File dirFile = new File(directory);
                    if (!dirFile.exists()){
                        dirFile.mkdirs();
                    }
                    File file = new File(filePath);
                    if(file.exists()){
                        downloadedLength = file.length();
                    }
                    long contentLength = 0;
                    contentLength = getContentLength(downloadUrl);
                    if(contentLength == 0){
                        if (downloadListener != null){
                            downloadListener.fail();
                        }
                    }else if(contentLength == downloadedLength){
                        if (downloadListener != null){
                            downloadListener.success();
                        }
                    }
                    Request request = new Request.Builder()
                            .addHeader("RANGE","bytes=" + downloadedLength + "-")//断点下载，指定从哪个字节开始下载
                            .url(downloadUrl)
                            .build();
                    Response response = mClient.newCall(request).execute();
                    if(response != null) {
                        InputStream is = response.body().byteStream();
                        RandomAccessFile saveFile = new RandomAccessFile(file, "rw");
                        saveFile.seek(downloadedLength);//跳过已下载的字节
                        byte[] b = new byte[1024];
                        int total = 0;
                        int len;
                        while ((len = is.read(b)) != -1) {
                            if (isCanceled) {
                                if (downloadListener != null){
                                    downloadListener.canceled();
                                    break;
                                }
                            } else if (isPaused) {
                                if (downloadListener != null){
                                    downloadListener.paused();
                                    break;
                                }
                            } else {
                                total += len;
                                saveFile.write(b, 0, len);
                                int progress = (int) ((total + downloadedLength) * 100 / contentLength);
                                if (downloadListener != null && progress != mPrePosition){
                                    mPrePosition = progress;
                                    downloadListener.downloading(progress);
                                }
                            }
                        }
                        if (downloadListener != null && !isCanceled && !isPaused){
                            downloadListener.success();
                        }
                        response.body().close();
                    }
                } catch (Exception e) {
                    if (downloadListener != null){
                        downloadListener.fail();
                    }
                    e.printStackTrace();
                }

            }
        }.start();



    }

    public void setPaused(boolean isPaused){
        this.isPaused = isPaused;
    }
    public void setCanceled(boolean isCanceled){
        this.isCanceled = isCanceled;
    }
}
