package com.VegaSolutions.lpptransit.travanaserver.Objects;

import java.util.Arrays;

public class Update {

    private int last_version;
    private int[] still_supported_versions;
    private int[] beta_verisons;
    private String play_store_link;

    public int getLast_version() {
        return last_version;
    }

    public int[] getStill_supported_versions() {
        return still_supported_versions;
    }

    public int[] getBeta_verisons() {
        return beta_verisons;
    }

    public String getPlay_store_link() {
        return play_store_link;
    }

    @Override
    public String toString() {
        return "Update{" +
                "last_version=" + last_version +
                ", still_supported_versions=" + Arrays.toString(still_supported_versions) +
                ", beta_verisons=" + Arrays.toString(beta_verisons) +
                ", play_store_link='" + play_store_link + '\'' +
                '}';
    }
}
