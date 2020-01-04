package com.VegaSolutions.lpptransit.ui.activities.forum;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.firebase.FirebaseManager;
import com.VegaSolutions.lpptransit.travanaserver.Objects.LiveUpdateMessage;
import com.VegaSolutions.lpptransit.travanaserver.Objects.MessageTag;
import com.VegaSolutions.lpptransit.travanaserver.TravanaAPI;
import com.VegaSolutions.lpptransit.ui.errorhandlers.CustomToast;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewMessageActivity extends AppCompatActivity {

    @BindView(R.id.new_message_back) ImageView back;
    @BindView(R.id.message_image_0) ImageView image0;
    @BindView(R.id.message_image_1) ImageView image1;
    @BindView(R.id.new_message_post) TextView post;
    @BindView(R.id.message_add_tags_btn) TextView add;
    @BindView(R.id.message_content) EditText messageContent;
    @BindView(R.id.message_tags) FlexboxLayout tags;

    private List<MessageTag> tagList = new ArrayList<>();


    private void setupUI() {

        back.setOnClickListener(v -> onBackPressed());
        post.setOnClickListener(v -> FirebaseManager.getFirebaseToken((data, error, success) -> {
            if (success) {

                if (messageContent.getText().length() < 3) {
                    runOnUiThread(() -> new CustomToast(this).showStringError(getString(R.string.msg_content_error)));
                    return;
                }

                // TODO: implement pictures (probably redundant).
                LiveUpdateMessage message = new LiveUpdateMessage(messageContent.getText().toString(), tagList.toArray(new MessageTag[0]), new String[0]);
                TravanaAPI.addMessage(data, message, (apiResponse, statusCode, success1) -> NewMessageActivity.this.runOnUiThread(() -> {
                    if (success1 && apiResponse.isSuccess()) {
                        NewMessageActivity.this.finish();
                    } else {
                        if (!success1)
                            new CustomToast(NewMessageActivity.this).showDefault(statusCode);
                        else new CustomToast(NewMessageActivity.this).showStringError(apiResponse.getInternal_error());
                    }
                }));
            }
        }));

        // To add a tag
        add.setOnClickListener(v -> {
            if (tagList.size() >= 3) {
                new CustomToast(NewMessageActivity.this).showStringError(getString(R.string.too_many_tags_error));
            } else NewMessageActivity.this.startActivityForResult(new Intent(NewMessageActivity.this, TagsActivity.class), 100);
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ViewGroupUtils.isDarkTheme(this) ? R.style.DarkTheme : R.style.WhiteTheme);
        setContentView(R.layout.activity_new_message);
        ButterKnife.bind(this);

        setupUI();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Add a tag to the tag list
        if (resultCode == TagsActivity.SELECTED) {

            MessageTag tag = data.getParcelableExtra("TAG");

            for (MessageTag tag1 : tagList)
                if (tag1.get_id().equals(tag.get_id())) return;

            tagList.add(tag);

            View v = getLayoutInflater().inflate(R.layout.template_tag_add, tags, false);
            TextView name = v.findViewById(R.id.tag_name);
            ImageView imageView = v.findViewById(R.id.tag_delete);
            View view = v.findViewById(R.id.tag_background);

            name.setText("#" + tag.getTag());
            view.getBackground().setTint(Color.parseColor(tag.getColor()));
            imageView.setOnClickListener(vi -> {
                tags.removeView(v);
                tagList.remove(tag);
            });

            tags.addView(v);

        }

    }
}
