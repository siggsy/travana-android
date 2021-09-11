package com.VegaSolutions.lpptransit.ui.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;

public class AboutActivity extends AppCompatActivity {

    TextView tvVersion;
    TextView tvMail;
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);
        tvMail = findViewById(R.id.mail_tv);
        ivBack = findViewById(R.id.iv_back);
        tvVersion = findViewById(R.id.tv_version);

        tvMail.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "info@travana.si", null));
            startActivity(intent);
        });

        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            String versionName = pInfo.versionName;
            tvVersion.setText(getResources().getString(R.string.version) + " " + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            tvVersion.setVisibility(View.INVISIBLE);
        }

        ivBack.setOnClickListener(view -> finish());
    }
}
