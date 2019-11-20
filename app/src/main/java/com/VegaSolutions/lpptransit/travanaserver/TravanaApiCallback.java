package com.VegaSolutions.lpptransit.travanaserver;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import java.io.InputStream;

public interface TravanaApiCallback<T> {

    /**
     * Executed when API method completed
     * @param apiResponse representing JSON
     * @param statusCode HTML status code
     * @param success boolean if it was successfully executed
     */
    void onComplete(@Nullable T apiResponse, int statusCode, boolean success);

}
