package com.VegaSolutions.lpptransit.firebase;

import androidx.annotation.Nullable;

public interface FirebaseCallback {

    /**
     * Callback for token request
     * @param data requested token
     * @param error Exception if error occurred
     * @param success boolean if request was successful
     */
    void onComplete(@Nullable String data, Exception error, boolean success);
}
