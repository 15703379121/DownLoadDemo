package com.download.radish.downloaddemo.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.download.radish.downloaddemo.util.AppConstants;
import com.download.radish.downloaddemo.bean.DownLoadBean;
import com.download.radish.downloaddemo.service.DownloadService;
import com.download.radish.downloaddemo.R;
import com.download.radish.downloaddemo.util.FileUtil;
import com.download.radish.downloaddemo.util.SPUtil;
import com.download.radish.downloaddemo.util.ServiceUtil;
import com.download.radish.downloaddemo.util.VideoUtil;

import java.io.File;

/**
 * 访视视频播放器下载模式
 * 首页点击下载，实现开启并绑定服务
 * 下载方式：使用Okhttp3断点续传并带进度条
 */
public class MainActivity extends Activity implements View.OnClickListener{

    private DownloadService.DownloadBinder downloadBinder;

    //联接服务
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadService.DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //开启并绑定服务
        initService();
       /* SPUtil.deleteSp(this);
        String[] strs= {"1517830079704.mp4","1517830310836.mp4","1517836207390.mp4","progress.mp4"};
        for (int i = 0; i < strs.length; i++) {
            String url = "http://192.168.43.248:8080/javademo/upload/video/"+strs[i];
            //保存要下载的文件URL
            String fileName = FileUtil.getFileNameByPath(url);
            String name = FileUtil.getName(fileName);
            String suffix = FileUtil.getSuffix(fileName);
            DownLoadBean downLoadBean = new DownLoadBean(name, suffix, url,AppConstants.FILE_DOWN_VIDEO_TEMP+"/"+fileName);
//            String json = GsonUtil.getGson().toJson(downLoadBean);
//            Log.e("radish","json----"+json);
            SPUtil.setVideoDownloadList(this,downLoadBean);
            //保存缩略图
            boolean flag = VideoUtil.saveNetVideoThumbnail(url, name, MediaStore.Images.Thumbnails.MICRO_KIND);
            Log.e("radish","保存网络视频缩略图"+flag);
        }*/

        //事件监听
        initEvent();
    }

    private void initEvent() {
        findViewById(R.id.btn_download).setOnClickListener(MainActivity.this);
        findViewById(R.id.btn_info).setOnClickListener(this);
    }

    private void initService() {
        //开始下载
        Intent intent = new Intent(MainActivity.this, DownloadService.class);
        if (!ServiceUtil.isServiceRunning(this,"com.download.radish.downloaddemo.service.DownloadService")){
            startService(intent);
        }else{
        }
        bindService(intent,connection,BIND_AUTO_CREATE);//绑定服务
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "拒绝权限将无法使用程序", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_download:
                String url = "http://192.168.43.248:8080/javademo/upload/video/apiRoad.mp4";
                String fileName = FileUtil.getFileNameByPath(url);
                String name = FileUtil.getName(fileName);
                String suffix = FileUtil.getSuffix(fileName);
                //查看文件是否已下载
                //已下载文件
                File loadedDir = new File(AppConstants.FILE_DOWN_VIDEO);
                String[] loadedFiles = loadedDir.list();
                if (loadedFiles != null && loadedFiles.length > 0){
                    //轮询查看
                    for (int i = 0; i < loadedFiles.length; i++) {
                        if (loadedFiles[i].equals(name+suffix)){
                            Toast.makeText(this, "此视频已下载", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
                //保存要下载的文件URL
                DownLoadBean downLoadBean = new DownLoadBean(name, suffix, url,AppConstants.FILE_DOWN_VIDEO_TEMP+"/"+fileName);
                SPUtil.setVideoDownloadList(this,downLoadBean);
                //保存缩略图
                boolean flag = VideoUtil.saveNetVideoThumbnail(url, name);
                //开始下载
                if (downloadBinder != null && !downloadBinder.isDownLoading()) {
                    downloadBinder.startDownload(downLoadBean);
                }
                break;
            case R.id.btn_info:
                //下载详情页面
                startActivity(new Intent(MainActivity.this,DownloadListActivity.class));
                break;
        }
    }
}
