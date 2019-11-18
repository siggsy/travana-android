package com.VegaSolutions.lpptransit.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.VegaSolutions.lpptransit.R;
import com.google.android.flexbox.FlexboxLayout;

public class NewMessageActivity extends AppCompatActivity {

    private ImageView back;
    private TextView post, add;

    private EditText messageContent;
    private FlexboxLayout tags;


    private void setupUI() {

        back.setOnClickListener(v -> onBackPressed());
        // TODO; send message.
        post.setOnClickListener(null);
        add.setOnClickListener(v -> {
            //TODO: create tag search activity.
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

        setupUI();

    }
}
