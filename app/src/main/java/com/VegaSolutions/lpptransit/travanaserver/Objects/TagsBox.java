package com.VegaSolutions.lpptransit.travanaserver.Objects;

import java.util.Arrays;
import java.util.List;

public class TagsBox {


    public MessageTag[] tags;

    public MessageTag[] main_tags;

    public MessageTag[] getTags() {
        return tags;
    }

    public void setTags(MessageTag[] tags) {
        this.tags = tags;
    }

    public MessageTag[] getMain_tags() {
        return main_tags;
    }

    public void setMain_tags(MessageTag[] main_tags) {
        this.main_tags = main_tags;
    }

    @Override
    public String toString() {
        return "TagsBox{" +
                "tags=" + Arrays.toString(tags) +
                ", main_tags=" + Arrays.toString(main_tags) +
                '}';
    }
}
