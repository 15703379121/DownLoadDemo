package com.download.radish.downloaddemo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.download.radish.downloaddemo.util.AppConstants;
import com.download.radish.downloaddemo.bean.DownLoadBean;
import com.download.radish.downloaddemo.service.DownloadService;
import com.download.radish.downloaddemo.listener.OnDownloadListener;
import com.download.radish.downloaddemo.R;
import com.download.radish.downloaddemo.util.SPUtil;
import com.download.radish.downloaddemo.util.ServiceUtil;
import com.download.radish.downloaddemo.util.VideoUtil;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2018/2/10 0010.
 */

public class DownloadActivity extends Activity {
    private DownloadService.DownloadBinder mBinder;
    private static final int MSG_SUCCESS = 0;
    private static final int MSG_FAIL = 1;
    private static final int MSG_DOWNLOADING = 2;
    private static final int MSG_PAUSED = 3;
    private static final int MSG_CANCEL = 4;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_SUCCESS:
                    //移除已下载
                    SPUtil.removeVideoDownloadList(DownloadActivity.this,mDownLoadBean);
                    if (mDownloadPosition!=-1) {
                        updateList(mDownloadPosition);
                    }
                    Toast.makeText(DownloadActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_FAIL:
                    if (mIvIconDownload != null){
                        mIvIconDownload.setSelected(false);
                        mTvIconDownload.setText("已暂停");
                    }
                    Toast.makeText(DownloadActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_DOWNLOADING:
                    if (mPbDownloading != null) {
                        mPbDownloading.setProgress(mPosition);
                    }
                    if(mTvDownloading != null) {
                        mTvDownloading.setText(mPosition + "%");
                    }
                    break;
                case MSG_PAUSED:
                    break;
                case MSG_CANCEL:

                    break;
            }
        }
    };
    private int mPosition;
    private ListView mLvDownloading;
    private List<DownLoadBean> mDownloadList;
    private int mDownloadPosition = -1;
    private ProgressBar mPbDownloading;
    private TextView mTvDownloading;
    private ImageView mIvIconDownload;
    private TextView mTvIconDownload;
    private DownLoadBean mDownLoadBean;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        Intent intent = new Intent(this, DownloadService.class);
        if (!ServiceUtil.isServiceRunning(this,"com.download.radish.downloaddemo.service.DownloadService")){
            startService(intent);
        }
        bindService(intent,connection,BIND_AUTO_CREATE);//绑定服务
        initView();
    }

    private void initView() {
        mLvDownloading = (ListView) findViewById(R.id.lv_downloading);
    }

    private void initData() {
        //正在下载中文件
        mDownloadList = SPUtil.getVideoDownloadList(this);
        if (mDownloadList != null && mDownloadList.size() > 0){
            mAdapter = new MyAdapter();
            //有缓存文件
            mLvDownloading.setAdapter(mAdapter);
            //长按删除
            mLvDownloading.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DownloadActivity.this);
                    TextView tv = new TextView(DownloadActivity.this);
                    tv.setText("删除");
                    tv.setGravity(Gravity.CENTER);
                    tv.setTextColor(Color.BLACK);
                    tv.setPadding(30,10,30,10);
                    tv.setBackgroundColor(Color.WHITE);
                    builder.setView(tv);
                    final AlertDialog dialog = builder.create();
                    dialog.show();
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (position == mDownloadPosition){
                                //若是正在下载的文件则停止下载
                                mBinder.cancelDownload();
                            }
                            DownLoadBean downLoadBean = mDownloadList.get(position);
                            //从下载列表中删除
                            SPUtil.removeVideoDownloadList(DownloadActivity.this,downLoadBean);
                            //若是本地有文件则也删除本地文件
                            File file = new File(downLoadBean.getFilePath());
                            if (file.exists()){
                                file.delete();
                            }
                            //若本地有图片也要删除
                            File filePic = new File(AppConstants.FILE_DOWN_VIDEO_THUMBNAIL+"/"+downLoadBean.getName()+".png");
                            if (filePic.exists()){
                                filePic.delete();
                            }
                            //更新列表
                            updateList(position);
                            dialog.dismiss();
                        }
                    });
                    return true;
                }
            });
        }else{
            mLvDownloading.setVisibility(View.GONE);
        }
    }

    private void updateList(int position){
        mDownloadList.remove(position);
        if (mAdapter != null){
            mAdapter.notifyDataSetChanged();
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (DownloadService.DownloadBinder) service;
            mBinder.setDownloadListener(mDownloadListener);

            initData();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connection!=null) {
            unbindService(connection);
        }
    }

    OnDownloadListener mDownloadListener = new OnDownloadListener() {
        @Override
        public void success() {
            handler.sendEmptyMessage(MSG_SUCCESS);
        }

        @Override
        public void fail() {
            handler.sendEmptyMessage(MSG_FAIL);
        }

        @Override
        public void downloading(int position) {
            mPosition = position;
            handler.sendEmptyMessage(MSG_DOWNLOADING);
        }

        @Override
        public void paused() {

        }

        @Override
        public void canceled() {

        }
    };

    class MyAdapter extends BaseAdapter{

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
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(DownloadActivity.this).inflate(R.layout.item_downloading,null);
            TextView mTvFileName = (TextView) convertView.findViewById(R.id.tv_file_name);
            ImageView mIvVideoBg = (ImageView) convertView.findViewById(R.id.iv_video_bg);
            LinearLayout llDownloading = (LinearLayout) convertView.findViewById(R.id.ll_downloading);
            final TextView tvDownloading = (TextView) convertView.findViewById(R.id.tv_downloading);
            final TextView tvIconDownload = (TextView) convertView.findViewById(R.id.tv_icon_download);
            final ImageView ivIconDownload = (ImageView) convertView.findViewById(R.id.iv_icon_download);
            final ProgressBar pbDownloading = (ProgressBar) convertView.findViewById(R.id.pb_downloading);
            LinearLayout llIconDownload = (LinearLayout) convertView.findViewById(R.id.ll_icon_download);
            final DownLoadBean downLoadBean = mDownloadList.get(position);
            mTvFileName.setText(""+downLoadBean.getName());
            Bitmap bitmap = VideoUtil.getLocalPic(AppConstants.FILE_DOWN_VIDEO_THUMBNAIL +"/"+ downLoadBean.getName() + ".png");
            if (bitmap != null){
                mIvVideoBg.setImageBitmap(bitmap);
            }

            if (mBinder != null && mBinder.isDownLoading() && (""+downLoadBean.getName()).equals(mBinder.getDownLoadBean().getName())){
                mPbDownloading = pbDownloading;
                mTvDownloading = tvDownloading;
                mDownloadPosition = position;
                mDownLoadBean = downLoadBean;
                ivIconDownload.setSelected(true);
                tvIconDownload.setText("缓存中");
                mIvIconDownload = ivIconDownload;
                mTvIconDownload = tvIconDownload;
            }else{
                mDownloadPosition = -1;
                ivIconDownload.setSelected(false);
                tvIconDownload.setText("已暂停");
            }
            llIconDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ivIconDownload.isSelected()){
                        //缓存变暂停
                        if (mBinder != null) {
                            mBinder.pauseDownload();
                            ivIconDownload.setSelected(false);
                            tvIconDownload.setText("已暂停");
                            mDownLoadBean = null;
                            mPbDownloading = null;
                            mTvDownloading = null;
                            mIvIconDownload = null;
                            mTvIconDownload = null;
                            mDownloadPosition = -1;
                        }
                    }else{
                        if (mBinder != null) {
                            if (mBinder.isDownLoading()) {
                                mBinder.pauseDownload();
                            }
                            //暂停变缓存
                            mDownloadPosition = position;
                            mBinder.startDownload(downLoadBean);
                            mDownLoadBean = downLoadBean;
                            mPbDownloading = pbDownloading;
                            mTvDownloading = tvDownloading;
                            ivIconDownload.setSelected(true);
                            tvIconDownload.setText("缓存中");
                            mIvIconDownload = ivIconDownload;
                            mTvIconDownload = tvIconDownload;
                        }
                    }
                }
            });

            return convertView;
        }
    }
}
