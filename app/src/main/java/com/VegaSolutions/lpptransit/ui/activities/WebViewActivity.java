package com.VegaSolutions.lpptransit.ui.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;

public class WebViewActivity extends AppCompatActivity {

    ImageView iv_back;
    WebView webView;
    TextView tv_link;
    ProgressBar pbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ViewGroupUtils.isDarkTheme(this) ? R.style.DarkTheme : R.style.WhiteTheme);
        setContentView(R.layout.activity_web_view);

        iv_back = findViewById(R.id.iv_back);
        webView = findViewById(R.id.webview);
        tv_link = findViewById(R.id.tv_link);
        pbar = findViewById(R.id.p_bar);

        iv_back.setOnClickListener(view -> {
            finish();
        });

        String link = getIntent().getStringExtra("LINK");
        tv_link.setText(link);
        webView.loadUrl(link);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                pbar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pbar.setVisibility(View.INVISIBLE);
            }
        });

    }
}