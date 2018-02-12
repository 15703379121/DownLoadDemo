package com.download.radish.downloaddemo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.download.radish.downloaddemo.R;
import com.download.radish.downloaddemo.bean.DownLoadBean;
import com.download.radish.downloaddemo.service.DownloadService;
import com.download.radish.downloaddemo.util.AppConstants;
import com.download.radish.downloaddemo.util.VideoUtil;

import java.util.List;

/**
 * Created by Administrator on 2018/2/11 0011.
 */

public class DownLoadAdapter extends BaseAdapter{

    private final Context context;
    private final List<DownLoadBean> mDownloadList;
    private final DownloadService.DownloadBinder  mBinder;

    public DownLoadAdapter(Context context, List<DownLoadBean> list, DownloadService.DownloadBinder iBinder){
        this.context = context;
        mDownloadList = list;
        mBinder = iBinder;
    }

    @Override
    public int getCount() {
        return mDownloadList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_downloading,null);
        TextView mTvFileName = (TextView) convertView.findViewById(R.id.tv_file_name);
        ImageView mIvVideoBg = (ImageView) convertView.findViewById(R.id.iv_video_bg);
        LinearLayout llDownloading = (LinearLayout) convertView.findViewById(R.id.ll_downloading);
        TextView tvDownloading = (TextView) convertView.findViewById(R.id.tv_downloading);
        TextView tvIconDownload = (TextView) convertView.findViewById(R.id.tv_icon_download);
        ImageView ivIconDownload = (ImageView) convertView.findViewById(R.id.iv_icon_download);
        ProgressBar pbDownloading = (ProgressBar) convertView.findViewById(R.id.pb_downloading);
        LinearLayout llIconDownload = (LinearLayout) convertView.findViewById(R.id.ll_icon_download);
        DownLoadBean downLoadBean = mDownloadList.get(position);
        mTvFileName.setText(""+downLoadBean.getName());
        Bitmap bitmap = VideoUtil.getLocalPic(AppConstants.FILE_DOWN_VIDEO_THUMBNAIL + downLoadBean.getName() + ".png");
        if (bitmap != null){
            mIvVideoBg.setImageBitmap(bitmap);
        }
        if (mBinder.isDownLoading() && (""+downLoadBean.getName()).equals(mBinder.getDownLoadBean().getName())){
            ivIconDownload.setSelected(true);
            tvIconDownload.setText("缓存中");
        }else{
            ivIconDownload.setSelected(false);
            tvIconDownload.setText("已暂停");
        }
        llIconDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (mIvIconDownload.isSelected()){
                    //缓存变暂停
                    mBinder.pauseDownload();
                    mIvIconDownload.setSelected(false);
                    mTvIconDownload.setText("已暂停");
                }else{
                    //暂停变缓存
                    mBinder.startDownload(AppConstants.VIDEO_URL_PATH+fileNames[0],AppConstants.FILE_DOWN_VIDEO_TEMP+"/"+fileNames[0]);
                    mIvIconDownload.setSelected(true);
                    mTvIconDownload.setText("缓存中");
                }*/
            }
        });

        return convertView;
    }
}