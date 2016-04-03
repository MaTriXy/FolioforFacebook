// Copyright (C) 2014-2015 Jorell Rutledge/Creative Trends.
//This file is originally apart of Folio for Facebook.
//Copyright notice must remain here if you're using any part of this code.
//Some code taken from Tinfoil for Facebook
//Some code taken from Facebook Lite


package com.creativtrendz.folio.activities;

import com.creativetrends.folio.app.R;
import com.creativtrendz.folio.fragments.Settings;
import com.creativtrendz.folio.ui.SnackBar;
import com.creativtrendz.folio.utils.Miscellany;
import com.creativtrendz.folio.utils.PreferencesUtility;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

@TargetApi(23)
public class SettingsActivity extends AppCompatActivity {
	
	private static final String TAG = SettingsActivity.class.getSimpleName();
    private static final int REQUEST_STORAGE = 1;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean isFolioTheme = PreferencesUtility.getInstance(this).getTheme().equals("folio");
        final boolean isPinkTheme = PreferencesUtility.getInstance(this).getTheme().equals("pink");
        boolean isDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("darktheme");
        final boolean isBlueGreyTheme = PreferencesUtility.getInstance(this).getTheme().equals("bluegrey");

        boolean mCreatingActivity = true;
        if (!mCreatingActivity) {
            if (isFolioTheme)
            setTheme(R.style.FolioBlue);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_settings);

        } else {


            if (isDarkTheme)
                setTheme(R.style.FolioDark);


            if (isPinkTheme)
                setTheme(R.style.FolioPink);


            if (isBlueGreyTheme)
                setTheme(R.style.BlueGrey);


            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_settings);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }

            getFragmentManager().beginTransaction().replace(R.id.content_frame,
                    new Settings()).commit();
        }

    }
    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0)
            super.onBackPressed();
        else
            getFragmentManager().popBackStack();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
    	
    	case android.R.id.home: 
            onBackPressed();
            
            return true;

            case R.id.rate_folio:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.get_app_store))));
                return true;
        
    	case R.id.settings_bugs:
    		
        	Intent bugIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "bugs@creativetrendsapps.com", null));
            bugIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " Bug");
            bugIntent.putExtra(Intent.EXTRA_TEXT, "I found a bug in Folio \n\n--"  + Miscellany.getDeviceInfo(this));
            startActivity(Intent.createChooser(bugIntent, getString(R.string.choose_email_client)));
            return true;
             
        case R.id.settings_feedback:
        	Intent feedbackIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "feedback@creativetrendsapps.com", null));
            feedbackIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " Feedback");
            feedbackIntent.putExtra(Intent.EXTRA_TEXT, "Here is some awesome feedback for " + getString(R.string.app_name));
            startActivity(Intent.createChooser(feedbackIntent, getString(R.string.choose_email_client)));
            return true;
       
    default:
        return super.onOptionsItemSelected(item);
        
        
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "Storage permission granted");
                    // It's awesome, dude!
                } else {
                    Log.e(TAG, "Storage permission denied");
                    new SnackBar(this, getString(R.string.permission_not_granted), Snackbar.LENGTH_LONG)
             		.setTextColor(Color.parseColor("#FFFFFF"))
                     .setBackgroundColor(Color.parseColor("#354f88"))
                     .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}