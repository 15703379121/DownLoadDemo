package com.download.radish.downloaddemo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.Toast;

import com.download.radish.downloaddemo.bean.DownLoadBean;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by hegeyang on 2017/3/25 0025 .
 */

public class SPUtil {
	private static final String SPNAME ="javaDemo" ;
	private static SharedPreferences sp;
	public static void putBoolean(String key, boolean value, Context context){
		if (sp==null) {
			sp = context.getSharedPreferences(SPNAME, Context.MODE_PRIVATE);
		}
		sp.edit().putBoolean(key,value).commit();
	}

	public static boolean getBoolean(String key, Context context){
		if (sp==null) {
			sp = context.getSharedPreferences(SPNAME, Context.MODE_PRIVATE);
		}
		boolean b = sp.getBoolean(key, false);
		return b;
	}


	//写入数据
	public static void putString(Context context, String key, String value) {
		if (sp == null) {
			sp = context.getSharedPreferences(SPNAME, context.MODE_PRIVATE);
		}
		sp.edit().putString(key, value).commit();
	}

	// 读取数据
	public static String getString(Context context, String key, String value) {
		if (sp == null) {
			sp = context.getSharedPreferences(SPNAME, context.MODE_PRIVATE);
		}
		return sp.getString(key, value);
	}

    //写入数据
    public static void putInt(Context context, String key, int value) {
        if (sp == null) {
            sp = context.getSharedPreferences(SPNAME, context.MODE_PRIVATE);
        }
        sp.edit().putInt(key, value).commit();
    }

    // 读取数据
    public static int getInt(Context context, String key, int value) {
        if (sp == null) {
            sp = context.getSharedPreferences(SPNAME, context.MODE_PRIVATE);
        }
        return sp.getInt(key, value);
    }

	public static void deleteSp(Context context){
		if (sp == null) {
			sp = context.getSharedPreferences(SPNAME, context.MODE_PRIVATE);
		}
		sp.edit().clear().commit();
	}



    public static List<DownLoadBean> getVideoDownloadList(Context context){
        if (sp == null) {
            sp = context.getSharedPreferences(SPNAME, context.MODE_PRIVATE);
        }
        String str = SPUtil.getString(context, AppConstants.VIDEO_DOWNLOAD_LIST, "");
        if(TextUtils.isEmpty(str)){
            return null;
        }
        return (List<DownLoadBean>) GsonUtil.parseJsonToList(str, new TypeToken<List<DownLoadBean>>() {
        }.getType());
    }

    public static void setVideoDownloadList(Context context,DownLoadBean bean){
        if (sp == null) {
            sp = context.getSharedPreferences(SPNAME, context.MODE_PRIVATE);
        }
        String str = SPUtil.getString(context, AppConstants.VIDEO_DOWNLOAD_LIST, "");
        if(TextUtils.isEmpty(str)){
            String json = GsonUtil.getGson().toJson(bean);
            sp.edit().putString(AppConstants.VIDEO_DOWNLOAD_LIST, "["+json+"]").commit();
            Toast.makeText(context, "添加到下载列表", Toast.LENGTH_SHORT).show();
        }else{
            List<DownLoadBean> videoDownloadList = getVideoDownloadList(context);
            int position = -1;
            for (int i = 0; i < videoDownloadList.size(); i++) {
                if (bean == null) {
                    if(videoDownloadList.get(i) == null){
                        //是这个
                        position = i;
                        break;
                    }
                }else if (bean.toString().equals(videoDownloadList.get(i).toString())){
                    position = i;
                    break;
                }
            }
            if (position != -1){
                //已存在
                Toast.makeText(context, "此视频已在下载列表", Toast.LENGTH_SHORT).show();
            }else{
                videoDownloadList.add(bean);
                String jsonList = GsonUtil.getGson().toJson(videoDownloadList);
                sp.edit().putString(AppConstants.VIDEO_DOWNLOAD_LIST, jsonList).commit();
                Toast.makeText(context, "添加到下载列表", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void removeVideoDownloadList(Context context,DownLoadBean bean){
        if (sp == null) {
            sp = context.getSharedPreferences(SPNAME, context.MODE_PRIVATE);
        }
        List<DownLoadBean> videoDownloadList = getVideoDownloadList(context);
        int position = -1;

        for (int i = 0; i < videoDownloadList.size(); i++) {
            if (bean == null) {
                if(videoDownloadList.get(i) == null){
                    //是这个
                    position = i;
                    break;
                }
            }else if (bean.toString().equals(videoDownloadList.get(i).toString())){
                position = i;
                break;
            }
        }
        if (position != -1){
            videoDownloadList.remove(position);
        }
        String jsonList = GsonUtil.getGson().toJson(videoDownloadList);
        sp.edit().putString(AppConstants.VIDEO_DOWNLOAD_LIST, jsonList).commit();

    }


}
