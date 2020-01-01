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

public class CustomToast {

    private Context context;

    private View v;
    private TextView textView;
    private ImageView imageView;

    public CustomToast(Context context) {
        this.context = context;
        v = ((Activity)context).getLayoutInflater().inflate(R.layout.template_custom_toast, null);
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
                setIcon(ContextCompat.getDrawable(context, R.drawable.ic_error_outline_black_24dp));
                break;
            case -2:
                setText(context.getString(R.string.timed_out_error));
                setIcon(ContextCompat.getDrawable(context, R.drawable.ic_error_outline_black_24dp));
                break;
            case -1:
                setText(context.getString(R.string.network_error));
                setIcon(ContextCompat.getDrawable(context, R.drawable.ic_wifi_off_24px));
                break;
            default:
                setText(context.getString(R.string.unknown_error, statusCode));
                setIcon(ContextCompat.getDrawable(context, R.drawable.ic_error_outline_black_24dp));
                break;

        }
        show(Toast.LENGTH_SHORT);
    }

    public void showStringError(String error) {
        setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        setTextColor(Color.WHITE);
        setIconColor(Color.WHITE);
        setText(error);
        setIcon(ContextCompat.getDrawable(context, R.drawable.ic_error_outline_black_24dp));
        show(Toast.LENGTH_SHORT);
    }

}
