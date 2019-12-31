package com.VegaSolutions.lpptransit.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;

import butterknife.BindViews;
import butterknife.ButterKnife;

public class LauncherActivity extends AppCompatActivity {

    @BindViews({R.id.v1, R.id.v2, R.id.v3, R.id.v4}) View[] v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ViewGroupUtils.isDarkTheme(this) ? R.style.DarkTheme : R.style.WhiteTheme);
        setContentView(R.layout.activity_launcher);
        ButterKnife.bind(this);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.popout_fast);
        animation.setStartOffset(0);
        v[0].setAnimation(animation);

        animation = AnimationUtils.loadAnimation(this, R.anim.popout_fast);
        animation.setStartOffset(100);
        v[1].setAnimation(animation);

        animation = AnimationUtils.loadAnimation(this, R.anim.popout_fast);
        animation.setStartOffset(200);
        v[2].setAnimation(animation);

        animation = AnimationUtils.loadAnimation(this, R.anim.popout_fast);
        animation.setStartOffset(300);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(LauncherActivity.this, MainActivity.class));
                finish();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        v[3].setAnimation(animation);


    }
}
