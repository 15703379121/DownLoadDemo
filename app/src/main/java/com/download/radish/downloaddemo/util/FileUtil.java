package com.download.radish.downloaddemo.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by Administrator on 2018/2/11 0011.
 */

public class FileUtil {
    /**
     * 获取文件名
     */
    public static String getName(String fileName){
        int position = fileName.lastIndexOf(".");
        return fileName.substring(0,position);
    }
    /**
     * 获取文件后缀名
     */
    public static String getSuffix(String fileName){
        int position = fileName.lastIndexOf(".");
        return fileName.substring(position);
    }

    /**
     *
     * @param oldPath String 原文件路径
     * @param newPath String 剪切后路径
     * @return boolean
     */
    public static void cutFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                oldfile.delete();
                inStream.close();
            }
        }
        catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
    }
    /**
     * 获取文件名
     */
    public static String getFileNameByPath(String filePath){
        int position = filePath.lastIndexOf("/");
        return filePath.substring(position+1);
    }
    /**
     * 获取文件路径
     */
    public static String getDirectoryNameByPath(String filePath){
        int position = filePath.lastIndexOf("/");
        return filePath.substring(0,position);
    }
}

