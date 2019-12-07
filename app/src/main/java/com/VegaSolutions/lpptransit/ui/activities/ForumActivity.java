package com.VegaSolutions.lpptransit.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.travanaserver.Objects.LiveUpdateMessage;
import com.VegaSolutions.lpptransit.travanaserver.TravanaAPI;
import com.VegaSolutions.lpptransit.travanaserver.TravanaApiCallback;
import com.VegaSolutions.lpptransit.ui.animations.ElevationAnimation;
import com.VegaSolutions.lpptransit.ui.fragments.FragmentHeaderCallback;
import com.VegaSolutions.lpptransit.ui.fragments.PostListFragment;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class ForumActivity extends AppCompatActivity implements FragmentHeaderCallback {


    private View header;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ImageView searchButton;
    private FloatingActionButton newMessage;
    private ElevationAnimation elevationAnimation;

    private ViewPagerAdapter adapter;


    private void setupUI() {

        adapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        newMessage.setOnClickListener(v -> startActivity(new Intent(this, NewMessageActivity.class)));

        final Intent tagSearch = new Intent(this, TagsActivity.class);
        tagSearch.putExtra("TYPE", TagsActivity.TYPE_NORMAL);
        searchButton.setOnClickListener(v -> startActivity(tagSearch));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ViewGroupUtils.isDarkTheme(this) ? R.style.DarkTheme : R.style.WhiteTheme);
        setContentView(R.layout.activity_forum);

        header = findViewById(R.id.header);
        viewPager = findViewById(R.id.forum_view_pager);
        searchButton = findViewById(R.id._forum_search_image_view);
        tabLayout = findViewById(R.id.forum_tab_layout);
        newMessage = findViewById(R.id.new_message_fab);

        elevationAnimation = new ElevationAnimation(header, 16);

        setupUI();

    }

    @Override
    public void onHeaderChanged(boolean selected) {
        newMessage.setVisibility(selected ? View.GONE : View.VISIBLE);
        elevationAnimation.elevate(selected);
    }


    private class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return PostListFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return getString(R.string.all);
                case 1: return getString(R.string.following);

            }
            return super.getPageTitle(position);
        }
    }

}
