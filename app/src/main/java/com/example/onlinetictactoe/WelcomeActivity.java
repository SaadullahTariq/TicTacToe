package com.example.onlinetictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


public class WelcomeActivity extends AppCompatActivity {

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        handler.postDelayed(() -> {
            Intent intent = new Intent(WelcomeActivity.this, PlayerNameActivity.class);
            startActivity(intent);
        }, 4800);

    }
}