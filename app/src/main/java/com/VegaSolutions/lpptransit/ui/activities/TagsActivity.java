package com.VegaSolutions.lpptransit.ui.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.travanaserver.Objects.MessageTag;
import com.VegaSolutions.lpptransit.travanaserver.TravanaAPI;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TagsActivity extends AppCompatActivity {

    public static final int SELECTED = 11;

    private FlexboxLayout tags;
    private SearchView searchView;
    private View back;

    private List<MessageTag> tagList = new ArrayList<>();
    private String filter = "";

    private void setupUI() {

        back.setOnClickListener(v -> onBackPressed());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                applyFilter(query);
                filter = query;
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                applyFilter(newText);
                filter = newText;
                return true;
            }
        });

    }

    private void applyFilter(String text) {

        if(text.isEmpty()) {
            tags.removeAllViews();
        } else {
            ArrayList<MessageTag> items = new ArrayList<>();

            // Ignore all special Slovene characters and hashtags.
            text = text.toLowerCase().replace('č', 'c').replace('š', 's').replace('ž', 'z').replace("#", "");

            // Find an item and add to the list
            for(MessageTag item : tagList) {
                String itemName = item.getTag().toLowerCase().replace('č', 'c').replace('š', 's').replace('ž', 'z');
                if (itemName.contains(text))
                    items.add(item);
            }
            setTags(items);
        }

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
        setContentView(R.layout.activity_tags);

        back = findViewById(R.id.search_activity_back);
        searchView = findViewById(R.id.search_activity_search);
        tags = findViewById(R.id.tags_fb);

        setupUI();

        TravanaAPI.tags((apiResponse, statusCode, success) -> {
            if (success) {
                Log.i("TagsActivity", Arrays.toString(apiResponse));
                tagList.addAll(Arrays.asList(apiResponse));
                runOnUiThread(() -> applyFilter(filter));
            }
        });

    }
}
