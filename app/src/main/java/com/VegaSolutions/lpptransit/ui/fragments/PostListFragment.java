package com.VegaSolutions.lpptransit.ui.fragments;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.joda.time.DateTime;
import org.joda.time.Hours;

import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostListFragment extends Fragment {


    public static final int TYPE_FOLLOWING = 1;
    public static final int TYPE_ALL = 0;

    private static final String TYPE = "type";

    private int type;


    public static PostListFragment newInstance(int type) {
        PostListFragment fragment = new PostListFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(TYPE);
        }
    }

    private RecyclerView rv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_post_list, container, false);

        Adapter adapter = new Adapter();
        rv = root.findViewById(R.id.post_list_rv);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));



        if (type == TYPE_ALL) {
            TravanaAPI.messagesMeta(FirebaseManager.getSignedUser().getUid(), (apiResponse, statusCode, success) -> {
                if (success) {

                    LiveUpdateMessage[] messages = (LiveUpdateMessage[]) apiResponse;
                    Log.i("message json", Arrays.toString(messages));
                    adapter.setMessages(messages);
                    getActivity().runOnUiThread(adapter::notifyDataSetChanged);
                }
            });
        } else {
            /*
            TravanaAPI.followedMessagesMeta(FirebaseManager.getSignedUser().getUid(), new String[0], (apiResponse, statusCode, success) -> {
                if (success) {
                    LiveUpdateMessage[] messages = (LiveUpdateMessage[]) apiResponse;
                    adapter.setMessages(messages);
                    getActivity().runOnUiThread(adapter::notifyDataSetChanged);
                }
            });

             */
        }

        return root;
    }

    public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private LiveUpdateMessage[] messages = new LiveUpdateMessage[0];

        public void setMessages(LiveUpdateMessage[] messages) {
            this.messages = messages;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(getLayoutInflater().inflate(R.layout.template_forum_post, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            LiveUpdateMessage message = messages[position];

            if (message.getUser() == null) {
                viewHolder.userName.setText("TEST");
                viewHolder.userTag.setVisibility(View.GONE);
            } else {
                viewHolder.userName.setText(message.getUser().getName());
                if (message.getUser().getTag() != null) {
                    viewHolder.userTag.setVisibility(View.VISIBLE);
                    viewHolder.userTag.setText(message.getUser().getTag().getTag());
                    viewHolder.userTag.getBackground().setTint(Color.parseColor(message.getUser().getTag().getTag_color()));
                } else {
                    viewHolder.userTag.setVisibility(View.GONE);
                }
            }


            viewHolder.postLikes.setText(String.valueOf(message.getLikes()));
            viewHolder.postComments.setText(getString(R.string.post_comments, message.getComments_int()));
            viewHolder.postTime.setText(getString(R.string.posted_time, Hours.hoursBetween(new DateTime(message.getCreated_time()), DateTime.now()).getHours()));
            viewHolder.postContent.setText(message.getMessage_content());

            viewHolder.postTags.removeAllViews();
            for (MessageTag tag : message.getTags()) {
                TextView v = (TextView) getLayoutInflater().inflate(R.layout.template_tag, viewHolder.postTags, false);
                v.getBackground().setTint(Color.parseColor(tag.getColor()));
                v.setText("#" + tag.getTag());
                viewHolder.postTags.addView(v);
            }

            viewHolder.setLiked(message.isLiked(), message);

            viewHolder.likeContainer.setOnClickListener(v -> {
                if (!message.isLiked()) {
                    viewHolder.setLiked(true, message);
                } else {
                    viewHolder.setLiked(false, message);
                }

                FirebaseManager.getFirebaseToken((data, error, success) -> {
                    if (success) {
                        TravanaAPI.messagesLike(data, message.get_id(), message.isLiked(), (apiResponse, statusCode, success1) -> {
                            Log.i("Liked",  apiResponse.toString() + " " + statusCode);
                            if (success1 && apiResponse.toString().equals("Successful")) {
                                ((Activity) getContext()).runOnUiThread(() -> {
                                    CustomToast customToast = new CustomToast(getContext());
                                    customToast.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                                    customToast.setIconColor(Color.WHITE);
                                    customToast.setTextColor(Color.WHITE);
                                    customToast.setText("Success!");
                                    customToast.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_check_black_24dp));
                                    customToast.show(Toast.LENGTH_SHORT);
                                    message.setLikes(message.isLiked() ? (message.getLikes() + 1) : (message.getLikes() - 1));
                                    viewHolder.postLikes.setText(message.getLikes() + "");
                                });
                            } else {
                                ((Activity) getContext()).runOnUiThread(() -> {
                                    CustomToast customToast = new CustomToast(getContext());
                                    customToast.showDefault(getContext(), statusCode);
                                    viewHolder.setLiked(!message.isLiked(), message);
                                    viewHolder.postLikes.setText(message.getLikes() + "");
                                });
                            }
                        });
                    }
                });

            });

        }

        @Override
        public int getItemCount() {
            return messages.length;
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            private TextView userName, userTag, postContent, postLikes, postComments, postTime;
            private CircleImageView userImage;
            private ImageView likeImage;
            private FlexboxLayout postTags;
            private View likeContainer;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                userName = itemView.findViewById(R.id.user_name);
                userTag = itemView.findViewById(R.id.user_tag);
                postContent = itemView.findViewById(R.id.post_content);
                postLikes = itemView.findViewById(R.id.post_likes);
                postComments = itemView.findViewById(R.id.post_comments);
                postTime = itemView.findViewById(R.id.posted_time);
                userImage = itemView.findViewById(R.id.user_image);
                postTags = itemView.findViewById(R.id.post_tags);
                likeContainer = itemView.findViewById(R.id.post_like_container);
                likeImage = itemView.findViewById(R.id.post_like_image);

            }


            private void setLiked(boolean value, LiveUpdateMessage message) {
                if (value) {
                    likeContainer.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.stretched_circle));
                    likeContainer.getBackground().setTint(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    postLikes.setTextColor(Color.WHITE);
                    likeImage.setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);
                    message.setLiked(true);

                } else {
                    int color = ViewGroupUtils.isDarkTheme(getContext()) ? Color.WHITE : Color.BLACK;
                    likeContainer.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.like_container));
                    postLikes.setTextColor(color);
                    likeImage.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);
                    message.setLiked(false);
                }
            }


        }

    }


}
