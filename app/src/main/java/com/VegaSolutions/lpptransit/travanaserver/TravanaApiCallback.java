package com.VegaSolutions.lpptransit.travanaserver;

import androidx.annotation.Nullable;

public interface TravanaApiCallback<T> {

    /**
     * Executed when API method completed
     * @param apiResponse representing JSON
     * @param statusCode HTML status code
     * @param success boolean if it was successfully executed
     */
    void onComplete(@Nullable String apiResponse, int statusCode, boolean success);
}
