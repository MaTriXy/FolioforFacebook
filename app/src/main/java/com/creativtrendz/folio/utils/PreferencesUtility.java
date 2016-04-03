/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.creativtrendz.folio.utils;

import com.creativtrendz.folio.activities.FolioApplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

public final class PreferencesUtility {

    private static final String THEME_PREFERNCE="theme_preference";
    private static final String FONT_SIZE="font_pref";
    private static final String FACEBOOK_THEMES="theme_preference_fb";
    private static final String NEWS_FEED="news_feed";
    


    private static PreferencesUtility sInstance;

    private static SharedPreferences mPreferences;

    public PreferencesUtility(final Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
    public static final PreferencesUtility getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new PreferencesUtility(context.getApplicationContext());
        }
        return sInstance;
    }


    public void setOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener){
        mPreferences.registerOnSharedPreferenceChangeListener(listener);
    }


    public String getTheme(){ return mPreferences.getString(THEME_PREFERNCE, "folio"); }
    
    public String getFont(){
        return mPreferences.getString(FONT_SIZE, "default_font");
    }

    public String getFeed(){
        return mPreferences.getString(NEWS_FEED, "default_news");
    }
    
    public String getFreeTheme(){ return mPreferences.getString(FACEBOOK_THEMES, "materialtheme"); }

   public static boolean getBoolean(String key, boolean defValue){
	return PreferenceManager.getDefaultSharedPreferences(FolioApplication.getContextOfApplication()).getBoolean(key, defValue);
   }

   public static void putBoolean(String key, boolean value) {
	   Editor editor = PreferenceManager.getDefaultSharedPreferences(FolioApplication.getContextOfApplication()).edit();
	   editor.putBoolean(key, value);
	   editor.apply();
   }
   
   public static int getInt(String key, int defValue){
   return PreferenceManager.getDefaultSharedPreferences(FolioApplication.getContextOfApplication()).getInt(key, defValue);
	   }
   
   public static void putInt(String key, int value) {
	   Editor editor = PreferenceManager.getDefaultSharedPreferences(FolioApplication.getContextOfApplication()).edit();
	   editor.putInt(key, value);
	   editor.apply();
   }
   
   public static long getLong(String key, long defValue){
   return PreferenceManager.getDefaultSharedPreferences(FolioApplication.getContextOfApplication()).getLong(key, defValue);
		   }
   
   public static void putLong(String key, long value) {
	   Editor editor = PreferenceManager.getDefaultSharedPreferences(FolioApplication.getContextOfApplication()).edit();
	   editor.putLong(key, value);
	   editor.apply();
   }
   	   public static String getString(String key, String defValue){
	   return PreferenceManager.getDefaultSharedPreferences(FolioApplication.getContextOfApplication()).getString(key, defValue);
	   }
   	   
   	public static void putString(String key, String value) {
 	   Editor editor = PreferenceManager.getDefaultSharedPreferences(FolioApplication.getContextOfApplication()).edit();
 	   editor.putString(key, value);
 	   editor.apply();
    }
   	
   	public static void remove(String key) {
  	   Editor editor = PreferenceManager.getDefaultSharedPreferences(FolioApplication.getContextOfApplication()).edit();
  	   editor.remove(key);
  	   editor.apply();
     }
   	
   	public static void registerOnPreferenceChangeListener(OnSharedPreferenceChangeListener listener){
   		try{
   		PreferenceManager.getDefaultSharedPreferences(FolioApplication.getContextOfApplication()).registerOnSharedPreferenceChangeListener(listener);
   	}catch (Exception e) {
   	}
   	   		
    }
   	public static void unregisterOnPreferenceChangeListener(OnSharedPreferenceChangeListener listener){
   		try{
   		PreferenceManager.getDefaultSharedPreferences(FolioApplication.getContextOfApplication()).unregisterOnSharedPreferenceChangeListener(listener);
   	}catch (Exception e) {
   	}
	}
	} 