package com.VegaSolutions.lpptransit.lppapi.responseobjects;

public class DetourInfo {

    private String title;
    private String date;

    private String moreDataUrl;
    private String content;
    private String photoUrl;

    public DetourInfo(String title, String date) {
        this.title = title;
        this.date = date;
    }

    public DetourInfo(String title, String date, String moreDataUrl) {
        this.title = title;
        this.date = date;
        this.moreDataUrl = moreDataUrl;
    }

    public DetourInfo(String title, String date, String moreDataUrl, String content, String photoUrl) {
        this.title = title;
        this.date = date;
        this.moreDataUrl = moreDataUrl;
        this.content = content;
        this.photoUrl = photoUrl;
    }

    @Override
    public String toString() {
        return "DetourInfo{" +
                "title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", moreDataUrl='" + moreDataUrl + '\'' +
                ", content='" + content + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMoreDataUrl() {
        return moreDataUrl;
    }

    public void setMoreDataUrl(String moreDataUrl) {
        this.moreDataUrl = moreDataUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getContent() {
        return content;
    }
}
