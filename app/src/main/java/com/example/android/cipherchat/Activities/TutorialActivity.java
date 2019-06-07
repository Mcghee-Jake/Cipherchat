package com.example.android.cipherchat.Activities;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.android.cipherchat.Adapters.TutorialViewPagerAdapter;
import com.example.android.cipherchat.Objects.Tutorial;
import com.example.android.cipherchat.Utils.TutorialUtils;
import com.example.android.encryptedmessengerapp.R;

import java.util.List;

public class TutorialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        initializeToolbar();
        initializeViewPager();
    }

    private void initializeToolbar() {
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar_tutorial_activity));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initializeViewPager() {
        final List<Tutorial> tutorials = TutorialUtils.getTutorials();
        ViewPager viewPager = findViewById(R.id.vp_tutorial);
        PagerAdapter pagerAdapter = new TutorialViewPagerAdapter(getSupportFragmentManager(), tutorials);
        viewPager.setAdapter(pagerAdapter);
        final int position = getIntent().getIntExtra("POSITION", 0);
        getSupportActionBar().setTitle(tutorials.get(position).getTitle());
        viewPager.setCurrentItem(position);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) { }

            @Override
            public void onPageSelected(int i) { getSupportActionBar().setTitle(tutorials.get(i).getTitle()); }

            @Override
            public void onPageScrollStateChanged(int i) { }
        });
    }
}
