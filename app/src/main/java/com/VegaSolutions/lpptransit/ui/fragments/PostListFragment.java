package com.VegaSolutions.lpptransit.ui.fragments;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.VegaSolutions.lpptransit.travanaserver.TravanaApiCallbackSpecial;
import com.VegaSolutions.lpptransit.ui.errorhandlers.CustomToast;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;
import com.google.android.flexbox.FlexboxLayout;

import org.joda.time.DateTime;
import org.joda.time.Hours;

import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostListFragment extends Fragment {


    // Available parameters
    public static final int TYPE_FOLLOWING = 1;
    public static final int TYPE_ALL = 0;

    private static final String TYPE = "type";

    private FragmentHeaderCallback fragmentHeaderCallback;


    // Parameter
    private int type;

    private RecyclerView rv;

    public static PostListFragment newInstance(int type) {
        PostListFragment fragment = new PostListFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentHeaderCallback)
            fragmentHeaderCallback = (FragmentHeaderCallback) context;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(TYPE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentHeaderCallback.onHeaderChanged(rv.canScrollVertically(-1));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_post_list, container, false);

        rv = root.findViewById(R.id.post_list_rv);
        SwipeRefreshLayout refreshLayout = root.findViewById(R.id.refresh_layout);

        Adapter adapter = new Adapter();
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                fragmentHeaderCallback.onHeaderChanged(recyclerView.canScrollVertically(-1));
            }
        });

        refreshLayout.setRefreshing(true);
        refreshLayout.setOnRefreshListener(() -> {
            if (type == TYPE_ALL) {
                TravanaAPI.messagesMeta(FirebaseManager.getSignedUser().getUid(), (apiResponse, statusCode, success) -> {
                    getActivity().runOnUiThread(() -> refreshLayout.setRefreshing(false));
                    if (success) {
                        Log.i("message json", Arrays.toString(apiResponse));
                        adapter.setMessages(apiResponse);
                        getActivity().runOnUiThread(adapter::notifyDataSetChanged);
                    }
                });
            } else {

            TravanaAPI.followedMessagesMeta(FirebaseManager.getSignedUser().getUid(), (apiResponse, statusCode, success) -> {
                getActivity().runOnUiThread(() -> refreshLayout.setRefreshing(false));
                if (success) {
                    adapter.setMessages((LiveUpdateMessage[]) apiResponse);
                    getActivity().runOnUiThread(adapter::notifyDataSetChanged);
                }
            });


            }
        });

        if (type == TYPE_ALL) {
            TravanaAPI.messagesMeta(FirebaseManager.getSignedUser().getUid(), (apiResponse, statusCode, success) -> {
                getActivity().runOnUiThread(() -> refreshLayout.setRefreshing(false));
                if (success) {
                    Log.i("message json", Arrays.toString(apiResponse));
                    adapter.setMessages(apiResponse);
                    getActivity().runOnUiThread(adapter::notifyDataSetChanged);
                }
            });
        } else {
            TravanaAPI.followedMessagesMeta(FirebaseManager.getSignedUser().getUid(), (apiResponse, statusCode, success) -> {
                getActivity().runOnUiThread(() -> refreshLayout.setRefreshing(false));
                if (success) {
                    LiveUpdateMessage[] messages = (LiveUpdateMessage[]) apiResponse;
                    adapter.setMessages(messages);
                    getActivity().runOnUiThread(adapter::notifyDataSetChanged);
                }
            });
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
                    viewHolder.userTag.setText("#" + message.getUser().getTag().getTag());
                    viewHolder.userTag.getBackground().setTint(Color.parseColor(message.getUser().getTag().getColor()));
                } else {
                    viewHolder.userTag.setVisibility(View.GONE);
                }
            }

            viewHolder.postLikes.setText(String.valueOf(message.getLikes()));
            viewHolder.postComments.setText(getString(R.string.post_comments, message.getComments_int()));
            viewHolder.postTime.setText(getString(R.string.posted_time, message.getTime_ago()));
            viewHolder.postContent.setText(message.getMessage_content());

            viewHolder.postTags.removeAllViews();
            if (message.getTags() != null)
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
                            Log.i("Liked",  apiResponse + " " + statusCode);
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

            TravanaAPI.getUserImage(message.getUser().getUser_photo_url(), (bitmap, statusCode, success) -> {
                getActivity().runOnUiThread(() -> {
                    if (success)
                        viewHolder.userImage.setImageBitmap(bitmap);
                    else new CustomToast(getContext()).showDefault(getActivity(), Toast.LENGTH_SHORT);
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
