package com.download.radish.downloaddemo.listener;

/**
 * Created by Administrator on 2018/2/10 0010.
 */

public interface OnDownloadListener {
        void success();//下载成功
        void fail();//下载失败
        void downloading(int position);//正在下载中
        void paused();//暂停下载
        void canceled();//取消下载
}
