package com.VegaSolutions.lpptransit.travanaserver;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;

public interface TravanaApiCallbackSpecial {

    void onComplete(@Nullable Bitmap bitmap, int statusCode, boolean success);
}
