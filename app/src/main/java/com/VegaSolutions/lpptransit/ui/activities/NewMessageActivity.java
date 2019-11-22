package com.VegaSolutions.lpptransit.ui.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.firebase.FirebaseCallback;
import com.VegaSolutions.lpptransit.firebase.FirebaseManager;
import com.VegaSolutions.lpptransit.travanaserver.Objects.LiveUpdateMessage;
import com.VegaSolutions.lpptransit.travanaserver.Objects.MessageTag;
import com.VegaSolutions.lpptransit.travanaserver.TravanaAPI;
import com.VegaSolutions.lpptransit.travanaserver.TravanaApiCallback;
import com.VegaSolutions.lpptransit.travanaserver.TravanaPOSTQuery;
import com.VegaSolutions.lpptransit.ui.errorhandlers.CustomToast;
import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewMessageActivity extends AppCompatActivity {

    private ImageView back, image0, image1;
    private TextView post, add;

    private EditText messageContent;
    private FlexboxLayout tags;
    private List<MessageTag> tagList = new ArrayList<>();


    private void setupUI() {

        back.setOnClickListener(v -> onBackPressed());
        post.setOnClickListener(v -> FirebaseManager.getFirebaseToken((data, error, success) -> {
            if (success) {
                FirebaseUser user = FirebaseManager.getSignedUser();
                // TODO: implement pictures
                LiveUpdateMessage message = new LiveUpdateMessage(user.getUid(), messageContent.getText().toString(), tagList.toArray(new MessageTag[0]), new String[0]);
                TravanaAPI.addMessage(data, message, (apiResponse, statusCode, success1) -> {
                    if (success1) {
                        Log.i("NewMessage", apiResponse.toString());
                        runOnUiThread(this::finish);
                    }
                    else {
                        runOnUiThread(() -> {
                            CustomToast customToast = new CustomToast(this);
                            customToast.showDefault(this, statusCode);
                        });
                        Log.e("NewMessage", statusCode + "");
                    }
                });
            }
        }));
        add.setOnClickListener(v -> {
            if (tagList.size() >= 3) {
                CustomToast customToast = new CustomToast(this);
                customToast.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
                customToast.setIconColor(Color.WHITE);
                customToast.setTextColor(Color.WHITE);
                customToast.setText(getString(R.string.too_many_tags_error));
                customToast.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_error_outline_black_24dp));
                customToast.show(Toast.LENGTH_SHORT);
            } else startActivityForResult(new Intent(this, TagsActivity.class), 100);
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);

        back = findViewById(R.id.new_message_back);
        add = findViewById(R.id.message_add_tags_btn);
        post = findViewById(R.id.new_message_post);
        messageContent = findViewById(R.id.message_content);
        tags = findViewById(R.id.message_tags);
        image0 = findViewById(R.id.message_image_0);
        image1 = findViewById(R.id.message_image_1);

        setupUI();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
