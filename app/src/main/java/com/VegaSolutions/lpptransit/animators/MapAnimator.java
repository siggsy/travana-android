package com.VegaSolutions.lpptransit.animators;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

import com.VegaSolutions.lpptransit.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapAnimator {

    private GoogleMap map;

    private List<AnimatorSet> runningAnimations = new ArrayList<>();

    public MapAnimator(GoogleMap map) {
        this.map = map;
    }


    public void animateStations(List<StationIcon> stations, int duration) {

        cancelAllAnimations();

        AnimatorSet stationSet = setupStationAnimatorSet(stations);
        stationSet.setDuration(duration);
        stationSet.start();
        runningAnimations.add(stationSet);

    }
    public void animateRouteWithStations(List<StationIcon> stations, PolylineOptions routeOptions, int durationStation, int durationRoute) {

        cancelAllAnimations();

        AnimatorSet stationAnimatorSet = setupStationAnimatorSet(stations).setDuration(durationStation);
        ValueAnimator routeAnimator = setupRouteAnimator(routeOptions).setDuration(durationRoute);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(stationAnimatorSet, routeAnimator);
        animatorSet.start();
        runningAnimations.add(animatorSet);

    }

    private void cancelAllAnimations() {
        for (AnimatorSet animatorSet : runningAnimations) {
            animatorSet.end();
            animatorSet.cancel();
        }
        runningAnimations = new ArrayList<>();
    }
    private AnimatorSet setupStationAnimatorSet(List<StationIcon> stationIcons) {

        List<ValueAnimator> animators = new ArrayList<>();

        // Set interpolator for station spawning.
        Interpolator interpolator = new AccelerateInterpolator();

        for (int i = 0, stationsSize = stationIcons.size(); i < stationsSize; i++) {
            StationIcon stationIcon = stationIcons.get(i);

            // Set marker on map
            Marker marker = map.addMarker(stationIcon.options.anchor(0.5f, 0.5f).visible(false));

            // Get bitmap
            Bitmap bitmap = stationIcon.bitmap;
            int width = (int) (bitmap.getWidth() * stationIcon.scale);
            int height = (int) (bitmap.getHeight() * stationIcon.scale);

            ValueAnimator animator = ValueAnimator.ofFloat(0.1f, 1f);
            animator.setInterpolator(new OvershootInterpolator());
            long time = (long) (1500 * interpolator.getInterpolation(((float) i / (float) stationsSize)));
            animator.setStartDelay(time);

            // Update scale
            animator.addUpdateListener(valueAnimator -> {
                float scale = (float) valueAnimator.getAnimatedValue();
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmap, (int) (width * scale), (int) (height * scale), false)));
                marker.setVisible(true);
            });
            animators.add(animator);
        }

        AnimatorSet stationSet = new AnimatorSet();
        stationSet.setInterpolator(new OvershootInterpolator());
        stationSet.playTogether(animators.toArray(new Animator[0]));

        return stationSet;
    }
    private ValueAnimator setupRouteAnimator(PolylineOptions lineOptions) {

        ValueAnimator drawRoute = ValueAnimator.ofFloat(0, lineOptions.getWidth());
        drawRoute.setInterpolator(new DecelerateInterpolator());

        Polyline line = map.addPolyline(lineOptions);
        drawRoute.addUpdateListener(valueAnimator -> line.setWidth((float) valueAnimator.getAnimatedValue()));

        return drawRoute;

    }

    public static class StationIcon {

        public Bitmap bitmap;
        public MarkerOptions options;
        public float scale;

        public StationIcon(MarkerOptions options, Bitmap bitmap) {
            this(options, bitmap, 1);
        }

        public StationIcon(MarkerOptions options, Bitmap bitmap, float scale) {
            this.bitmap = bitmap;
            this.options = options;
            this.scale = scale;
        }

    }

}
