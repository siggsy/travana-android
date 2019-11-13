package com.VegaSolutions.lpptransit.travanaserver.Objects;

public class UserTag {

    private String tag;
    private String tag_color;

    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }
    public String getTag_color() {
        return tag_color;
    }
    public void setTag_color(String tag_color) {
        this.tag_color = tag_color;
    }

    @Override
    public String toString() {
        return "UserTag{" +
                "tag='" + tag + '\'' +
                ", tag_color='" + tag_color + '\'' +
                '}';
    }
}
