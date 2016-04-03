package com.creativtrendz.folio.notifications;

import com.creativtrendz.folio.activities.FolioApplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import net.grandcentrix.tray.TrayAppPreferences;


public class PackageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        
        context = FolioApplication.getContextOfApplication();

       
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        TrayAppPreferences trayPreferences = new TrayAppPreferences(context);


        trayPreferences.put("interval_pref", Integer.parseInt(preferences.getString("interval_pref", "1800000")));
        preferences.getString("ringtone", "content://settings/system/notification_sound");
        preferences.getBoolean("vibrate", false);
        preferences.getBoolean("led_light", false);
        trayPreferences.put("notifications_everywhere", preferences.getBoolean("notifications_everywhere", true));
        trayPreferences.put("notifications_activated", preferences.getBoolean("notifications_activated", false));


        Intent startIntent = new Intent(context, FolioNotifications.class);


        if (preferences.getBoolean("notifications_activated", false) || preferences.getBoolean("messages_activated", false))
            context.startService(startIntent);
    }

}