package com.VegaSolutions.lpptransit.travanaserver.Objects;

public class Warning {

    private String _id;
    private String title_en;
    private String title_slo;
    private String content_en;
    private String content_slo;

    public String get_id() {
        return _id;
    }

    public String getTitle_en() {
        return title_en;
    }

    public String getTitle_slo() {
        return title_slo;
    }

    public String getContent_en() {
        return content_en;
    }

    public String getContent_slo() {
        return content_slo;
    }

    @Override
    public String toString() {
        return "Warning{" +
                "_id='" + _id + '\'' +
                ", title_en='" + title_en + '\'' +
                ", title_slo='" + title_slo + '\'' +
                ", content_en='" + content_en + '\'' +
                ", content_slo='" + content_slo + '\'' +
                '}';
    }
}
