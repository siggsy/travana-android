package com.VegaSolutions.lpptransit.animators;

import com.google.android.gms.maps.model.Marker;

public class AnimatorManager implements MapAnimator.MapAnimatorListener {


    @Override
    public void onRemoveAnimationFinished(Marker marker) {
        marker.remove();
    }
}
