package com.example.android.cipherchat.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.encryptedmessengerapp.R;

public class TutorialFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_tutorial, container, false);

        TextView tutorialContentsTextView = rootView.findViewById(R.id.tv_tutorial_contents);
        String tutorialContents = getArguments().getString("CONTENTS");
        if (tutorialContents != null) tutorialContentsTextView.setText(tutorialContents);

        return rootView;
    }

}
