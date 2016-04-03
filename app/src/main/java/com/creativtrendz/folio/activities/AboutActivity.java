package com.creativtrendz.folio.activities;

import com.creativetrends.folio.app.R;
import com.creativtrendz.folio.utils.PreferencesUtility;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;



public class AboutActivity extends AppCompatActivity {
	
	

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
            setContentView(R.layout.activity_about);
        } else {


            if (isDarkTheme)
                setTheme(R.style.FolioDark);


            if (isPinkTheme)
                setTheme(R.style.FolioPink);


            if (isBlueGreyTheme)
                setTheme(R.style.BlueGrey);


            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_about);


            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
        }

    }
    
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dummy_menu, menu);
        return true;
   
 }
    
}