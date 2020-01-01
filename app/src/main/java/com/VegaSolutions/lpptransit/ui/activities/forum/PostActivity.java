package com.VegaSolutions.lpptransit.ui.activities.forum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.firebase.FirebaseCallback;
import com.VegaSolutions.lpptransit.firebase.FirebaseManager;
import com.VegaSolutions.lpptransit.travanaserver.Objects.LiveUpdateComment;
import com.VegaSolutions.lpptransit.travanaserver.Objects.LiveUpdateMessage;
import com.VegaSolutions.lpptransit.travanaserver.Objects.MessageTag;
import com.VegaSolutions.lpptransit.travanaserver.Objects.TagsBox;
import com.VegaSolutions.lpptransit.travanaserver.Objects.UserTag;
import com.VegaSolutions.lpptransit.travanaserver.Objects.responses.ResponseObjectCommit;
import com.VegaSolutions.lpptransit.travanaserver.TravanaAPI;
import com.VegaSolutions.lpptransit.travanaserver.TravanaApiCallback;
import com.VegaSolutions.lpptransit.travanaserver.TravanaApiCallbackSpecial;
import com.VegaSolutions.lpptransit.ui.custommaps.LatLngInterpolator;
import com.VegaSolutions.lpptransit.ui.errorhandlers.CustomToast;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;
import com.google.android.flexbox.FlexboxLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class PostActivity extends AppCompatActivity {

    public static final String MESSAGE_ID = "MESSAGE_ID";

    @BindView(R.id.user_tag) TextView tag;
    @BindView(R.id.user_name) TextView name;
    @BindView(R.id.user_image) ImageView userImage;
    @BindView(R.id.post_content) TextView content;
    @BindView(R.id.post_pictures_container) LinearLayout pictureContainer;
    @BindView(R.id.post_pictures) FlexboxLayout pictures;
    @BindView(R.id.post_tags) FlexboxLayout mTags;
    @BindView(R.id.post_comment_count) TextView commentCount;
    @BindView(R.id.new_comment) EditText newComment;
    @BindView(R.id.new_comment_post) ImageView newCommentPost;
    @BindView(R.id.post_replies) ListView comments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ViewGroupUtils.isDarkTheme(this) ? R.style.DarkTheme : R.style.WhiteTheme);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);

        String id = getIntent().getStringExtra(MESSAGE_ID);

        FirebaseManager.getFirebaseToken((data, error, success) -> {
            TravanaAPI.messageid(id, (apiResponse, statusCode, success1) -> runOnUiThread(() -> {
                if (success1 && apiResponse.getResponse_code() == 200) {
                    updateUI(apiResponse.getData());
                } else new CustomToast(this).showDefault(apiResponse != null ? apiResponse.getResponse_code() : statusCode);
            }));
        });



    }


    void updateUI(LiveUpdateMessage message) {


        // Set name
        name.setText(message.getUser().getName());

        // Set tag
        UserTag uTag = message.getUser().getTag();
        if (uTag != null && !uTag.getTag().equals("")) {
            tag.setVisibility(View.VISIBLE);
            tag.setText(uTag.getTag());
            tag.getBackground().setTint(Color.parseColor(uTag.getColor()));
        } else tag.setVisibility(View.GONE);

        // Set content
        content.setText(message.getMessage_content());

        // Set photos
        String[] photos = message.getPhoto_ids();
        if (photos != null && photos.length != 0) {
            pictureContainer.setVisibility(View.VISIBLE);
            for (String url : photos) {
                ImageView iv = new ImageView(this);
                pictures.addView(iv);
                TravanaAPI.getImage(url, (bitmap, statusCode, success) -> {
                    if (success)
                        iv.setImageBitmap(bitmap);
                });
            }
        } else pictureContainer.setVisibility(View.GONE);

        // Set message tags
        MessageTag[] messageTags = message.getTags();
        if (messageTags.length != 0) {
            for (MessageTag messageTag : messageTags) {
                TextView v = (TextView) getLayoutInflater().inflate(R.layout.template_tag, mTags, false);
                v.getBackground().setTint(Color.parseColor(messageTag.getColor()));
                v.setText("#" + messageTag.getTag());
                mTags.addView(v);
            }
        }

        commentCount.setText(getString(R.string.post_comments, message.comments.size()));

        // Load user image
        TravanaAPI.getUserImage(message.getUser().getUser_photo_url(), (bitmap, statusCode, success) -> runOnUiThread(() -> {
             if (success)
                 userImage.setImageBitmap(bitmap);
             else new CustomToast(this).showDefault(statusCode);
         }));

        // Comment on the post
        newCommentPost.setOnClickListener(v -> {
            if (newComment.getText().length() == 0)
                return;
            FirebaseManager.getFirebaseToken((data, error, success) -> {
                if (error != null)
                    return;
                TravanaAPI.addComment(data, message.get_id(), new LiveUpdateComment(newComment.getText().toString()), (apiResponse, statusCode, success1) -> runOnUiThread(() -> {
                    if (success1 && apiResponse.isSuccess()) {
                        CustomToast customToast = new CustomToast(this);
                        customToast.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
                        customToast.setIconColor(Color.WHITE);
                        customToast.setTextColor(Color.WHITE);
                        customToast.setText("");
                        customToast.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_check_black_24dp));
                        customToast.show(Toast.LENGTH_SHORT);
                    } else new CustomToast(this).showDefault(statusCode);
                }));
            });
        });


        // Load comments on the list
        Adapter adapter = new Adapter(this, message.comments.toArray(new LiveUpdateComment[0]));
        comments.setAdapter(adapter);

    }

    private class Adapter extends ArrayAdapter<LiveUpdateComment> {


        public Adapter(Context context, LiveUpdateComment[] comments) {
            super(context, 0, comments);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LiveUpdateComment comment = getItem(position);

            if (convertView == null)
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.template_comment, parent, false);

            TextView name = convertView.findViewById(R.id.user_name);
            TextView tag = convertView.findViewById(R.id.user_tag);
            TextView timeAgo = convertView.findViewById(R.id.posted_time);
            TextView content = convertView.findViewById(R.id.post_content);
            TextView replies = convertView.findViewById(R.id.post_replies);
            TextView likes = convertView.findViewById(R.id.post_likes);
            ImageView likeImage = convertView.findViewById(R.id.post_like_image);

            name.setText(comment.getUser().getName());
            tag.getBackground().setTint(Color.parseColor(comment.getUser().getTag().getColor()));
            timeAgo.setText(comment.getTime_ago());
            content.setText(comment.getComment_content());
            if (comment.getSubcomments() != null) {
                replies.setText(getString(R.string.comment_replies, comment.getSubcomments().length));
                if (comment.getSubcomments().length == 0)
                    replies.setVisibility(View.VISIBLE);
                else replies.setVisibility(View.GONE);
            } else replies.setVisibility(View.GONE);
            likes.setText("" + comment.getLikes());
            likeImage.setColorFilter(comment.isLiked() ? ContextCompat.getColor(PostActivity.this, R.color.colorAccent) : ViewGroupUtils.isDarkTheme(PostActivity.this) ? Color.WHITE : Color.BLACK);
            return convertView;

        }
    }

}
