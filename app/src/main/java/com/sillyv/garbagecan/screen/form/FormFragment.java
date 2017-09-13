package com.sillyv.garbagecan.screen.form;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sillyv.garbagecan.R;

public class FormFragment
        extends Fragment {
    private static final String ARG_FILE_PATH = "param1";

    private String filePath;
    private ImageAdapter imageAdapter;


    public FormFragment() {
        // Required empty public constructor
    }

    public static FormFragment newInstance(String filePath) {
        FormFragment fragment = new FormFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FILE_PATH, filePath);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            filePath = getArguments().getString(ARG_FILE_PATH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_form, container, false);

        RecyclerView recyclerView = inflate.findViewById(R.id.image_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL,
                false));
        imageAdapter = new ImageAdapter();
        recyclerView.setAdapter(imageAdapter);
        imageAdapter.addPhoto(filePath);
        return inflate;
    }

}
