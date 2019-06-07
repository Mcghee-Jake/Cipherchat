package com.example.android.cipherchat.Adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.android.cipherchat.Fragments.TutorialFragment;
import com.example.android.cipherchat.Objects.Tutorial;

import java.util.List;

public class TutorialViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<Tutorial> tutorialList;

    public TutorialViewPagerAdapter(FragmentManager fm, List<Tutorial> tutorialList) {
        super(fm);
        this.tutorialList = tutorialList;
    }

    @Override
    public Fragment getItem(int i) {
        TutorialFragment tutorialFragment = new TutorialFragment();
        Bundle args = new Bundle();
        args.putString("CONTENTS", tutorialList.get(i).getTutorialContents());
        tutorialFragment.setArguments(args);
        return tutorialFragment;
    }

    @Override
    public int getCount() {
        return tutorialList.size();
    }
}
