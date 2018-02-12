package com.download.radish.downloaddemo.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2018/2/11 0011.
 */

public class AppConstants {


    /*************关于存储路径************************/
    public static final String SD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"JavaDemo/";//存储根目录
    public static final String FILE_DOWN = SD_ROOT+"FileDown/";//文件下载目录
    public static final String FILE_DOWN_VIDEO = FILE_DOWN+"FileVideo";//文件下载目录
    public static final String FILE_DOWN_VIDEO_TEMP = FILE_DOWN_VIDEO+"/Temp";//文件下载目录
    public static final String FILE_DOWN_VIDEO_THUMBNAIL = FILE_DOWN_VIDEO+"/Thumbnail";//文件下载目录

    /*************关于视频URL************************/
    public static final String VIDEO_URL_PATH = "http://192.168.43.248:8080/javademo/upload/video/";//文件下载目录

    /*************关于SP************************/
    public static final String VIDEO_DOWNLOAD_LIST = "video_download_list";//文件下载目录
}
