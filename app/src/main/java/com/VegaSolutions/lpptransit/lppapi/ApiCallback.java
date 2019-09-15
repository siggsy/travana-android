package com.VegaSolutions.lpptransit.lppapi;

import androidx.annotation.Nullable;

import com.VegaSolutions.lpptransit.lppapi.responseobjects.ApiResponse;

public interface ApiCallback<T> {

    /**
     * Executed when API method completed
     * @param apiResponse <T> representing JSON
     * @param statusCode HTML status code
     * @param success boolean if it was successfully executed
     */
    void onComplete(@Nullable ApiResponse<T> apiResponse, int statusCode, boolean success);
}
