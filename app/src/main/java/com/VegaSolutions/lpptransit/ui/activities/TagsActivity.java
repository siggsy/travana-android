package com.VegaSolutions.lpptransit.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.firebase.FirebaseCallback;
import com.VegaSolutions.lpptransit.firebase.FirebaseManager;
import com.VegaSolutions.lpptransit.travanaserver.Objects.MessageTag;
import com.VegaSolutions.lpptransit.travanaserver.Objects.TagsBox;
import com.VegaSolutions.lpptransit.travanaserver.Objects.UserTag;
import com.VegaSolutions.lpptransit.travanaserver.TravanaAPI;
import com.VegaSolutions.lpptransit.travanaserver.TravanaApiCallback;
import com.VegaSolutions.lpptransit.travanaserver.TravanaQuery;
import com.VegaSolutions.lpptransit.ui.errorhandlers.CustomToast;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.common.util.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TagsActivity extends AppCompatActivity {

    public static final int SELECTED = 11;

    public static final int TYPE_NORMAL = 1;

    int type;

    private RecyclerView tags;
    private SearchView searchView;
    private View back;
    private Adapter adapter;

    private String filter = "";

    private void setupUI() {

        adapter = new Adapter();

        tags.setLayoutManager(new LinearLayoutManager(this));
        tags.setAdapter(adapter);

        back.setOnClickListener(v -> onBackPressed());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.applyFilter(query);
                filter = query;
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.applyFilter(newText);
                filter = newText;
                return true;
            }
        });

    }

    private void setTags(List<MessageTag> messageTags) {

        LayoutInflater inflater = getLayoutInflater();

        for (MessageTag tag : messageTags) {

            View v = inflater.inflate(R.layout.template_tag, tags, false);
            TextView name = v.findViewById(R.id.tag_text);
            name.setText("#" + tag.getTag());
            name.getBackground().setTint(Color.parseColor(tag.getColor()));
            name.setOnClickListener(vi -> {
                setResult(SELECTED, getIntent().putExtra("TAG", tag));
                finish();
            });
            tags.addView(v);

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ViewGroupUtils.isDarkTheme(this) ? R.style.DarkTheme : R.style.WhiteTheme);
        setContentView(R.layout.activity_tags);

        type = getIntent().getIntExtra("TYPE", 0);

        back = findViewById(R.id.search_activity_back);
        searchView = findViewById(R.id.search_activity_search);
        tags = findViewById(R.id.search_activity_rv);

        setupUI();

        if (type == TYPE_NORMAL) {
            TravanaAPI.tags((apiResponse, statusCode, success) -> {
                if (success) {

                    MessageTag[] main = apiResponse.getMain_tags();
                    MessageTag[] tags = apiResponse.getTags();
                    UserTag[] userTags = apiResponse.getUser_tags();

                    Log.i("tags", Arrays.toString(main));

                    Object[] allTags = new Object[main.length + tags.length + userTags.length];
                    System.arraycopy(userTags, 0, allTags, 0, userTags.length);
                    System.arraycopy(main, 0, allTags, userTags.length, main.length);
                    System.arraycopy(tags, 0, allTags, userTags.length + main.length, tags.length);

                    adapter.allTags = allTags;
                    runOnUiThread(() -> {
                        adapter.applyFilter(filter);
                        adapter.notifyDataSetChanged();
                    });
                }
            });
        } else {
            TravanaAPI.tags((apiResponse, statusCode, success) -> {
                if (success) {

                    MessageTag[] main = apiResponse.getMain_tags();
                    MessageTag[] tags = apiResponse.getTags();

                    Object[] allTags = new Object[main.length + tags.length];
                    System.arraycopy(main, 0, allTags, 0, main.length);
                    System.arraycopy(tags, 0, allTags, main.length, tags.length);

                    adapter.allTags = allTags;
                    runOnUiThread(() -> {
                        adapter.applyFilter(filter);
                        adapter.notifyDataSetChanged();
                    });
                }
            });
        }


    }

    class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Object[] tags = new Object[0];
        private Object[] allTags = new Object[0];

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(getLayoutInflater().inflate(R.layout.template_tag_search, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ViewHolder vh = (ViewHolder) holder;

            Object tag = tags[position];

            View.OnClickListener onClickListener;
            if (type == TYPE_NORMAL) {
               onClickListener = v -> {
                    startActivity(new Intent(TagsActivity.this, TagMessageActivity.class));
                };
            } else {
                onClickListener = v -> {
                    setResult(SELECTED, getIntent().putExtra("TAG", (MessageTag) tag));
                    finish();
                };
            }

            if (tag instanceof UserTag) {

                UserTag uTag = (UserTag) tag;

                vh.name.setText("#" + uTag.getTag());
                vh.name.setCompoundDrawables(null, null, ContextCompat.getDrawable(TagsActivity.this, R.drawable.ic_perm_identity_black_24dp), null);
                vh.name.getBackground().setTint(Color.parseColor(uTag.getColor()));
                vh.description.setText(uTag.getDescription_ang());
                vh.root.setOnClickListener(onClickListener);
                vh.following.setOnClickListener(v -> {
                    FirebaseManager.getFirebaseToken((data, error, success) -> {
                        if (success) {
                            TravanaAPI.followTag(data, uTag.get_id(), (apiResponse, statusCode, success1) -> runOnUiThread(() -> {
                                if (success1 && apiResponse.equals("Successful")) {
                                    CustomToast customToast = new CustomToast(TagsActivity.this);
                                    customToast.setBackgroundColor(ContextCompat.getColor(TagsActivity.this, R.color.colorPrimary));
                                    customToast.setIconColor(Color.WHITE);
                                    customToast.setTextColor(Color.WHITE);
                                    customToast.setText("Success!");
                                    customToast.setIcon(ContextCompat.getDrawable(TagsActivity.this, R.drawable.ic_check_black_24dp));
                                    customToast.show(Toast.LENGTH_SHORT);
                                }
                            }));
                        }
                    });

                });

            } else {

                MessageTag mTag = (MessageTag) tag;

                vh.name.setText("#" + mTag.getTag());
                vh.name.setCompoundDrawablesRelative(null, null, null, null);
                vh.name.getBackground().setTint(Color.parseColor(mTag.getColor()));
                vh.description.setText(mTag.getDescription_ang());
                vh.root.setOnClickListener(onClickListener);
                vh.following.setOnClickListener(v -> {
                    FirebaseManager.getFirebaseToken((data, error, success) -> {
                        if (success) {
                            TravanaAPI.followTag(data, mTag.get_id(), (apiResponse, statusCode, success1) -> runOnUiThread(() -> {
                                if (success1 && apiResponse.equals("Successful")) {
                                    CustomToast customToast = new CustomToast(TagsActivity.this);
                                    customToast.setBackgroundColor(ContextCompat.getColor(TagsActivity.this, R.color.colorPrimary));
                                    customToast.setIconColor(Color.WHITE);
                                    customToast.setTextColor(Color.WHITE);
                                    customToast.setText("Success!");
                                    customToast.setIcon(ContextCompat.getDrawable(TagsActivity.this, R.drawable.ic_check_black_24dp));
                                    customToast.show(Toast.LENGTH_SHORT);
                                }
                            }));
                        }
                    });

                });

            }

        }

        private void applyFilter(String text) {

            ArrayList<Object> items = new ArrayList<>();

            // Ignore all special Slovene characters and hashtags.
            text = text.toLowerCase().replace('č', 'c').replace('š', 's').replace('ž', 'z').replace("#", "");

            // Find an item and add to the list
            for(Object item : allTags) {
                if (item instanceof UserTag) {
                    UserTag uTag = (UserTag) item;
                    String itemName = uTag.getTag().toLowerCase().replace('č', 'c').replace('š', 's').replace('ž', 'z');
                    if (itemName.contains(text))
                        items.add(item);
                } else {
                    MessageTag uTag = (MessageTag) item;
                    String itemName = uTag.getTag().toLowerCase().replace('č', 'c').replace('š', 's').replace('ž', 'z');
                    if (itemName.contains(text))
                        items.add(item);
                }
            }
            tags = items.toArray();
            notifyDataSetChanged();

        }

        @Override
        public int getItemCount() {
            return tags.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView name, description, following;
            LinearLayout root;

            ViewHolder(@NonNull View itemView) {
                super(itemView);

                name = itemView.findViewById(R.id.tag_tv);
                description = itemView.findViewById(R.id.tag_desc_tv);
                following = itemView.findViewById(R.id.tag_following_btn);
                root = itemView.findViewById(R.id.tag_root);

            }
        }

    }

}
