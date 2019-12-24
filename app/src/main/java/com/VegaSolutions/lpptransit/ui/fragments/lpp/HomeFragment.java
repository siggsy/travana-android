package com.VegaSolutions.lpptransit.ui.fragments.lpp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.VegaSolutions.lpptransit.R;

public class HomeFragment extends Fragment {

    public static final int BUS = 0;
    public static final int TRAIN = 1;
    public static final int BIKE = 2;
    public static final int PARKING = 3;

    private HomeFragmentListener mListener;

    public static HomeFragment newInstance() {

        Bundle args = new Bundle();

        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        ImageView bus = root.findViewById(R.id.home_bus);
        ImageView train = root.findViewById(R.id.home_train);
        ImageView bike = root.findViewById(R.id.home_bike);
        ImageView parking = root.findViewById(R.id.home_parking);

        bus.setOnClickListener(view -> onButtonPressed(BUS));
        train.setOnClickListener(view -> onButtonPressed(TRAIN));
        bike.setOnClickListener(view -> onButtonPressed(BIKE));
        parking.setOnClickListener(view -> onButtonPressed(PARKING));

        return root;
    }

    public void onButtonPressed(int b) {
        if (mListener != null) {
            mListener.onButtonPressed(b);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof HomeFragmentListener) {
            mListener = (HomeFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement HomeFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface HomeFragmentListener {
        void onButtonPressed(int b);
    }
}
