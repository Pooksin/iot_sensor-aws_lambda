package com.example.iot_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    public ImageView img;
    public TextView t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_layout);

        img = (ImageView) findViewById(R.id.imageView);
        t = (TextView)findViewById(R.id.home);

        Handler hand1 = new Handler();
        Handler hand2 = new Handler();

        hand1.postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, 3000);

        hand2.postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                img.setImageResource(R.drawable.icon_2);
            }
        }, 500);

        hand2.postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                img.setImageResource(R.drawable.icon_3);
            }
        }, 1000);

        hand2.postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                img.setImageResource(R.drawable.icon_1);
            }
        }, 1500);

        hand2.postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                img.setImageResource(R.drawable.icon_2);
            }
        }, 2000);

        hand2.postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                img.setImageResource(R.drawable.icon_3);
            }
        }, 2500);

        hand2.postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                img.setImageResource(R.drawable.icon_1);
            }
        }, 2800);

    }
}
