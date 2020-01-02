package com.VegaSolutions.lpptransit.ui.fragments.forum;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.VegaSolutions.lpptransit.travanaserver.Objects.responses.ResponseObject;
import com.VegaSolutions.lpptransit.travanaserver.TravanaAPI;
import com.VegaSolutions.lpptransit.travanaserver.TravanaApiCallback;
import com.VegaSolutions.lpptransit.ui.activities.forum.PostActivity;
import com.VegaSolutions.lpptransit.ui.activities.forum.SignInActivity;
import com.VegaSolutions.lpptransit.ui.errorhandlers.CustomToast;
import com.VegaSolutions.lpptransit.ui.fragments.FragmentHeaderCallback;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class PostListFragment extends Fragment {


    // Available parameters
    public static final int TYPE_FOLLOWING = 0;
    public static final int TYPE_ALL = 1;

    private static final String TYPE = "type";

    // Parameter
    private int type;

    // UI elements
    @BindView(R.id.post_list_rv) RecyclerView rv;
    @BindView(R.id.refresh_layout) SwipeRefreshLayout refreshLayout;
    @BindView(R.id.sign_in_text) TextView signInText;
    @BindView(R.id.sign_in_btn) TextView signInBtn;

    private Adapter adapter;
    private FragmentHeaderCallback fragmentHeaderCallback;

    private TravanaApiCallback<ResponseObject<LiveUpdateMessage[]>> callback = (apiResponse, statusCode, success1) -> {
        Activity a = getActivity();
        if (a == null)
            return;
        a.runOnUiThread(() -> {
            refreshLayout.setRefreshing(false);
            if (success1 && apiResponse.isSuccess()) {
                if (apiResponse.getData() == null)
                    adapter.setMessages(new LiveUpdateMessage[0]);
                else {
                    adapter.setMessages(apiResponse.getData());
                    signInText.setVisibility(View.GONE);
                    signInBtn.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            } else {
                if (!success1)
                    new CustomToast(a).showDefault(statusCode);
                else new CustomToast(a).showStringError(apiResponse.getInternal_error());
            }
        });

    };

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
    public void onDetach() {
        super.onDetach();
        fragmentHeaderCallback = null;
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

        ButterKnife.bind(this, root);

        adapter = new Adapter(getActivity(), rv);
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
        refreshLayout.setOnRefreshListener(() -> FirebaseManager.getFirebaseToken((data, error, success) -> {
            if (type == TYPE_ALL)
                 TravanaAPI.messagesMeta(data, callback);
            else TravanaAPI.followedMessagesMeta(data, callback);

        }));

        signInText.setVisibility(View.GONE);
        signInBtn.setVisibility(View.GONE);
        if (FirebaseManager.isSignedIn()) {
            FirebaseManager.getFirebaseToken((data, error, success) -> {
                if (type == TYPE_ALL)
                    TravanaAPI.messagesMeta(data, callback);
                else TravanaAPI.followedMessagesMeta(data, callback);
            });
        } else {
            if (type == TYPE_ALL)
                TravanaAPI.messagesMeta(callback);
            else {
                refreshLayout.setRefreshing(false);
                signInText.setVisibility(View.VISIBLE);
                signInBtn.setVisibility(View.VISIBLE);
                signInBtn.setOnClickListener(v -> startActivity(new Intent(getContext(), SignInActivity.class)));
            }
        }

        return root;
    }

    private void showSignIn() {
        Snackbar snack = Snackbar
                .make(rv, R.string.sign_in_alert, BaseTransientBottomBar.LENGTH_LONG)
                .setAction(R.string.sign_in_text, v -> startActivity(new Intent(getContext(), SignInActivity.class)));
        View view = snack.getView();
        TextView tv = view.findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snack.show();
    }

    public static class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private LiveUpdateMessage[] messages = new LiveUpdateMessage[0];
        private Activity context;
        private RecyclerView rv;

        public Adapter(Activity context, RecyclerView rv) {
            this.context = context;
            this.rv = rv;
        }

        public void setMessages(LiveUpdateMessage[] messages) {
            this.messages = messages;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(context.getLayoutInflater().inflate(R.layout.template_forum_post, parent, false));
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
                    viewHolder.userTag.getBackground().setTint(Color.parseColor(message.getUser().getTag().getColor()));
                } else {
                    viewHolder.userTag.setVisibility(View.GONE);
                }
            }

            viewHolder.postLikes.setText(String.valueOf(message.getLikes()));
            viewHolder.postComments.setText(context.getString(R.string.post_comments, message.getComments_int()));
            viewHolder.postTime.setText(context.getString(R.string.posted_time, message.getTime_ago()));
            viewHolder.postContent.setText(message.getMessage_content());

            viewHolder.postTags.removeAllViews();
            if (message.getTags() != null)
                for (MessageTag tag : message.getTags()) {
                    TextView v = (TextView) context.getLayoutInflater().inflate(R.layout.template_tag, viewHolder.postTags, false);
                    v.getBackground().setTint(Color.parseColor(tag.getColor()));
                    v.setText("#" + tag.getTag());
                    viewHolder.postTags.addView(v);
                }

            viewHolder.setLiked(message.isLiked(), message);

            viewHolder.likeContainer.setOnClickListener(v -> {

                if (!FirebaseManager.isSignedIn()) {
                    showSignIn();
                    return;
                }

                viewHolder.setLiked(!message.isLiked(), message);
                FirebaseManager.getFirebaseToken((data, error, success) -> {
                    if (success) {
                        TravanaAPI.messagesLike(data, message.get_id(), message.isLiked(), (apiResponse, statusCode, success1) -> {
                            Log.i("Liked",  apiResponse + " " + statusCode);
                            Activity activity = context;
                            if (activity == null)
                                return;
                            if (success1 && apiResponse.isSuccess()) {
                                activity.runOnUiThread(() -> {
                                    new CustomToast(context)
                                            .setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
                                            .setIconColor(Color.WHITE)
                                            .setTextColor(Color.WHITE)
                                            .setText("")
                                            .setIcon(ContextCompat.getDrawable(context, R.drawable.ic_check_black_24dp))
                                            .show(Toast.LENGTH_SHORT);
                                    message.setLikes(message.isLiked() ? (message.getLikes() + 1) : (message.getLikes() - 1));
                                    viewHolder.postLikes.setText(message.getLikes() + "");
                                });
                            } else {
                                activity.runOnUiThread(() -> {
                                    if (!success1)
                                        new CustomToast(activity).showDefault(statusCode);
                                    else new CustomToast(activity).showStringError(apiResponse.getInternal_error());
                                    viewHolder.setLiked(!message.isLiked(), message);
                                    viewHolder.postLikes.setText(message.getLikes() + "");
                                });
                            }
                        });
                    }
                });
            });

            TravanaAPI.getUserImage(message.getUser().getUser_photo_url(), (bitmap, statusCode, success) -> {
                context.runOnUiThread(() -> {
                    if (success)
                        viewHolder.userImage.setImageBitmap(bitmap);
                    else new CustomToast(context).showDefault(statusCode);
                });
            });

            viewHolder.postRoot.setOnClickListener(v -> {
                Intent i = new Intent(context, PostActivity.class);
                i.putExtra(PostActivity.MESSAGE_ID, message.get_id());
                context.startActivity(i);
            });

        }

        @Override
        public int getItemCount() {
            return messages.length;
        }

        private void showSignIn() {
            Snackbar snack = Snackbar
                    .make(rv, R.string.sign_in_alert, BaseTransientBottomBar.LENGTH_LONG)
                    .setAction(R.string.sign_in_text, v -> context.startActivity(new Intent(context, SignInActivity.class)));
            View view = snack.getView();
            TextView tv = view.findViewById(com.google.android.material.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snack.show();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView userName, userTag, postContent, postLikes, postComments, postTime;
            private CircleImageView userImage;
            private ImageView likeImage;
            private FlexboxLayout postTags;
            private View likeContainer;
            private View postRoot;

            ViewHolder(@NonNull View itemView) {
                super(itemView);

                userName = itemView.findViewById(R.id.user_name);
                userTag = itemView.findViewById(R.id.user_tag);
                postContent = itemView.findViewById(R.id.post_content);
                postLikes = itemView.findViewById(R.id.post_likes);
                postComments = itemView.findViewById(R.id.post_replies);
                postTime = itemView.findViewById(R.id.posted_time);
                userImage = itemView.findViewById(R.id.user_image);
                postTags = itemView.findViewById(R.id.post_tags);
                likeContainer = itemView.findViewById(R.id.post_like_container);
                likeImage = itemView.findViewById(R.id.post_like_image);
                postRoot = itemView.findViewById(R.id.post_root);

            }


            private void setLiked(boolean value, LiveUpdateMessage message) {
                if (value) {
                    likeContainer.setBackground(ContextCompat.getDrawable(context, R.drawable.stretched_circle));
                    likeContainer.getBackground().setTint(ContextCompat.getColor(context, R.color.colorAccent));
                    postLikes.setTextColor(Color.WHITE);
                    likeImage.setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);
                    message.setLiked(true);

                } else {
                    int color = ViewGroupUtils.isDarkTheme(context) ? Color.WHITE : Color.BLACK;
                    likeContainer.setBackground(ContextCompat.getDrawable(context, R.drawable.like_container));
                    postLikes.setTextColor(color);
                    likeImage.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);
                    message.setLiked(false);
                }
            }


        }

    }


}
