package com.VegaSolutions.lpptransit.ui.errorhandlers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;

public class CustomToast {

    private final Context context;

    private final View v;
    private final TextView textView;
    private final ImageView imageView;

    public CustomToast(Context context) {
        this.context = context;
        v = ((Activity) context).getLayoutInflater().inflate(R.layout.template_custom_toast, null);
        textView = v.findViewById(R.id.custom_toast_text);
        imageView = v.findViewById(R.id.custom_toast_icon);
    }

    public CustomToast setText(String text) {
        textView.setText(text);
        if (text.equals(""))
            textView.setVisibility(View.GONE);
        else textView.setVisibility(View.VISIBLE);
        return this;
    }

    public CustomToast setTextColor(int color) {
        textView.setTextColor(color);
        return this;
    }

    public CustomToast setIcon(Drawable drawable) {
        imageView.setImageDrawable(drawable);
        return this;
    }

    public CustomToast setIconColor(int color) {
        imageView.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);
        return this;
    }

    public CustomToast setBackgroundColor(int color) {
        v.getBackground().setTint(color);
        return this;
    }

    public void show(int duration) {
        Toast t = new Toast(context);
        t.setView(v);
        t.setDuration(duration);
        t.show();
    }

    public void showDefault(int statusCode) {
        setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        setTextColor(Color.WHITE);
        setIconColor(Color.WHITE);
        switch (statusCode) {
            case -3:
                setText(context.getString(R.string.connection_refused));
                setIcon(ContextCompat.getDrawable(context, R.drawable.ic_error_outline));
                break;
            case -2:
                setText(context.getString(R.string.timed_out_error));
                setIcon(ContextCompat.getDrawable(context, R.drawable.ic_error_outline));
                break;
            case -1:
                setText(context.getString(R.string.no_internet_connection));
                setIcon(ContextCompat.getDrawable(context, R.drawable.ic_no_wifi));
                break;
            default:
                setText(context.getString(R.string.unknown_error, statusCode));
                setIcon(ContextCompat.getDrawable(context, R.drawable.ic_error_outline));
                break;

        }
        show(Toast.LENGTH_SHORT);
    }

    public void showStringError(String error) {
        setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        setTextColor(Color.WHITE);
        setIconColor(Color.WHITE);
        setText(error);
        setIcon(ContextCompat.getDrawable(context, R.drawable.ic_error_outline));
        show(Toast.LENGTH_SHORT);
    }

    public void showCheck() {
        setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
        setIconColor(Color.WHITE);
        setTextColor(Color.WHITE);
        setText("");
        setIcon(ContextCompat.getDrawable(context, R.drawable.ic_check));
        show(Toast.LENGTH_SHORT);
    }

    public void showStringNormal(String message) {
        boolean isDark = ViewGroupUtils.isDarkTheme(context);
        setBackgroundColor(isDark ? Color.BLACK : Color.WHITE);
        setTextColor(isDark ? Color.WHITE : Color.BLACK);
        setIconColor(isDark ? Color.WHITE : Color.BLACK);
        setText(message);
        setIcon(ContextCompat.getDrawable(context, R.drawable.ic_info_outline));
        show(Toast.LENGTH_SHORT);
    }

}
