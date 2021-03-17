package com.VegaSolutions.lpptransit.ui.activities.forum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.DetourInfo;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;

import java.util.ArrayList;
import java.util.List;

public class DetourActivity extends AppCompatActivity {

    public static final String TAG = "DetourActivity";

    DetoursAdapter detoursAdapter;
    RecyclerView rv;
    ImageView back;
    SwipeRefreshLayout refreshLayout;
    TextView whereIsDataFromTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ViewGroupUtils.isDarkTheme(this) ? R.style.DarkTheme : R.style.WhiteTheme);
        setContentView(R.layout.activity_detur);
        initComponents();
        initList();
        refreshLayout.setRefreshing(true);

        back.setOnClickListener(view -> {
            finish();
        });

        refreshLayout.setOnRefreshListener(
                () -> {
                    loadDetours();
                }
        );

        whereIsDataFromTextView.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.lpp.si/javni-prevoz/obvozi"));
            startActivity(browserIntent);
        });

        loadDetours();

    }

    private void loadDetours() {
        Api.getDetours((apiResponse, statusCode, success) -> {
            runOnUiThread(() -> {
                if (success) {
                    updateList(apiResponse.getData());
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_loading), Toast.LENGTH_LONG).show();
                }
                refreshLayout.setRefreshing(false);
            });
        });
    }

    private void initComponents() {
        rv = findViewById(R.id.rv_detours);
        back = findViewById(R.id.iv_back);
        refreshLayout = findViewById(R.id.swiperefresh);
        whereIsDataFromTextView = findViewById(R.id.tv_where_is_data_from);
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
            holder.tv_title.setText(list.get(position).getTitle());
            holder.tv_date.setText(list.get(position).getDate());
            holder.rl.setOnClickListener(view -> {

                String url = "https://www.lpp.si" + list.get(position).getMore_data_url();
                //String url = "https://www.lpp.si/obvoz/linije-lpp-na-obvozu-9";
                Log.e(TAG, url);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
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
            TextView tv_title;
            TextView tv_date;
            RelativeLayout rl;

            public ViewHolder(View v) {
                super(v);
                tv_title = v.findViewById(R.id.post_content);
                tv_date = v.findViewById(R.id.posted_time);
                rl = v.findViewById(R.id.rl_item);
            }
        }

    }
}