package com.creativtrendz.folio.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import com.creativetrends.folio.app.R;
import com.creativtrendz.folio.activities.AboutActivity;
import com.creativtrendz.folio.activities.FolioLockSetup;
import com.creativtrendz.folio.activities.MainActivity;
import com.creativtrendz.folio.ui.SnackBar;
import com.creativtrendz.folio.utils.FileOperation;





public class Settings extends PreferenceFragment implements Preference.OnPreferenceClickListener{


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        
        addPreferencesFromResource(R.xml.preferences);



        Preference notifications = findPreference("notifications_settings");
        Preference customize = findPreference("custom_settings");
        Preference about = findPreference("about_settings");
        Preference credits = findPreference("credits_settings");
        Preference lock = findPreference("folio_locker");
        Preference getkey = findPreference("help_development");
        Preference navigation = findPreference("customnav");
        Preference fontset = findPreference("tap");
        Preference clearCachePref = findPreference("clear");
        
        
        
        
       
        notifications.setOnPreferenceClickListener(this);
        customize.setOnPreferenceClickListener(this);
        about.setOnPreferenceClickListener(this);
        credits.setOnPreferenceClickListener(this);
        lock.setOnPreferenceClickListener(this);
        getkey.setOnPreferenceClickListener(this);
        navigation.setOnPreferenceClickListener(this);
        fontset.setOnPreferenceClickListener(this);
        clearCachePref.setOnPreferenceClickListener(this);
        
       

     
    }
    
     
    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        
        switch (key) {
            case "notifications_settings":
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, 0)
                        .addToBackStack(null).replace(R.id.content_frame,
                        new Notify()).commit();
                break;
                
            case "custom_settings":
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, 0)
                        .addToBackStack(null).replace(R.id.content_frame,
                        new Customize()).commit();
                break;
                
            case "about_settings":
            	Intent settings = new Intent(getActivity(), AboutActivity.class);
                startActivity(settings);
                break;
                
                            
            case "credits_settings":
            	getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, 0)
                .addToBackStack(null).replace(R.id.content_frame,
                new Credits()).commit();
                break;
                
                          
            case "help_development":
            	startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.get_key))));
                break;

                           
            case "tap":            	
            	 relaunch();
                break;
                
            case "customnav":
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, 0)
                        .addToBackStack(null).replace(R.id.content_frame,
                        new Navigation()).commit();
                break;
                
            case "folio_locker":
            	Intent folioLocker = new Intent(getActivity(), FolioLockSetup.class);
                startActivity(folioLocker);
                break;

                            
            case "clear": 
            	               	 
            	AlertDialog.Builder clear =
 		       	new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
 		            	clear.setTitle(getResources().getString(R.string.clear_cache_title));
 		            	clear.setMessage( Html.fromHtml(getResources().getString(R.string.clear_cache_message)));
 		           		clear.setPositiveButton(R.string.ok, new AlertDialog.OnClickListener(){
       		            	@Override
      						public void onClick(DialogInterface arg0, int arg1) {
       		            		FileOperation.deleteCache(getActivity().getApplicationContext());
       		            		new SnackBar(getActivity(), (R.string.applying_changes), Snackbar.LENGTH_SHORT)       		                    
       		                    .show();
       							}
       		            });
 		          		clear.setNeutralButton(R.string.cancel, null);
 		         		clear.show();
                break;
             
                                
               
        }

        return false;
    
    
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
        super.onPause();
    }




    private void relaunch() {  
    	
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("apply_changes_to_app", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        
     
        }
 

}



	