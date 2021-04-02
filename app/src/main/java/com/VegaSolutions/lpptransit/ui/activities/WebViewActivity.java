package com.VegaSolutions.lpptransit.ui.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.TravanaApp;
import com.VegaSolutions.lpptransit.utility.Constants;
import com.VegaSolutions.lpptransit.utility.NetworkConnectivityManager;
import com.VegaSolutions.lpptransit.utility.ScreenState;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;

import static com.VegaSolutions.lpptransit.utility.ScreenState.DONE;
import static com.VegaSolutions.lpptransit.utility.ScreenState.ERROR;
import static com.VegaSolutions.lpptransit.utility.ScreenState.LOADING;

public class WebViewActivity extends AppCompatActivity {

    ImageView ivBack;
    WebView webView;
    TextView tvLink;
    ProgressBar pBar;
    LinearLayout errorContainer;
    TextView errorText;
    ImageView errorImageView;
    TextView tryAgainText;

    private String link;
    private TravanaApp app;
    private NetworkConnectivityManager networkConnectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ViewGroupUtils.isDarkTheme(this) ? R.style.DarkTheme : R.style.WhiteTheme);
        setContentView(R.layout.activity_web_view);
        initElements();

        app = TravanaApp.getInstance();
        networkConnectivityManager = app.getNetworkConnectivityManager();

        link = getIntent().getStringExtra(Constants.LINK_KEY);
        loadPage(link);

        if (ViewGroupUtils.isDarkTheme(this) && WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            WebSettingsCompat.setForceDark(webView.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
        }
    }

    void setErrorUi(String errorName, int errorIconCode) {
        runOnUiThread(() -> {
            errorText.setText(errorName);
            errorImageView.setImageResource(errorIconCode);
        });
    }

    void setupUi(ScreenState screenState) {
        runOnUiThread(() -> {
            switch (screenState) {
                case DONE: {
                    this.pBar.setVisibility(View.GONE);
                    this.webView.setVisibility(View.VISIBLE);
                    this.errorContainer.setVisibility(View.GONE);
                    break;
                }
                case LOADING: {
                    this.pBar.setVisibility(View.VISIBLE);
                    this.webView.setVisibility(View.GONE);
                    this.errorContainer.setVisibility(View.GONE);
                    break;
                }
                case ERROR: {
                    this.pBar.setVisibility(View.GONE);
                    this.webView.setVisibility(View.GONE);
                    this.errorContainer.setVisibility(View.VISIBLE);
                    break;
                }
            }
        });
    }

    void loadPage(String link) {
        tvLink.setText(link);

        if (!networkConnectivityManager.isConnectionAvailable()) {
            setupUi(ERROR);
            setErrorUi(this.getResources().getString(R.string.no_internet_connection), R.drawable.ic_wifi);
            return;
        }
        webView.loadUrl(link);
    }

    void initElements() {
        ivBack = findViewById(R.id.iv_back);
        webView = findViewById(R.id.webview);
        tvLink = findViewById(R.id.tv_link);
        pBar = findViewById(R.id.progress_bar);
        errorText = findViewById(R.id.tv_error);
        errorImageView = findViewById(R.id.iv_error);
        tryAgainText = findViewById(R.id.tv_try_again);
        errorContainer = findViewById(R.id.ll_error_container);

        ivBack.setOnClickListener(view -> {
            finish();
        });

        tryAgainText.setOnClickListener(view -> {
            loadPage(link);
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                setupUi(LOADING);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                setupUi(DONE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                setupUi(ERROR);
                setErrorUi(getApplicationContext().getResources().getString(R.string.error_loading), R.drawable.ic_error_outline);
            }
        });
    }
}