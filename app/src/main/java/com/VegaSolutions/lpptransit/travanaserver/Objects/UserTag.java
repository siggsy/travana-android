package com.VegaSolutions.lpptransit.travanaserver.Objects;

public class UserTag {

    private String _id;
    private String tag;
    private String color;

    private String description_slo = "";
    private String description_ang = "";

    public String get_id() {
        return _id;
    }

    public String getTag() {
        return tag;
    }

    public String getColor() {
        return color;
    }

    public String getDescription_slo() {
        return description_slo;
    }

    public String getDescription_ang() {
        return description_ang;
    }

    @Override
    public String toString() {
        return "UserTag{" +
                "_id='" + _id + '\'' +
                ", tag='" + tag + '\'' +
                ", color='" + color + '\'' +
                ", description_slo='" + description_slo + '\'' +
                ", description_ang='" + description_ang + '\'' +
                '}';
    }
}
