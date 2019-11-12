package com.VegaSolutions.lpptransit.travanaserver.Objects;

import java.util.Arrays;

public class Update {

    private int last_version;
    private int[] still_supported_versions;
    private int[] beta_verisons;

    public int getLast_version() {
        return last_version;
    }

    public void setLast_version(int last_version) {
        this.last_version = last_version;
    }

    public int[] getStill_supported_versions() {
        return still_supported_versions;
    }

    public void setStill_supported_versions(int[] still_supported_versions) {
        this.still_supported_versions = still_supported_versions;
    }

    public int[] getBeta_verisons() {
        return beta_verisons;
    }

    public void setBeta_verisons(int[] beta_verisons) {
        this.beta_verisons = beta_verisons;
    }

    @Override
    public String toString() {
        return "Update{" +
                "last_version=" + last_version +
                ", still_supported_versions=" + Arrays.toString(still_supported_versions) +
                ", beta_verisons=" + Arrays.toString(beta_verisons) +
                '}';
    }
}
