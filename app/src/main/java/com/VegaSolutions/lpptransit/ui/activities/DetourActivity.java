package com.VegaSolutions.lpptransit.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.TravanaApp;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.DetourInfo;
import com.VegaSolutions.lpptransit.utility.Constants;
import com.VegaSolutions.lpptransit.utility.NetworkConnectivityManager;
import com.VegaSolutions.lpptransit.utility.ScreenState;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;

import java.util.List;

import static com.VegaSolutions.lpptransit.utility.ScreenState.DONE;
import static com.VegaSolutions.lpptransit.utility.ScreenState.ERROR;
import static com.VegaSolutions.lpptransit.utility.ScreenState.LOADING;

public class DetourActivity extends AppCompatActivity {

    public static final String TAG = "DetourActivity";

    DetoursAdapter detoursAdapter;
    RecyclerView rv;
    ImageView back;
    TextView whereIsDataFromTextView;
    ProgressBar progressBar;
    LinearLayout errorContainer;
    TextView errorText;
    ImageView errorImageView;
    TextView tryAgainText;
    TextView noDetoursText;

    private TravanaApp app;
    private NetworkConnectivityManager networkConnectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ViewGroupUtils.isDarkTheme(this) ? R.style.DarkTheme : R.style.WhiteTheme);
        setContentView(R.layout.activity_detur);
        initElements();
        initList();

        app = TravanaApp.getInstance();
        networkConnectivityManager = app.getNetworkConnectivityManager();

        retrieveDetours();

    }

    private void retrieveDetours() {

        if (!networkConnectivityManager.isConnectionAvailable()) {
            setupUi(ERROR);
            setErrorUi(this.getResources().getString(R.string.no_internet_connection), R.drawable.ic_wifi);
            return;
        }
        setupUi(LOADING);
        Api.getDetours((apiResponse, statusCode, success) -> {
            runOnUiThread(() -> {
                if (success) {
                    updateList(apiResponse.getData());
                    setupUi(DONE);
                } else {
                    setupUi(ERROR);
                    setErrorUi(this.getResources().getString(R.string.error_loading), R.drawable.ic_error_outline);
                }
            });
        });
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
                    this.progressBar.setVisibility(View.GONE);
                    this.rv.setVisibility(View.VISIBLE);
                    this.errorContainer.setVisibility(View.GONE);
                    break;
                }
                case LOADING: {
                    this.progressBar.setVisibility(View.VISIBLE);
                    this.rv.setVisibility(View.GONE);
                    this.errorContainer.setVisibility(View.GONE);
                    break;
                }
                case ERROR: {
                    this.progressBar.setVisibility(View.GONE);
                    this.rv.setVisibility(View.GONE);
                    this.errorContainer.setVisibility(View.VISIBLE);
                    break;
                }
            }
        });
    }

    private void initElements() {
        rv = findViewById(R.id.rv_detours);
        back = findViewById(R.id.iv_back);
        progressBar = findViewById(R.id.progress_bar);
        whereIsDataFromTextView = findViewById(R.id.tv_where_is_data_from);
        errorText = findViewById(R.id.tv_error);
        errorImageView = findViewById(R.id.iv_error);
        tryAgainText = findViewById(R.id.tv_try_again);
        errorContainer = findViewById(R.id.ll_error_container);
        noDetoursText = findViewById(R.id.tv_no_detours);

        back.setOnClickListener(view -> {
            finish();
        });

        whereIsDataFromTextView.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.lpp.si/javni-prevoz/obvozi"));
            startActivity(browserIntent);
        });

        tryAgainText.setOnClickListener(view -> {
            retrieveDetours();
        });
    }

    private void initList() {
        rv.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);

        detoursAdapter = new DetoursAdapter(null);
        rv.setAdapter(detoursAdapter);
    }

    private void updateList(List<DetourInfo> list) {
        detoursAdapter.list = list;
        detoursAdapter.notifyDataSetChanged();

        if (list.size() == 0) {
            noDetoursText.setVisibility(View.VISIBLE);
        } else {
            noDetoursText.setVisibility(View.GONE);
        }
    }

    public class DetoursAdapter extends RecyclerView.Adapter<DetoursAdapter.ViewHolder> {

        public static final String TAG = "DetoursAdapter";

        public List<DetourInfo> list;

        public DetoursAdapter(List<DetourInfo> list) {
            this.list = list;
        }

        @Override
        public DetoursAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detours_rv_item, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.tvTitle.setText(list.get(position).getTitle());
            holder.tvDate.setText(list.get(position).getDate());
            holder.rl.setOnClickListener(view -> {

                Intent i = new Intent(getApplicationContext(), WebViewActivity.class);
                String url = "https://www.lpp.si" + list.get(position).getMore_data_url();
                i.putExtra(Constants.LINK_KEY, url);
                startActivity(i);

            });
        }

        @Override
        public int getItemCount() {

            if (list == null)
                return 0;

            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle;
            TextView tvDate;
            RelativeLayout rl;

            public ViewHolder(View v) {
                super(v);
                tvTitle = v.findViewById(R.id.post_content);
                tvDate = v.findViewById(R.id.posted_time);
                rl = v.findViewById(R.id.rl_item);
            }
        }

    }
}