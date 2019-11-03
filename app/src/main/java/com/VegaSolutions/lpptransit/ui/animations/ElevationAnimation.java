package com.VegaSolutions.lpptransit.ui.animations;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

public class ElevationAnimation {

    private int elevationTo;
    private View target;
    private boolean selected;

    public ElevationAnimation(View target, int elevationTo) {
        this.elevationTo = elevationTo;
        this.target = target;
    }


    public void elevate(boolean selected) {
        if (selected != this.selected) {
            ValueAnimator animator = new ValueAnimator();
            animator.setInterpolator(new LinearInterpolator());
            animator.setDuration(200);

            if (selected) animator.setFloatValues(0, elevationTo);
            else animator.setFloatValues(elevationTo, 0);

            animator.addUpdateListener(animation -> target.setElevation((float) animation.getAnimatedValue()));
            animator.start();
            this.selected = selected;
        }
    }

}
