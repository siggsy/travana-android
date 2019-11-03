package com.VegaSolutions.lpptransit.ui.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.StationOnRoute;

import java.util.List;

public class RouteView extends View {

    private List<StationOnRoute> stationsOnRoute;
    private Paint paint;
    private float textSize;
    private TextView name;


    public RouteView(Context context) {
        super(context);


    }

    public RouteView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);



    }

    private void init(@Nullable AttributeSet set) {

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(5f);

        if (set == null)
            return;
        TypedArray ta = getContext().obtainStyledAttributes(set, R.styleable.RouteView);
        paint.setColor(ta.getColor(R.styleable.RouteView_line_color, Color.BLACK));
        textSize = ta.getDimension(R.styleable.RouteView_station_name_size, 24);

    }


    public void setStationsOnRoute(List<StationOnRoute> stationsOnRoute) {
        this.stationsOnRoute = stationsOnRoute;
        invalidate();
    }


    public void invalidate() {



    }




}
