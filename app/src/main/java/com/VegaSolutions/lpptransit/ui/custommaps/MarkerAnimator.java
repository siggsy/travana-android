package com.VegaSolutions.lpptransit.ui.custommaps;

import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MarkerAnimator {

    private Handler handler;
    private Runnable current;

    public MarkerAnimator() {
        handler = new Handler();
    }

    /**
     * Animate marker from point A to B.
     * @param marker marker to animate
     * @param finalPosition point B
     * @param finalCardinalDirection final rotation
     * @param latLngInterpolator interpolator to interpolate marker
     */
    public void animateMarker(Marker marker, LatLng finalPosition, float finalCardinalDirection, LatLngInterpolator latLngInterpolator) {

        final LatLng startPosition = marker.getPosition();
        float startCardinalDirection = marker.getRotation() % 360;
        final float beginCardinalDirection;
        final float endCardinalDirection;

        // Adjust angle animation (shortest path).
        if (Math.abs(finalCardinalDirection - startCardinalDirection) > 180) {
            if (finalCardinalDirection > startCardinalDirection)
                startCardinalDirection += 360;
            else finalCardinalDirection += 360;
        }

        endCardinalDirection = finalCardinalDirection;
        beginCardinalDirection = startCardinalDirection;

        handler.removeCallbacks(current);
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 1000;

        current = new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {

                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                // Update marker
                marker.setPosition(latLngInterpolator.interpolate(v, startPosition, finalPosition));
                marker.setRotation(v * (endCardinalDirection - beginCardinalDirection) + beginCardinalDirection);

                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        };

        handler.post(current);
    }

}
