// Copyright (C) 2014-2015 Jorell Rutledge/Creative Trends.
//This file is originally apart of Folio for Facebook.
//Copyright notice must remain here if you're using any part of this code.
//Some code taken from Tinfoil for Facebook
//Some code taken from Facebook Lite


package com.creativtrendz.folio.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.creativetrends.folio.app.R;
import com.creativtrendz.folio.activities.MainActivity;


public class Navigation extends PreferenceFragment {


   
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);

       
        
        addPreferencesFromResource(R.xml.navigation_preferences);


        
    findPreference("tap").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
        	relaunch();
            return true;
        }
    });
    
    

    findPreference("enable_gplus").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
        	
            return true;
        }
    });
    
    findPreference("enable_instagram").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
        	
            return true;
        }
    });
    
    findPreference("enable_tumblr").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
        	
            return true;
        }
    });
    
    
    findPreference("recent_off").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
        	
            return true;
        }
    });
    
    findPreference("top_off").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
        	
            return true;
        }
    });
    
        
    findPreference("trending_off").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
        	
            return true;
        }
    });
    
    
    findPreference("friends_off").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
        	
            return true;
        }
    });
    
    
    
    findPreference("groups_off").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
        	
            return true;
        }
    });
    
    
    findPreference("pages_off").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
        	
            return true;
        }
    });
    
    
    findPreference("photos_off").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
        	
            return true;
        }
    });
    
    
    findPreference("events_off").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
        	
            return true;
        }
    });
    
    
    findPreference("thisday_off").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
        	
            return true;
        }
    });
    
    
    findPreference("saved_off").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
        	
            return true;
        }
    });
    
    
    }
    
     
    

		private void relaunch() {  
        	
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("apply_changes_to_app", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
		}
        


@Override
public void onStart() {
    super.onStart();
                   
 	}
 	

@Override
public void onResume() {
    super.onResume();
                   
 	}
 	

@Override
public void onPause() {
    super.onResume();
                  
 	}
 	}
