package com.animewatcher.animewatcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class disclaimer_privacy_policy extends AppCompatActivity {

    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclaimer_privacy_policy);

        img = findViewById(R.id.topnav_disclaimer_privacy_close);

        img.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                }
        );
    }
}