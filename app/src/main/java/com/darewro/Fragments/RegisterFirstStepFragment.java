package com.darewro.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.darewro.R;

/**
 * Created by Jaffar on 2018-01-09.
 */

public class RegisterFirstStepFragment extends Fragment {

    Activity activity;
    private TextView tvNumber;
    public RegisterFirstStepFragment() {
        // Required empty public constructor

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_register_first_step, container, false);
        tvNumber = (TextView)rootView.findViewById(R.id.tvNumber);
        tvNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterSecondStepFragment fragment = new RegisterSecondStepFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
            }
        });
        // Inflate the layout for this fragment
        return rootView;
    }

}

