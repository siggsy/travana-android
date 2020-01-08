package com.VegaSolutions.lpptransit.ui.activities.forum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.firebase.FirebaseManager;
import com.VegaSolutions.lpptransit.travanaserver.Objects.LiveUpdateComment;
import com.VegaSolutions.lpptransit.travanaserver.Objects.LiveUpdateMessage;
import com.VegaSolutions.lpptransit.travanaserver.Objects.MessageTag;
import com.VegaSolutions.lpptransit.travanaserver.Objects.UserTag;
import com.VegaSolutions.lpptransit.travanaserver.Objects.responses.ResponseObject;
import com.VegaSolutions.lpptransit.travanaserver.TravanaAPI;
import com.VegaSolutions.lpptransit.travanaserver.TravanaApiCallback;
import com.VegaSolutions.lpptransit.ui.errorhandlers.CustomToast;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostActivity extends AppCompatActivity {

    public static final String MESSAGE_ID = "MESSAGE_ID";

    String id;

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
    @BindView(R.id.root) ConstraintLayout root;
    @BindView(R.id.header) ConstraintLayout header;

    private TravanaApiCallback<ResponseObject<LiveUpdateMessage>> callback = (apiResponse, statusCode, success) -> runOnUiThread(() -> {
        if (success && apiResponse.isSuccess()) {
            updateUI(apiResponse.getData());
        } else {
            if (!success)
                new CustomToast(this).showDefault(statusCode);
            else new CustomToast(this).showStringError(apiResponse.getInternal_error());
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ViewGroupUtils.isDarkTheme(this) ? R.style.DarkTheme : R.style.WhiteTheme);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);

        id = getIntent().getStringExtra(MESSAGE_ID);

        comments.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // Nothing.
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                header.setSelected(view.canScrollVertically(-1));
            }
        });
        comments.setVerticalScrollBarEnabled(false);

        if (FirebaseManager.isSignedIn()) FirebaseManager.getFirebaseToken((data, error, success) -> {
            if (success)
                TravanaAPI.messageid(id, data, (callback));
        });
        else TravanaAPI.messageid(id, callback);

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

        String[] photos = message.getPhotos_ids();
        if (photos != null && photos.length != 0) {
            pictureContainer.setVisibility(View.VISIBLE);
            pictureContainer.removeAllViews();
            for (String url : photos) {
                ImageView iv = new ImageView(this);
                pictures.addView(iv);
                TravanaAPI.getImage(url, (bitmap, statusCode, success) -> {
                    if (success)
                        iv.setImageBitmap(bitmap);
                    else new CustomToast(this).showDefault(statusCode);
                });
            }
        } else pictureContainer.setVisibility(View.GONE);

        // Set message tags
        MessageTag[] messageTags = message.getTags();
        if (messageTags.length != 0) {
            mTags.removeAllViews();
            for (MessageTag messageTag : messageTags) {
                TextView v = (TextView) getLayoutInflater().inflate(R.layout.template_tag, mTags, false);
                v.getBackground().setTint(Color.parseColor(messageTag.getColor()));
                v.setText("#" + messageTag.getTag());
                v.setClickable(true);
                v.setFocusable(true);
                v.setOnClickListener(v1 -> {
                    Intent i = new Intent(this, TagMessageActivity.class);
                    i.putExtra(TagMessageActivity.TAG_ID, messageTag.get_id());
                    i.putExtra(TagMessageActivity.TAG_NAME, messageTag.getTag());
                    startActivity(i);
                });
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
            newCommentPost.setOnClickListener(null);
            if (newComment.getText().length() == 0)
                return;
            newComment.setText("");
            FirebaseManager.getFirebaseToken((data, error, success) -> {
                if (!success)
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
                        newComment.setText("");
                        TravanaAPI.messageid(id, data, (apiResponse1, statusCode1, success2) -> runOnUiThread(() -> {
                            if (success2 && apiResponse1.isSuccess()) {
                                updateUI(apiResponse1.getData());
                            } else {
                                if (!success2)
                                    new CustomToast(this).showDefault(statusCode1);
                                else new CustomToast(this).showStringError(apiResponse1.getInternal_error());
                            }
                        }));
                    } else {
                        if (!success1)
                            new CustomToast(this).showDefault(statusCode);
                        else new CustomToast(this).showStringError(apiResponse.getInternal_error());
                    }
                }));
            });
        });


        // Load comments on the list
        Adapter adapter = new Adapter(this, message.comments.toArray(new LiveUpdateComment[0]));
        comments.setAdapter(adapter);

    }

    private class Adapter extends ArrayAdapter<LiveUpdateComment> {


        private Adapter(Context context, LiveUpdateComment[] comments) {
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
            if (comment.getUser().getTag() != null)
                tag.getBackground().setTint(Color.parseColor(comment.getUser().getTag().getColor()));
            else tag.getBackground().setTint(Color.TRANSPARENT);
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


            likeImage.setOnClickListener(v -> {

                if (!FirebaseManager.isSignedIn()) {
                    showSignIn();
                    return;
                }

                if (!comment.isLiked()) {
                    setLiked(true, comment, likeImage);
                } else {
                    setLiked(false, comment, likeImage);
                }
                FirebaseManager.getFirebaseToken((data, error, success) -> {
                    if (success) {
                        TravanaAPI.likeComment(data, comment.getComment_id(), comment.isLiked(), (apiResponse, statusCode, success1) -> {
                            Log.i("Liked",  apiResponse + " " + statusCode);
                            if (success1 && apiResponse.isSuccess()) {
                                runOnUiThread(() -> {
                                    CustomToast customToast = new CustomToast(getContext());
                                    customToast.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                                    customToast.setIconColor(Color.WHITE);
                                    customToast.setTextColor(Color.WHITE);
                                    customToast.setText("");
                                    customToast.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_check_black_24dp));
                                    customToast.show(Toast.LENGTH_SHORT);
                                    comment.setLikes(comment.isLiked() ? (comment.getLikes() + 1) : (comment.getLikes() - 1));
                                    likes.setText(comment.getLikes() + "");
                                });
                            } else {
                                runOnUiThread(() -> {
                                    if (!success1)
                                        new CustomToast(PostActivity.this).showDefault(statusCode);
                                    else new CustomToast(PostActivity.this).showStringError(apiResponse.getInternal_error());
                                    setLiked(!comment.isLiked(), comment, likeImage);
                                    likes.setText(comment.getLikes() + "");
                                });
                            }
                        });
                    }
                });
            });

            return convertView;

        }

        void setLiked(boolean value, LiveUpdateComment comment, ImageView likeContainer) {
            if (value) {
                likeContainer.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent));
                comment.setLiked(true);
            } else {
                int color = ViewGroupUtils.isDarkTheme(getContext()) ? Color.WHITE : Color.BLACK;
                likeContainer.setColorFilter(color);
                comment.setLiked(false);
            }
        }
    }

    private void showSignIn() {
        Snackbar snack = Snackbar
                .make(root, R.string.sign_in_alert, BaseTransientBottomBar.LENGTH_LONG)
                .setAction(R.string.sign_in_text, v -> startActivity(new Intent(this, SignInActivity.class)));
        View view = snack.getView();
        TextView tv = view.findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snack.show();
    }

}
