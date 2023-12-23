package com.VegaSolutions.lpptransit.ui.customviews;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class NullSafeView<T extends View> {

    private T view;
    private final List<OnViewAssignedListener> listeners = new ArrayList<>();


    public synchronized void addTask(OnViewAssignedListener listener) {
        listeners.add(listener);
        onAssigned();
    }

    public void setView(T view) {
        this.view = view;
        onAssigned();
    }

    public T getView() {
        return view;
    }

    private synchronized void onAssigned() {
        if (view != null) {
            for (OnViewAssignedListener listener : listeners)
                listener.onAssigned(view);
            listeners.clear();
        }
    }

    public interface OnViewAssignedListener {
        void onAssigned(View view);
    }

}
