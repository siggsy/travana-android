package com.VegaSolutions.lpptransit.animators;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RouteAnimation {

    private GoogleMap map;

    private AnimatorSet setupLoop;
    private AnimatorSet routeLoop;

    private Polyline firstLine;
    private Polyline loopLine;

    private List<Marker> stations;

    private PolylineOptions polylineOptions;
    private PolylineOptions polylineOptionsLoop;
    private MarkerOptions markerOptions;

    public RouteAnimation(GoogleMap map, MarkerOptions markerOptions, PolylineOptions polylineOptions) {
        this.map = map;
        this.markerOptions = markerOptions;
        this.polylineOptions = polylineOptions.color(Color.GRAY);
        this.polylineOptionsLoop = polylineOptions.color(Color.BLACK);
    }

    public void startAnimation(List<LatLng> stations, List<LatLng> route) {

        if (setupLoop == null) {
            setupLoop = new AnimatorSet();
        } else {
            setupLoop.removeAllListeners();
            setupLoop.end();
            setupLoop.cancel();

            setupLoop = new AnimatorSet();
        }
        if (routeLoop == null) {
            routeLoop = new AnimatorSet();
        } else {
            routeLoop.removeAllListeners();
            routeLoop.end();
            routeLoop.cancel();

            routeLoop = new AnimatorSet();
        }

        map.clear();

        PolylineOptions temp0 = new PolylineOptions().add(route.get(0)).color(Color.LTGRAY).width(5);
        PolylineOptions temp1 = new PolylineOptions().add(route.get(0)).color(Color.BLACK).width(5);

        firstLine = map.addPolyline(temp0);
        loopLine = map.addPolyline(temp1);

        ValueAnimator drawRoute = ObjectAnimator.ofObject(this, "routeSetupForward", new RouteEvaluator(), route.toArray());
        ValueAnimator redrawRoute = ObjectAnimator.ofObject(this, "routeLoopForward", new RouteEvaluator(), route.toArray());
        ValueAnimator fadeRoute = ValueAnimator.ofObject(new ArgbEvaluator(), Color.BLACK, Color.LTGRAY);
        ValueAnimator stationSetup = ValueAnimator.ofInt(0, stations.size() - 1);



        stationSetup.setDuration(500);
        stationSetup.setInterpolator(new LinearInterpolator());
        //stationSetup.setInterpolator(new DecelerateInterpolator());
        stationSetup.addUpdateListener(valueAnimator -> {
            startDropMarkerAnimation(map.addMarker(markerOptions.position(stations.get((int) valueAnimator.getAnimatedValue()))));
            //Log.i("animation", "station Started");
        });

        drawRoute.setDuration(2000);
        drawRoute.setInterpolator(new LinearInterpolator());
        drawRoute.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                routeLoop.start();
                Log.i("loop", "started");
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        redrawRoute.setDuration(2000);
        redrawRoute.setInterpolator(new LinearInterpolator());
        redrawRoute.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                Log.i("loop", "ended");
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        fadeRoute.setDuration(1200);
        fadeRoute.setInterpolator(new AccelerateInterpolator());
        fadeRoute.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                loopLine.setColor((int) valueAnimator.getAnimatedValue());
            }
        });
        fadeRoute.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                List<LatLng> latLngs = new ArrayList<>();
                latLngs.add(route.get(0));
                loopLine.setPoints(latLngs);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        routeLoop.playSequentially(redrawRoute, fadeRoute);
        routeLoop.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                routeLoop.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        setupLoop.playSequentially(stationSetup, drawRoute);
        setupLoop.start();

    }

    private void startDropMarkerAnimation(final Marker marker) {
        final LatLng target = marker.getPosition();
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = map.getProjection();
        Point targetPoint = proj.toScreenLocation(target);
        final long duration = (long) (200 + (targetPoint.y * 0.6));
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        startPoint.y = 0;
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final Interpolator interpolator = new LinearOutSlowInInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * target.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * target.latitude + (1 - t) * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    // Post again 16ms later == 60 frames per second
                    handler.postDelayed(this, 16);
                }
            }
        });
    }


    private static class RouteEvaluator implements TypeEvaluator<LatLng> {

        @Override
        public LatLng evaluate(float v, LatLng start, LatLng end) {
            //if (start != null && end != null) {
            double lat = start.latitude + v * (end.latitude - start.latitude);
            double lng = start.longitude + v * (end.longitude - start.longitude);
            return new LatLng(lat, lng);
            //}
            //return null;
        }
    }

    public void setRouteSetupForward(LatLng endLatLng) {
        List<LatLng> foregroundPoints = firstLine.getPoints();
        foregroundPoints.add(endLatLng);
        firstLine.setPoints(foregroundPoints);
    }
    public void setRouteLoopForward(LatLng endLatLng) {
        List<LatLng> foregroundPoints = loopLine.getPoints();
        foregroundPoints.add(endLatLng);
        loopLine.setPoints(foregroundPoints);
    }


}
