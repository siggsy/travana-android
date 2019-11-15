package com.VegaSolutions.lpptransit.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.travanaserver.Objects.LiveUpdateMessage;
import com.VegaSolutions.lpptransit.travanaserver.TravanaAPI;
import com.VegaSolutions.lpptransit.travanaserver.TravanaApiCallback;
import com.VegaSolutions.lpptransit.ui.fragments.PostListFragment;
import com.google.android.material.tabs.TabLayout;

public class ForumActivity extends AppCompatActivity {


    private View header;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TextView newMessages;
    private ImageView searchButton;

    private ViewPagerAdapter adapter;


    private void setupUI() {

        adapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        header = findViewById(R.id.header);
        viewPager = findViewById(R.id.forum_view_pager);
        newMessages = findViewById(R.id.forum_new_messages_count);
        searchButton = findViewById(R.id._forum_search_image_view);
        tabLayout = findViewById(R.id.forum_tab_layout);

        setupUI();

    }


    private class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            //return PostListFragment.newInstance(position);
            return null;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return getString(R.string.following);
                case 1: return getString(R.string.all);
            }
            return super.getPageTitle(position);
        }
    }

}
