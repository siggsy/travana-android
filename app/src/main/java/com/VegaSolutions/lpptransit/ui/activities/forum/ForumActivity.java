package com.VegaSolutions.lpptransit.ui.activities.forum;

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

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.ui.animations.ElevationAnimation;
import com.VegaSolutions.lpptransit.ui.fragments.FragmentHeaderCallback;
import com.VegaSolutions.lpptransit.ui.fragments.forum.PostListFragment;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ForumActivity extends AppCompatActivity implements FragmentHeaderCallback {


    @BindView(R.id.header) View header;
    @BindView(R.id.forum_view_pager) ViewPager viewPager;
    @BindView(R.id.forum_tab_layout) TabLayout tabLayout;
    @BindView(R.id._forum_search_image_view) ImageView searchButton;
    @BindView(R.id.new_message_fab) FloatingActionButton newMessage;

    ElevationAnimation elevationAnimation;
    ViewPagerAdapter adapter;


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
        ButterKnife.bind(this);

        elevationAnimation = new ElevationAnimation(header, 16);

        setupUI();

    }

    @Override
    public void onHeaderChanged(boolean selected) {
        newMessage.setVisibility(selected ? View.GONE : View.VISIBLE);
        elevationAnimation.elevate(selected);
    }


    private class ViewPagerAdapter extends FragmentPagerAdapter {

        ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
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
                case 0: return getString(R.string.following);
                case 1: return getString(R.string.all);
            }
            return super.getPageTitle(position);
        }
    }

}
