package com.VegaSolutions.lpptransit.lppapi.responseobjects;

public class DetourInfo {

    private String title;
    private String date;

    private String more_data_url;
    private String content;
    private String photo_url;

    public DetourInfo(String title, String date) {
        this.title = title;
        this.date = date;
    }

    public DetourInfo(String title, String date, String more_data_url) {
        this.title = title;
        this.date = date;
        this.more_data_url = more_data_url;
    }

    public DetourInfo(String title, String date, String more_data_url, String content, String photo_url) {
        this.title = title;
        this.date = date;
        this.more_data_url = more_data_url;
        this.content = content;
        this.photo_url = photo_url;
    }

    @Override
    public String toString() {
        return "DetourInfo{" +
                "title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", more_data_url='" + more_data_url + '\'' +
                ", photo_url='" + photo_url + '\'' +
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

    public String getMore_data_url() {
        return more_data_url;
    }

    public void setMore_data_url(String more_data_url) {
        this.more_data_url = more_data_url;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getContent() {
        return content;
    }
}
