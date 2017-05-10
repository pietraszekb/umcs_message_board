package com.example.maktel.messageboardumcs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String DEBUG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void openNewsView(View v) {
        startActivity(new Intent(this, ScreenSlideActivity.class));
    }

    public void clearNewsFile(View v) {
        Log.d(DEBUG_TAG, "File length: " + new File(getApplicationContext().getFilesDir() +
                "NewsArrayList.ser").length());
        if (new File(getApplicationContext().getFilesDir() + "NewsArrayList.ser").length() > 0)
            new File(getApplicationContext().getFilesDir() + "NewsArrayList.ser").delete();
    }
}
