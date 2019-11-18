package com.VegaSolutions.lpptransit.ui.fragments;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.ApiCallback;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.ApiResponse;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station;
import com.VegaSolutions.lpptransit.ui.animations.ElevationAnimation;
import com.VegaSolutions.lpptransit.ui.fragments.subfragments.StationsSubFragment;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.tabs.TabLayout;

import java.util.List;


// TODO: Clean code.

public class StationsFragment extends Fragment implements FragmentHeaderCallback {


    private Context context;

    private FrameLayout header;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private Adapter adapter;
    private ElevationAnimation animation;

    private StationsFragmentListener mListener;


    public StationsFragment() {
    }


    public static StationsFragment newInstance() {
        StationsFragment fragment = new StationsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private void setupUI() {
        animation = new ElevationAnimation(header, 16);

        adapter = new Adapter(getFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        // ViewPager with TabLayout
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            switch (i) {
                case 0:
                    int color = ContextCompat.getColor(context, R.color.colorAccent);
                    TabLayout.Tab tab = tabLayout.getTabAt(i);
                    tab.setIcon(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_favorite_black_24dp, null));
                    tab.getIcon().setTint(color);
                    break;
                case 1:
                    tab = tabLayout.getTabAt(i);
                    tab.setIcon(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_location_on_black_24dp, null));
                    tab.getIcon().setTint(Color.GRAY);
                    break;
            }
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int color;
                switch (tab.getPosition()) {
                    case 0:
                        color = ContextCompat.getColor(context, R.color.colorAccent);
                        break;
                    case 1:
                        color = ContextCompat.getColor(context, R.color.main_blue_dark);
                        break;
                    default:
                        color = Color.GRAY;
                }
                tab.getIcon().setTint(color);
                mListener.onTabClicked();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int color = Color.GRAY;
                tab.getIcon().setTint(color);
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                mListener.onTabClicked();
            }
        });

    }

    private void removeFragments() {

        FragmentManager fm = getFragmentManager();

        if (fm != null) {
            for (Fragment fragment : fm.getFragments())
                if (fragment instanceof StationsSubFragment) {
                    try {
                        fm.beginTransaction().remove(fragment).commit();
                    } catch (IllegalStateException e) {
                        return;
                    }

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

        viewPager = root.findViewById(R.id.station_view_pager);
        header = root.findViewById(R.id.header);
        tabLayout = root.findViewById(R.id.tab_layout);

        setupUI();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof StationsFragmentListener) {
            mListener = (StationsFragmentListener) context;
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
    }

    private class Adapter extends FragmentPagerAdapter {

        SparseArray<Fragment> registeredFragments = new SparseArray<>();

        public Adapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return StationsSubFragment.newInstance(StationsSubFragment.TYPE_FAVOURITE, StationsFragment.this);
                case 1:
                    return StationsSubFragment.newInstance(StationsSubFragment.TYPE_NEARBY, StationsFragment.this);
                default:
                    return null;
            }
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
            registeredFragments.put(position, fragment);
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
        void onFragmentInteraction(Uri uri);
        void onStationsUpdated(List<Station> stations, boolean success, int responseCode);
        void onTabClicked();
    }

}
