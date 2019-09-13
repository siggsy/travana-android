package com.VegaSolutions.lpptransit.lppapi;

import androidx.annotation.Nullable;

import java.util.Map;

public interface ApiCallback<T> {

    /**
     * Executed when API method completed
     * @param data <T> representing JSON
     * @param statusCode HTML status code
     * @param success boolean if it was successfully executed
     */
    void onComplete(@Nullable T data, int statusCode, boolean success);
}
