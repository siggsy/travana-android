package com.VegaSolutions.lpptransit.travanaserver.Objects;

import java.util.Arrays;
import java.util.List;

public class TagsBox {


    public MessageTag[] tags;

    public MessageTag[] main_tags;

    public UserTag[] user_tags;

    public UserTag[] getUser_tags() {
        return user_tags;
    }

    public void setUser_tags(UserTag[] user_tags) {
        this.user_tags = user_tags;
    }

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
                ", user_tags=" + Arrays.toString(user_tags) +
                '}';
    }
}
