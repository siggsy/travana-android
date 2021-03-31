package com.VegaSolutions.lpptransit.ui.fragments.lpp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.ApiCallback;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station;
import com.VegaSolutions.lpptransit.ui.animations.ElevationAnimation;
import com.VegaSolutions.lpptransit.ui.fragments.FragmentHeaderCallback;
import com.VegaSolutions.lpptransit.ui.fragments.lpp.subfragments.StationsSubFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class StationsFragment extends Fragment implements FragmentHeaderCallback {


    private Context context;

    // Activity UI elements
    private FrameLayout header;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Adapter adapter;
    private ElevationAnimation animation;

    private StationsFragmentListener mListener;
    private FragmentHeaderCallback headerListener = null;
    private OnFragmentCreatedListener createdListener = null;

    private ApiCallback<List<Station>> callback = (apiResponse, statusCode, success) -> {

            if (context == null)
                return;

            if (success)
                mListener.onStationsUpdated(apiResponse.getData(), true, statusCode);
            else mListener.onStationsUpdated(null, false, statusCode);
            if (adapter.registeredFragments.size() == 2) {
                if (success)
                    for (int i = 0; i < adapter.registeredFragments.size(); i++)
                        adapter.registeredFragments.get(i).setStations(apiResponse.getData(), statusCode, true);
            } else if (success) createdListener = fragment -> fragment.setStations(apiResponse.getData(), statusCode, true);

    };


    public static StationsFragment newInstance() {
        StationsFragment fragment = new StationsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private void setupUI() {

        animation = new ElevationAnimation(16, header);

        adapter = new Adapter(getParentFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        // ViewPager with TabLayout
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        // Set tab layout icon color switcher
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mListener.onTabClicked();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                mListener.onTabClicked();
            }
        });

    }

    /**
     * Remove previous or cached fragments to avoid any unwanted results
     */
    private void removeFragments() {

        FragmentManager fm = getParentFragmentManager();

        for (Fragment fragment : fm.getFragments())
            if (fragment instanceof StationsSubFragment) {
                try {
                    fm.beginTransaction().remove(fragment).commit();
                } catch (IllegalStateException e) {
                    return;
                }

            }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        removeFragments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_stations, container, false);

        // Find all UI elements
        viewPager = root.findViewById(R.id.station_view_pager);
        header = root.findViewById(R.id.header);
        tabLayout = root.findViewById(R.id.tab_layout);

        setupUI();

        Api.stationDetails(false, callback);

        return root;
    }

    public void refresh() {
        Api.stationDetails(false, callback);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof StationsFragmentListener) {
            mListener = (StationsFragmentListener) context;
            headerListener = (FragmentHeaderCallback) context;
            this.context = context;
        } else throw new RuntimeException(context.toString() + " must implement StationsFragmentListener");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeFragments();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }

    @Override
    public void onHeaderChanged(boolean selected) {
        animation.elevate(selected);
        if (headerListener != null)
            headerListener.onHeaderChanged(selected);
    }

    private class Adapter extends FragmentPagerAdapter {

        SparseArray<StationsSubFragment> registeredFragments = new SparseArray<>();

        private Adapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return StationsSubFragment.newInstance(StationsSubFragment.TYPE_FAVOURITE, StationsFragment.this);
            return StationsSubFragment.newInstance(StationsSubFragment.TYPE_NEARBY, StationsFragment.this);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }


        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, (StationsSubFragment) fragment);
            if (createdListener != null) {
                createdListener.onFragmentCreated((StationsSubFragment) fragment);
                if (registeredFragments.size() == 2)
                    createdListener = null;
            }
            return fragment;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return context.getString(R.string.favourite);
                case 1:
                    return context.getString(R.string.nearby);
                default:
                    return super.getPageTitle(position);
            }

        }
    }

    public interface StationsFragmentListener {
        void onStationsUpdated(List<Station> stations, boolean success, int responseCode);
        void onTabClicked();
    }

    interface OnFragmentCreatedListener {
        void onFragmentCreated(StationsSubFragment fragment);
    }

}
