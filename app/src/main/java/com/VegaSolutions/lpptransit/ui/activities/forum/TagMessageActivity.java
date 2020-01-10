package com.VegaSolutions.lpptransit.ui.activities.forum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.firebase.FirebaseCallback;
import com.VegaSolutions.lpptransit.firebase.FirebaseManager;
import com.VegaSolutions.lpptransit.travanaserver.Objects.LiveUpdateMessage;
import com.VegaSolutions.lpptransit.travanaserver.Objects.responses.ResponseObject;
import com.VegaSolutions.lpptransit.travanaserver.TravanaAPI;
import com.VegaSolutions.lpptransit.travanaserver.TravanaApiCallback;
import com.VegaSolutions.lpptransit.ui.errorhandlers.CustomToast;
import com.VegaSolutions.lpptransit.ui.fragments.forum.PostListFragment;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TagMessageActivity extends AppCompatActivity {

    public static final String TAG_ID = "tagId";
    public static final String TAG_NAME = "tagName";


    PostListFragment.Adapter adapter;

    @BindView(R.id.tag_message_list) RecyclerView rv;
    @BindView(R.id.header) FrameLayout header;
    @BindView(R.id.tag_title) TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ViewGroupUtils.isDarkTheme(this) ? R.style.DarkTheme : R.style.WhiteTheme);
        setContentView(R.layout.activity_tag_message);
        ButterKnife.bind(this);

        // Get tag name and id.
        String tagId = getIntent().getStringExtra(TAG_ID);
        String tagName = getIntent().getStringExtra(TAG_NAME);

        // Set message list and adapter.
        adapter = new PostListFragment.Adapter(this, rv);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                header.setSelected(recyclerView.canScrollVertically(-1));
            }
        });


        title.setText("#" + tagName);

        // Call the correct method if user is signed in.
        if (FirebaseManager.isSignedIn())
            FirebaseManager.getFirebaseToken((data, error, success) -> {
                if (success) {
                    TravanaAPI.messagesByTagMeta(data, tagId, (apiResponse, statusCode, success1) -> runOnUiThread(() -> {
                        if (success1 && apiResponse.isSuccess()) {
                            adapter.setMessages(apiResponse.getData());
                            adapter.notifyDataSetChanged();
                        } else {
                            if (!success1) new CustomToast(this).showDefault(statusCode);
                            else new CustomToast(this).showDefault(apiResponse.getResponse_code());
                        }
                    }));
                }
            });
        else TravanaAPI.messagesByTagMeta(tagId, (apiResponse, statusCode, success) -> runOnUiThread(() -> {
            if (success && apiResponse.isSuccess()) {
                adapter.setMessages(apiResponse.getData());
                adapter.notifyDataSetChanged();
            } else {
                if (!success) new CustomToast(this).showDefault(statusCode);
                else new CustomToast(this).showDefault(apiResponse.getResponse_code());
            }
        }));

    }
}
