package com.VegaSolutions.lpptransit.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.VegaSolutions.lpptransit.R;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launcher);

        View[] v = new View[4];
        v[0] = findViewById(R.id.v1);
        v[1] = findViewById(R.id.v2);
        v[2] = findViewById(R.id.v3);
        v[3] = findViewById(R.id.v4);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.popout_fast);
        animation.setStartOffset(0);
        v[0].setAnimation(animation);

        animation = AnimationUtils.loadAnimation(this, R.anim.popout_fast);
        animation.setStartOffset(250);
        v[1].setAnimation(animation);

        animation = AnimationUtils.loadAnimation(this, R.anim.popout_fast);
        animation.setStartOffset(500);
        v[2].setAnimation(animation);

        animation = AnimationUtils.loadAnimation(this, R.anim.popout_fast);
        animation.setStartOffset(750);
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
