package com.example.alsoosbrotherscarpentry;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Delay to show splash screen
        new Handler().postDelayed(() -> {
            // Intent to LoginActivity (or MainActivity if already logged in)
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, 2000); // 2 seconds delay
    }
}

