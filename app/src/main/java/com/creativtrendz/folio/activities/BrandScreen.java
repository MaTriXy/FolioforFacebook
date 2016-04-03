package com.creativtrendz.folio.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class BrandScreen extends Activity {

    
    private static final int TIME = 1200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(BrandScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, TIME);
    }


}