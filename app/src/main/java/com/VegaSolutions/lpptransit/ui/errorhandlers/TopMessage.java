package com.VegaSolutions.lpptransit.ui.errorhandlers;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.BlendModeColorFilterCompat;
import androidx.core.graphics.BlendModeCompat;

import com.VegaSolutions.lpptransit.R;

public class TopMessage extends ConstraintLayout {

    TextView error_msg, loading_text;
    ProgressBar progress_bar;
    ImageView error_icon;
    View msg_background, refresh_background, msg_container, loading;
    ConstraintLayout layout;


    public TopMessage(Context context) {
        super(context);
        init();
    }

    public TopMessage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {

        LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = (ConstraintLayout) li.inflate(R.layout.template_loading_msg, this, true);

        error_msg = layout.findViewById(R.id.msg_text);
        error_icon = layout.findViewById(R.id.msg_icon);
        msg_background = layout.findViewById(R.id.msg_background);
        refresh_background = layout.findViewById(R.id.refresh_button_background);
        msg_container = layout.findViewById(R.id.msg_container);
        loading = layout.findViewById(R.id.loading);
        progress_bar = layout.findViewById(R.id.progress_bar);
        loading_text = layout.findViewById(R.id.loading_text);

    }

    public void setErrorMsg(String text) {
        error_msg.setText(text);
    }
    public void setErrorMsgColor(int color) {
        error_msg.setTextColor(color);
    }
    public void setErrorIcon(Drawable drawable) {
        error_icon.setImageDrawable(drawable);
    }
    public void setErrorIconColor(int color) {
        error_icon.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);
    }
    public void setLoadingTextColor(int color) {
        loading_text.setTextColor(color);
    }
    public void setProgressBarColor(int color) {

        progress_bar.getIndeterminateDrawable().setColorFilter(
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(color, BlendModeCompat.SRC_IN));
    }
    public void setErrorMsgBackgroundColor(int color) {
        msg_background.getBackground().setTint(color);
    }
    public void setRefreshBackgroundColor(int color) {
        refresh_background.getBackground().setTint(color);
    }
    public void setRefreshClickEvent(View.OnClickListener onClickListener) {
        refresh_background.setOnClickListener(onClickListener);
    }


    public void showLoading(boolean value) {
        msg_container.setVisibility(GONE);
        loading.setVisibility(value ? VISIBLE : GONE);
        show(msg_container, false);
        show(loading, value);
    }

    public void showMsg(String msg, Drawable icon) {
        setErrorMsg(msg);
        setErrorIcon(icon);
        msg_container.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
        show(msg_container, true);
        show(loading, false);
    }

    public void showMsgDefault(Context context, int statusCode) {
        switch (statusCode) {
            case -2:
                showMsg(context.getString(R.string.error_loading), ContextCompat.getDrawable(context, R.drawable.ic_error_outline));
                break;
            case -1:
                showMsg(context.getString(R.string.no_internet_connection), ContextCompat.getDrawable(context, R.drawable.ic_no_wifi));
                break;
            default:
                showMsg(context.getString(R.string.unknown_error, statusCode), ContextCompat.getDrawable(context, R.drawable.ic_error_outline));
                break;
        }
    }


    private void show(View view, boolean value) {

        ObjectAnimator objectAnimator;
        if (value) {
            objectAnimator = ObjectAnimator.ofFloat(view, "alpha", 0, 1);
        } else {
            objectAnimator = ObjectAnimator.ofFloat(view, "alpha", 1, 0);
        }
        objectAnimator.setDuration(200);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.start();
    }

}
