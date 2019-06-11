package com.example.android.cipherchat.Fragments;

import android.content.Context;
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

    private FragmentPageNavigator fragmentPageNavigator;

    public interface FragmentPageNavigator {
        void changePage(int position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentPageNavigator = (FragmentPageNavigator) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_tutorial, container, false);

        // Set up the tutorial text
        TextView tutorialContentsTextView = rootView.findViewById(R.id.tv_tutorial_contents);
        String tutorialContents = getArguments().getString("CONTENTS");
        if (tutorialContents != null) tutorialContentsTextView.setText(tutorialContents);

        // Set up the previous/next buttons
        View previousBtn = rootView.findViewById(R.id.btn_previous_page);
        View nextBtn = rootView.findViewById(R.id.btn_next_page);
        final int position = getArguments().getInt("POSITION");
        int tutorialCount = getArguments().getInt("TUTORIAL_COUNT");
        if (position > 0) {
            previousBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fragmentPageNavigator.changePage(position - 1);
                }
            });

        } else previousBtn.setVisibility(View.GONE);
        if (position < tutorialCount - 1) {
            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fragmentPageNavigator.changePage(position + 1);
                }
            });
        } else nextBtn.setVisibility(View.GONE);

        return rootView;
    }

}
