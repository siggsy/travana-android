package com.VegaSolutions.lpptransit.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.website_tv) TextView website;
    @BindView(R.id.mail_tv) TextView mail;
    @BindView(R.id.terms_tv) TextView terms;
    @BindView(R.id.privacy_tv) TextView privacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ViewGroupUtils.isDarkTheme(this) ? R.style.DarkTheme : R.style.WhiteTheme);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        website.setOnClickListener(v -> {
            String url = "https://travana.si";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        mail.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "info@travana.si", null));
            startActivity(intent);
        });

        privacy.setOnClickListener(v -> {
            String url = "https://travana.si/politika_zasebnosti";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        terms.setOnClickListener(v -> {
            String url = "https://travana.si/pogoji_uporabe";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

    }
}
