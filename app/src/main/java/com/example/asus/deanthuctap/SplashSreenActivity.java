package com.example.asus.deanthuctap;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.yasic.library.particletextview.MovingStrategy.RandomMovingStrategy;
import com.yasic.library.particletextview.Object.ParticleTextViewConfig;
import com.yasic.library.particletextview.View.ParticleTextView;

public class SplashSreenActivity extends AppCompatActivity {

    ParticleTextView particleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_sreen);
        particleTextView = findViewById(R.id.particleTextView);

        ParticleTextViewConfig config=new ParticleTextViewConfig.Builder()
                .setRowStep(5)
                .setColumnStep(5)
                .setTargetText("App Travel ")
                .setReleasing(0.2)
                .setParticleRadius(3)
                .setMiniDistance(0.1)
                .setTextSize(110)
                .setDelay((long) 2000)
                .setMovingStrategy(new RandomMovingStrategy())
                .instance();
        particleTextView.setConfig(config);
        particleTextView.startAnimation();

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(SplashSreenActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        },5000);
    }
}
