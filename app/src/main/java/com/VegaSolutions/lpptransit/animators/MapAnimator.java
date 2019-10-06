package com.VegaSolutions.lpptransit.animators;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapAnimator {

    public static final MarkerOptions DEFAULT_OPTIONS = new MarkerOptions().anchor(0.5f, 0.5f).visible(false);

    private GoogleMap map;

    private List<AnimatorSet> runningAnimations = new ArrayList<>();
    private List<StationIcon> markers = new ArrayList<>();

    private MapAnimatorListener listener;

    public MapAnimator(Activity activity, GoogleMap map) {
        listener = (MapAnimatorListener) activity;
        this.map = map;
    }


    public AnimatorSet animateStations(List<StationIcon> stations, int duration) {

        //cancelAllAnimations();

        AnimatorSet removeSet = removeAll();
        removeSet.setDuration(duration);

        AnimatorSet stationSet = setupStationAnimatorSet(stations);
        stationSet.setDuration(duration);
        AnimatorSet together = new AnimatorSet();
        together.playTogether(removeSet, stationSet);
        together.start();
        markers = stations;
        runningAnimations.add(together);
        return together;
    }

    public void removeMarkers(int duration) {

        removeAll().setDuration(duration).start();
        //runningAnimations.add(removeSet);

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

    private AnimatorSet removeAll () {

        List<ValueAnimator> animators = new ArrayList<>();

        Interpolator interpolator = new AccelerateInterpolator();
        for (int i = 0, markersSize = markers.size(); i < markersSize; i++) {
            StationIcon stationIcon = markers.get(i);

            // Set marker on map
            Marker marker = stationIcon.marker;

            // Get bitmap
            Bitmap bitmap = stationIcon.bitmap;
            int width = (int) (bitmap.getWidth() * stationIcon.scale);
            int height = (int) (bitmap.getHeight() * stationIcon.scale);

            ValueAnimator animator = ValueAnimator.ofFloat(1f, 0.1f);
            animator.setInterpolator(new AnticipateInterpolator());
            long time = (long) (1500 * interpolator.getInterpolation(((float) i / (float) markersSize)));
            animator.setStartDelay(time);

            // Update scale
            animator.addUpdateListener(valueAnimator -> {
                float scale = (float) valueAnimator.getAnimatedValue();
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmap, (int) (width * scale), (int) (height * scale), false)));
                marker.setVisible(true);
            });
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    listener.onRemoveAnimationFinished(marker);
                    markers.remove(marker);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animators.add(animator);
        }
        AnimatorSet stationSet = new AnimatorSet();
        if (animators.size() > 0)
            stationSet.playTogether(animators.toArray(new Animator[0]));

        return stationSet;

    }

    private void cancelAllAnimations() {
        for (AnimatorSet animatorSet : runningAnimations) {
            animatorSet.start();
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
            Marker marker = stationIcon.marker;

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
        if (stationIcons.size() > 0)
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
        public Marker marker;
        public float scale;

        public StationIcon(Marker marker, Bitmap bitmap) {
            this(marker, bitmap, 1);
        }

        public StationIcon(Marker options, Bitmap bitmap, float scale) {
            this.bitmap = bitmap;
            this.marker = options;
            this.scale = scale;
        }

    }

    public interface MapAnimatorListener {
        void onRemoveAnimationFinished(Marker marker);
    }

}
