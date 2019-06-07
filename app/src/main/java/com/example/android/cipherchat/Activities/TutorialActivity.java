package com.example.android.cipherchat.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.android.encryptedmessengerapp.R;

public class TutorialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        initializeToolbar();
    }

    private void initializeToolbar() {
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar_tutorial_activity));
    }
}
