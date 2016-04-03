package com.creativtrendz.folio.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.creativtrendz.folio.fragments.Notify;


public class FolioReceiver extends BroadcastReceiver {

    public static void scheduleAlarms(Context ctxt, boolean cancel) {
        AlarmManager mgr = (AlarmManager) ctxt.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(ctxt, FolioNotifications.class);
        PendingIntent pi = PendingIntent.getService(ctxt, 0, i, 0);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctxt);
        if (preferences.getBoolean("notifications_activated", false)
                || preferences.getBoolean("messages_activated", false)) {
            int interval = Integer.parseInt(preferences.getString(Notify.KEY_PREF_UPDATE_INTERVAL, "600000"));
            mgr.setRepeating(AlarmManager.ELAPSED_REALTIME, 5000, interval, pi);

        } else {

            mgr.cancel(pi);

        }
    }

    @Override
    public void onReceive(Context ctxt, Intent i) {
        scheduleAlarms(ctxt, false);
    }
}

