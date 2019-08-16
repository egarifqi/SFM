package com.example.sfmtesting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sfmtesting.R;

public class Splash extends AppCompatActivity {
    public SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        ImageView logo =findViewById(R.id.logo);
        ImageView parama = findViewById(R.id.parama);
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.splash);
        Animation animationx = AnimationUtils.loadAnimation(this,R.anim.splashx);
//        logo.startAnimation(animation);
        parama.startAnimation(animation);
//        parama.endAnimation(animationx);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        if (pref.getString("username", "").isEmpty()) {
            Intent i = new Intent(Splash.this, MainActivity.class);
            Thread timer = new Thread(){
                public void run(){
                    try {
                        sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    finally {
                        startActivity(i);
                        finish();
                    }
                }
            };
            timer.start();
        } else {
            Intent i = new Intent(Splash.this, com.example.sfmtesting.VisitActivity.class);
            Thread timer = new Thread(){
                public void run(){
                    try {
                        sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    finally {
                        startActivity(i);
                        finish();
                    }
                }
            };
            timer.start();
        }
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                finish();
//            }
//        }, 2000);
    }

}
