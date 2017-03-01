package com.rohail.beyondinfinity.news.hub.newshub.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.rohail.beyondinfinity.news.hub.newshub.R;

public class SplashActivity extends Activity {

    private Thread mSplashThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mSplashThread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        // Wait given period of time or exit on touch
                        wait(4000);
                    }
                } catch (InterruptedException ex) {
                }

                // Run next activity
                Intent intent = new Intent();
                intent.setClass(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        };
        mSplashThread.start();

    }
}
