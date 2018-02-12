package com.download.radish.downloaddemo.bean;

/**
 * Created by Administrator on 2018/2/11 0011.
 * {"filePath":"/storage/emulated/0/JavaDemo/FileDown/FileVideo/Temp/apiRoad.mp4",
 *  "name":"apiRoad",
 *  "suffix":".mp4",
 *  "url":"http://192.168.43.248:8080/javademo/upload/video/apiRoad.mp4"}
 */

public class DownLoadBean {
    private String name;
    private String suffix;
    private String url;
    private String filePath;

    public DownLoadBean() {}

    public DownLoadBean(String name, String suffix, String url,String filePath) {
        this.name = name;
        this.suffix = suffix;
        this.url = url;
        this.filePath = filePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        suffix = suffix;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "DownLoadBean{" +
                "name='" + name + '\'' +
                ", suffix='" + suffix + '\'' +
                ", url='" + url + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}
