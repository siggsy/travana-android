package com.VegaSolutions.lpptransit.travanaserver.Objects;

import java.util.Date;

public class LiveUpdateComment {

    private String name;
    //private String photo_path;

    private String comment_id;
    private String comment_content;
    private Date created_date;
    private boolean is_visible = false;
    private boolean checked_by_admin = false;

    public LiveUpdateComment() {

    }

    public LiveUpdateComment(String name, String comment_content) {
        this.name = name;
        this.comment_content = comment_content;
        this.created_date = new Date();

        String _id = "comm_gen" + name + created_date;
        _id = _id.replaceAll(" ", "");
        _id = _id.replace("+", "");

        this.comment_id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }

    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        this.created_date = created_date;
    }

    public boolean isVisible() {
        return is_visible;
    }

    public void setVisible(boolean is_visible) {
        this.is_visible = is_visible;
    }

    public boolean isChecked_by_admin() {
        return checked_by_admin;
    }

    public void setChecked_by_admin(boolean checked_by_admin) {
        this.checked_by_admin = checked_by_admin;
    }

    @Override
    public String toString() {
        return "LiveUpdateComment [name=" + name + ", comment_id=" + comment_id + ", comment_content=" + comment_content
                + ", created_date=" + created_date + ", is_visible=" + is_visible + ", checked_by_admin="
                + checked_by_admin + "]";
    }

}
