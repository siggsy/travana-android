package com.VegaSolutions.lpptransit.firebase;

import androidx.annotation.Nullable;

public interface FirebaseCallback {

    void onComplete(@Nullable String data, Exception error, boolean success);
}
