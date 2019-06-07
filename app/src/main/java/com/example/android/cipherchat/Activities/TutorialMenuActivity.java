package com.example.android.cipherchat.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.example.android.cipherchat.Adapters.TutorialMenuAdapter;
import com.example.android.cipherchat.Utils.TutorialUtils;
import com.example.android.encryptedmessengerapp.R;

public class TutorialMenuActivity extends AppCompatActivity implements TutorialMenuAdapter.TutorialMenuClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_menu);

        initializeToolbar();
        initializeRecyclerView();
    }

    private void initializeToolbar() {
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar_tutorial_menu_activity));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.tutorial_activity_title));
    }

    private void initializeRecyclerView() {
        RecyclerView tutorialRecyclerView = (RecyclerView) findViewById(R.id.rv_tutorial_titles);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        tutorialRecyclerView.setLayoutManager(layoutManager);
        tutorialRecyclerView.setHasFixedSize(true);
        TutorialMenuAdapter tutorialMenuAdapter = new TutorialMenuAdapter(TutorialUtils.getTutorials(), this);
        tutorialRecyclerView.setAdapter(tutorialMenuAdapter);
    }

    @Override
    public void onMenuItemClicked(int position) {
        Intent intent = new Intent(TutorialMenuActivity.this, TutorialActivity.class);
        intent.putExtra("POSITION", position);
        startActivity(intent);
    }
}
