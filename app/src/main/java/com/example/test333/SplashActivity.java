package com.example.test333;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    ImageView iv1;
    TextView tv1,tv2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        iv1 = findViewById(R.id.splash_iv1);
        tv1 = findViewById(R.id.splash_tv1);
        tv2 = findViewById(R.id.splash_tv2);
        Animation animiv1 = AnimationUtils.loadAnimation(this, R.anim.splash_iv1);
        Animation animtv1 = AnimationUtils.loadAnimation(this, R.anim.splash_tv1);
        Animation animtv2 = AnimationUtils.loadAnimation(this, R.anim.splash_tv2);
        iv1.setAnimation(animiv1);
        tv1.setAnimation(animtv1);
        tv2.setAnimation(animtv2);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            }
        },4300);
    }
}