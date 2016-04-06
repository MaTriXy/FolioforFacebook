//Notifications WITHOUT the need for a redirect

package com.creativtrendz.folio.notifications;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import java.net.URL;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import com.creativetrends.folio.app.R;
import com.creativtrendz.folio.activities.MainActivity;
import com.creativtrendz.folio.activities.MyApplication;
import com.creativtrendz.folio.saxrssreader.RssItem;
import com.creativtrendz.folio.saxrssreader.RssReader;
import com.creativtrendz.folio.services.Connectivity;
import com.creativtrendz.folio.tray.TrayAppPreferences;
import com.facebook.internal.ShareConstants;

@SuppressWarnings("deprecation")
public class FolioNotifications extends Service {

    private Handler handler = null;
    private static Runnable runnable = null;

    private String feedUrl;
    private int timeInterval;
    private SharedPreferences preferences;
    private TrayAppPreferences trayPreferences;

    private class RssReaderTask extends AsyncTask<String, Void, ArrayList<RssItem>> {
        private static final int MAX_RETRY = 3;

        private RssReaderTask() {
        }

        protected ArrayList<RssItem> doInBackground(String... params) {
            @SuppressWarnings("unused")
			String url = "https://m.facebook.com";
            CookieSyncManager.createInstance(FolioNotifications.this.getApplicationContext());
            CookieSyncManager.getInstance().sync();
            String cookie = CookieManager.getInstance().getCookie("https://m.facebook.com");
            try {
                Elements e = Jsoup.connect("http://facebook.com/notifications").cookie("https://m.facebook.com", cookie).get().select("div._li").select("div#globalContainer").select("div.fwn").select("a:matches(RSS)");
                String s = e.attr(ShareConstants.WEB_DIALOG_PARAM_HREF);                
                FolioNotifications.this.feedUrl = "https://www.facebook.com" + s;
            } catch (Exception e2) {
            }
            ArrayList<RssItem> result = null;
            int i = 0;
            while (true) {
                int tries = i + 1;
                if (i >= MAX_RETRY || result != null) {
                	preferences = getSharedPreferences(getApplicationContext().getPackageName() + "_preferences", Context.MODE_MULTI_PROCESS);
                    trayPreferences = new TrayAppPreferences(getApplicationContext());
                } else {
                    try {
                       
                        result = RssReader.read(new URL(FolioNotifications.this.feedUrl)).getRssItems();
                        i = tries;
                    } catch (Exception ex) {
                        
                        i = tries;
                    }
                }
            
            
            
            return result;
        }
        
    }

        protected void onPostExecute(ArrayList<RssItem> result) {
            String savedDate = FolioNotifications.this.preferences.getString("lastdate", "nothing");
            try {
                Log.d("date", ((RssItem) result.get(0)).getPubDate().toString());
                Log.d("date", savedDate);
                if (!((RssItem) result.get(0)).getPubDate().toString().equals(savedDate)) {
                if (!trayPreferences.getBoolean("activity_visible", false) || preferences.getBoolean("notifications_everywhere", true))
                    notifier(((RssItem) result.get(0)).getTitle(), Boolean.valueOf(false), ((RssItem) result.get(0)).getLink());
                    preferences.edit();
                }
                Editor editor = FolioNotifications.this.preferences.edit();
                editor.putString("lastdate", ((RssItem) result.get(0)).getPubDate().toString());
                editor.apply();
               
            } catch (NullPointerException e) {
                
            }
        }
    
        
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
       public void onCreate() {
           Log.i("Folio Notifications", "Started");

           // get shared preferences (for a multi process app) and TrayPreferences
           preferences = getSharedPreferences(getApplicationContext().getPackageName() + "_preferences", Context.MODE_MULTI_PROCESS);
           trayPreferences = new TrayAppPreferences(getApplicationContext());

           handler = new Handler();
           runnable = new Runnable() {
               public void run() {
                   //Log.i("Folio Notifications", "********** Service is still running **********");
                   Log.i("Folio Notifications", "isActivityVisible: " + Boolean.toString(trayPreferences.getBoolean("activity_visible", false)));

                   // get the url and time interval from shared prefs
                   feedUrl = preferences.getString("feed_url", "");
                   timeInterval = Integer.parseInt(preferences.getString("interval_pref", "3600000"));

                   // start AsyncTask if there is internet connection
                   if (Connectivity.isConnected(getApplicationContext())) {
                       Log.i("Folio Notifications", "Data available. Starting Sync.");
                       new RssReaderTask().execute(feedUrl);
                   } else
                       Log.i("Folio Notifications", "Data unavailable. Stopping Sync.");

                   // set repeat time interval
                   handler.postDelayed(runnable, timeInterval);
               }
           };

           // first run delay (3 seconds)
           handler.postDelayed(runnable, 3000);
       }
        

    @SuppressLint("InlinedApi")
	private void notifier(String title, Boolean boolean1, String url) {
        

        // start building a notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                        .setSmallIcon(R.drawable.ic_stat_f)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(title)
                        .setTicker(title)
                        .setWhen(System.currentTimeMillis())
                        .setAutoCancel(true);
        
        
        

        
        Intent allNotificationsIntent = new Intent(this, MainActivity.class);
        allNotificationsIntent.putExtra("start_url", "https://m.facebook.com/notifications");

       
        Uri ringtoneUri = Uri.parse(preferences.getString("ringtone", "content://settings/system/notification_sound"));
        mBuilder.setSound(ringtoneUri);

       
        if (preferences.getBoolean("vibrate", false))
            mBuilder.setVibrate(new long[] {500, 500});

        
        if (preferences.getBoolean("led_light", false))
            mBuilder.setLights(Color.CYAN, 1, 1);

        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            mBuilder.setPriority(Notification.PRIORITY_HIGH);

        
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("start_url", url);
        intent.setAction("NOTIFICATION_URL_ACTION");

        
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setOngoing(false);
        Notification note = mBuilder.build();

        
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, note);
    }

    public static void clearNotifications() {
        NotificationManager notificationManager = (NotificationManager)
                MyApplication.getContextOfApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }

}
