package com.VegaSolutions.lpptransit.ui.animations;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.cardview.widget.CardView;
import androidx.core.graphics.ColorUtils;

public class ElevationAnimation {

    private final int elevationTo;
    private final View[] targets;
    private boolean selected;
    private final int[] origColor;

    public ElevationAnimation(int elevationTo, View... targets) {
        this.elevationTo = elevationTo;
        this.targets = targets;
        origColor = new int[targets.length];
        for (int i = 0; i < targets.length; i++) {
            View target = targets[i];
            if (target != null) {
                if (target instanceof CardView) {
                    origColor[i] = ((CardView) target).getCardBackgroundColor().getDefaultColor();
                } else {
                    Drawable background = target.getBackground();
                    origColor[i] = ((ColorDrawable) background).getColor();
                }
            }
        }
    }


    public void elevate(boolean selected) {
        if (selected != this.selected) {
            ValueAnimator animator = new ValueAnimator();
            animator.setInterpolator(new LinearInterpolator());
            animator.setDuration(200);

            if (selected) animator.setFloatValues(0, elevationTo);
            else animator.setFloatValues(elevationTo, 0);

            animator.addUpdateListener(animation -> {
                float value = (float) animation.getAnimatedValue();
                if (targets[0] != null)
                    targets[0].setElevation(value);
                setBackgroundColorOverlay(value * 0.0025f);
            });
            animator.start();
            this.selected = selected;
        }
    }

    private void setBackgroundColorOverlay(float ratio) {
        for (int i = 0; i < targets.length; i++) {
            View target = targets[i];
            int color = ColorUtils.blendARGB(origColor[i], Color.WHITE, ratio) | 0xFF000000;
            if (target != null) {
                if (target instanceof CardView)
                    ((CardView) target).setCardBackgroundColor(color);
                else
                    target.setBackgroundColor(color);
            }
        }
    }

}
