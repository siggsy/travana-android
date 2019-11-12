package com.VegaSolutions.lpptransit.travanaserver.Objects;

public class Warning {

    private int id;
    private String title;
    private String content;
    private int importance;
    private String created_date;
    private String expire_date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getExpire_date() {
        return expire_date;
    }

    public void setExpire_date(String expire_date) {
        this.expire_date = expire_date;
    }

    @Override
    public String toString() {
        return "Warning{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", importance=" + importance +
                ", created_date='" + created_date + '\'' +
                ", expire_date='" + expire_date + '\'' +
                '}';
    }
}
