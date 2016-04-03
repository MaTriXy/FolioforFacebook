// Copyright (C) 2014-2015 Jorell Rutledge/Creative Trends.
//This file is originally apart of Folio for Facebook.
//Copyright notice must remain here if you're using any part of this code.
//Some code taken from Tinfoil for Facebook
//Some code taken from Facebook Lite

package com.creativtrendz.folio.activities;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;

public class FolioApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        super.onCreate();
        
    }   

    public static Context getContextOfApplication() {
        return mContext;
    }

	public static ContextWrapper getInstance() {
		// TODO Auto-generated method stub
		return null;
	}

}