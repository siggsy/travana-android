package com.VegaSolutions.lpptransit.ui.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.ui.animations.ElevationAnimation;
import com.VegaSolutions.lpptransit.ui.fragments.subfragments.StationsSubFragment;
import com.google.android.material.tabs.TabLayout;


// TODO: Clean code.

public class StationsFragment extends Fragment implements FragmentHeaderCallback {


    private Context context;
    private Location location;

    private FrameLayout header;
    private TextSwitcher switcher;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private Adapter adapter;
    ElevationAnimation animation;
    private LinearLayoutManager linearLayoutManager;
    private boolean fav = true;


    public StationsFragment() {
    }


    public static StationsFragment newInstance() {
        StationsFragment fragment = new StationsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        removeFragments();
    }

    private void removeFragments() {

        FragmentManager fm = getFragmentManager();

        if (fm != null) {
            for (Fragment fragment : fm.getFragments())
                if (fragment instanceof StationsSubFragment)
                    fm.beginTransaction().remove(fragment).commit();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_stations, container, false);

        viewPager = root.findViewById(R.id.station_view_pager);
        header = root.findViewById(R.id.header);
        switcher = root.findViewById(R.id.station_title);
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
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }


    private void setupUI() {
        animation = new ElevationAnimation(header, 16);

        adapter = new Adapter(getFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        // TextSwitcher
        switcher.setFactory(() -> {
            TextView textView = new TextView(context);
            textView.setTextAppearance(context, R.style.robotoBoldTitle);
            return textView;
        });
        switcher.setCurrentText("Postaje");
        switcher.setInAnimation(context.getApplicationContext(), android.R.anim.slide_in_left);
        switcher.setOutAnimation(context.getApplicationContext(), android.R.anim.slide_out_right);
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
                    tabLayout.getTabAt(i).setIcon(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_location_on_black_24dp, null));
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
                        color = ContextCompat.getColor(context, R.color.color_main_blue_dark);
                        break;
                    default:
                        color = Color.BLACK;
                }
                tab.getIcon().setTint(color);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int color = Color.BLACK;
                tab.getIcon().setTint(color);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

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
                    return "Priljubljene";
                case 1:
                    return "V blizini";
                default:
                    return super.getPageTitle(position);
            }

        }
    }

}
