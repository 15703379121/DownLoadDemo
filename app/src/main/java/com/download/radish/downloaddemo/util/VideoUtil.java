package com.download.radish.downloaddemo.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;

/**
 * Created by Administrator on 2018/2/6 0006.
 */

public class VideoUtil {

    /**
     * vitamio设置缩略图
    public static void setThumbnail(final Context context, final String url, final ImageView mIvThumbnail) {
        new Thread() {
            @Override
            public void run() {
                //设置缩略图,Vitamio提供的工具类。
                final Bitmap videoThumbnail = ThumbnailUtils.createVideoThumbnail(
                        context, url
                        , MediaStore.Video.Thumbnails.MINI_KIND);
                LogUtil.e("videoThumbnail--------"+videoThumbnail);
                if (videoThumbnail != null) {
                    mIvThumbnail.post(new Runnable() {
                        @Override
                        public void run() {
                            mIvThumbnail.setImageBitmap(videoThumbnail);
                        }
                    });
                }
            }
        }.start();
    }*/

    /**
     * 获取本地视频缩略图
     * @param videoPath
     * @return
     */
    public static Bitmap getVideoThumbnail(String videoPath) {
        try{
            MediaMetadataRetriever media =new MediaMetadataRetriever();
//        media.setDataSource(videoPath);
            media.setDataSource(videoPath);
            Bitmap bitmap = media.getFrameAtTime();
            return bitmap;
        }catch (Exception e){

        }
        return null;
    }

    /**
     * 获取本地图片
     * @param picPath
     * @return
     */
    public static Bitmap getLocalPic(String picPath){
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(picPath);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean savePic(Bitmap btImage,String filePath,String fileName){
        if (Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED)) // 判断是否可以对SDcard进行操作
        {
            File dirFile  = new File(filePath);  //目录转化成文件夹
            if (!dirFile.exists()) {              //如果不存在，那就建立这个文件夹
                dirFile.mkdirs();
            }                          //文件夹有啦，就可以保存图片啦
            File file = new File(filePath, fileName+".png");// 在SDcard的目录下创建图片文,以当前时间为其命名

            try {
                FileOutputStream out = new FileOutputStream(file);
                btImage.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     *
     * @return
     */
    public static boolean saveNetVideoThumbnail(String url,String name) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        int kind = MediaStore.Video.Thumbnails.MINI_KIND;
        try {
            if (Build.VERSION.SDK_INT >= 14) {
                retriever.setDataSource(url, new HashMap<String, String>());
            } else {
                retriever.setDataSource(url);
            }
            bitmap = retriever.getFrameAtTime();
        } catch (Exception ex) {
            // Assume this is a corrupt video file
            return false;
        }
        if (kind == MediaStore.Images.Thumbnails.MICRO_KIND && bitmap != null) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, 90, 90,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        if (savePic(bitmap, AppConstants.FILE_DOWN_VIDEO_THUMBNAIL,name)){
            return true;
        }
        return false;
    }
    /*public static boolean saveNetVideoThumbnail(String url,String name,int kind){
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            if (url.startsWith("http://")
                    || url.startsWith("https://")
                    || url.startsWith("widevine://")) {
                retriever.setDataSource(url,new Hashtable<String,String>());
            }else {
                retriever.setDataSource(url);
            }
            bitmap =retriever.getFrameAtTime(-1);
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
            ex.printStackTrace();
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
            ex.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
                ex.printStackTrace();
            }
        }

        if (bitmap==null)return false;

        if (kind== MediaStore.Images.Thumbnails.MINI_KIND) {
            //512×384
            // Scale down the bitmap if it's too large.
            int width= bitmap.getWidth();
            int height= bitmap.getHeight();
            int max =Math.max(width, height);
            if(max >512) {
                float scale=512f / max;
                int w =Math.round(scale * width);
                int h =Math.round(scale * height);
                bitmap = Bitmap.createScaledBitmap(bitmap,w, h, true);
            }
        } else if (kind== MediaStore.Images.Thumbnails.MICRO_KIND) {
            //96×96
            bitmap = ThumbnailUtils.extractThumbnail(bitmap,
                    96,
                    96,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        if (savePic(bitmap, AppConstants.FILE_DOWN_VIDEO_THUMBNAIL,name)){
            return true;
        }
        return false;
    }*/
}
