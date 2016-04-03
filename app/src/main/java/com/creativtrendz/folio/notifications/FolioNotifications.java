package com.creativtrendz.folio.notifications;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;

import com.creativetrends.folio.app.R;
import com.creativtrendz.folio.activities.MainActivity;
import com.creativtrendz.folio.activities.FolioApplication;
import com.creativtrendz.folio.services.Connectivity;
import com.creativtrendz.folio.utils.Logger;

import android.annotation.SuppressLint;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.TaskStackBuilder;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;
import net.grandcentrix.tray.TrayAppPreferences;
import nl.matshofman.saxrssreader.RssFeed;
import nl.matshofman.saxrssreader.RssItem;
import nl.matshofman.saxrssreader.RssReader;

@SuppressWarnings("deprecation")
public class FolioNotifications extends Service {

    
    private static final String BASE_URL = "https://www.facebook.com";
    private static final String NOTIFICATIONS_URL = "https://www.facebook.com/notifications";
    private static final String NOTIFICATIONS_URL_BACKUP = "https://web.facebook.com/notifications";
    private static final String MESSAGES_URL = "https://m.facebook.com/messages";
    private static final String MESSAGES_URL_BACKUP = "https://mobile.facebook.com/messages";
    private static final String NOTIFICATION_MESSAGE_URL = "https://m.facebook.com/messages";

    
    private static final int MAX_RETRY = 3;
    private static final int JSOUP_TIMEOUT = 10000;
    private static final String TAG;

    
    private final HandlerThread handlerThread;
    private final Handler handler;
    private static Runnable runnable;

    
    private volatile boolean shouldContinue = true;
    private static String userAgent;
    private TrayAppPreferences trayPreferences;
    private SharedPreferences preferences;
    
    private final Logger Log;

    
    static {
        TAG = FolioNotifications.class.getSimpleName();
    }

    
    public FolioNotifications() {
        handlerThread = new HandlerThread("Handler Thread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        Log = Logger.getInstance();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "Folio Notifications Started");
        super.onCreate();

        preferences = getSharedPreferences(getApplicationContext().getPackageName() + "_preferences", Context.MODE_MULTI_PROCESS);
        trayPreferences = new TrayAppPreferences(getApplicationContext());

        
        runnable = new HandlerRunnable();

        
        handler.postDelayed(runnable, 3000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Stopping Folio Notifications");
        super.onDestroy();

        synchronized (handler) {
            shouldContinue = false;
            handler.notify();
        }

        handler.removeCallbacksAndMessages(null);
        handlerThread.quit();
    }

    
    private class HandlerRunnable implements Runnable {

        public void run() {
            try {

                
                final int timeInterval = trayPreferences.getInt("interval_pref", 1800000);
                Log.i(TAG, "Time interval: " + (timeInterval / 1000) + " seconds");

                
                final long now = System.currentTimeMillis();
                final long sinceLastCheck = now - trayPreferences.getLong("last_check", now);
                final boolean ntfLastStatus = trayPreferences.getBoolean("ntf_last_status", false);
                final boolean msgLastStatus = trayPreferences.getBoolean("msg_last_status", false);

                if ((sinceLastCheck < timeInterval) && ntfLastStatus && msgLastStatus) {
                    final long waitTime = timeInterval - sinceLastCheck;
                    if (waitTime >= 1000) { 
                        Log.i(TAG, "Folio will sleep for " + (waitTime / 1000) + " seconds");

                        synchronized (handler) {
                            try {
                                handler.wait(waitTime);
                            } catch (InterruptedException ex) {
                               
                            } finally {
                               
                            }
                        }

                    }
                }

                
                if (shouldContinue) {
                    
                    if (Connectivity.isConnected(getApplicationContext())) {
                        Log.i(TAG, "Data is connected. Starting sync.");
                        String connectionType = "Wi-Fi";
                        if (Connectivity.isConnectedMobile(getApplicationContext()))
                            connectionType = "Mobile";
                        Log.i(TAG, "Connection Type: " + connectionType);
                        userAgent = trayPreferences.getString("webview_user_agent", System.getProperty("http.agent"));
                        Log.i(TAG, "User Agent: " + userAgent);

                        if (trayPreferences.getBoolean("notifications_activated", false))
                            new RssReaderTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
                        if (trayPreferences.getBoolean("messages_activated", false))
                            new CheckMessagesTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);

                        
                        trayPreferences.put("last_check", System.currentTimeMillis());
                    } else
                        Log.i(TAG, "No data connection. Stopping sync.");

                    
                    handler.postDelayed(runnable, timeInterval);
                } else
                    Log.i(TAG, "Stopping sync.");

            } catch (RuntimeException re) {
                Log.i(TAG, "Error");
                restartItself();
            }
        }

    }

    
    private class RssReaderTask extends AsyncTask<Void, Void, ArrayList<RssItem>> {

        private boolean syncProblemOccurred = false;

        private String getFeed(String connectUrl) {
            try {
                Elements element = Jsoup.connect(connectUrl).userAgent(userAgent).timeout(JSOUP_TIMEOUT)
                        .cookie("https://m.facebook.com", CookieManager.getInstance().getCookie("https://m.facebook.com")).get()
                        .select("div._li").select("div#globalContainer").select("div.fwn").select("a[href*=rss20]");

                return element.attr("href");
            } catch (IllegalArgumentException ex) {
                Log.i("Message Notifications", "Cookie sync problem occurred");
                if (!syncProblemOccurred) {
                    syncProblemToast();
                    syncProblemOccurred = true;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return "failure";
        }

        @Override
        protected ArrayList<RssItem> doInBackground(Void... params) {
            ArrayList<RssItem> result = null;
            String feedUrl;
            int tries = 0;

            
            syncCookies();

            while (tries++ < MAX_RETRY && result == null) {
                
                Log.i("Facebook Notifications", "Trying: " + NOTIFICATIONS_URL);
                String secondPart = getFeed(NOTIFICATIONS_URL);
                if (secondPart.length() < 10) {
                    Log.i("Facebook Notifications", "Trying: " + NOTIFICATIONS_URL_BACKUP);
                    secondPart = getFeed(NOTIFICATIONS_URL_BACKUP);
                }
                
                if (secondPart.length() > 10)
                    feedUrl = BASE_URL + secondPart;
                else
                    feedUrl = "malformed";

                try {
                    Log.i("Facebook Notifications", "Trying sync " + tries);
                    URL url = new URL(feedUrl);
                    RssFeed feed = RssReader.read(url);
                    result = feed.getRssItems();
                } catch (MalformedURLException ex) {
                    Log.i("Facebook Notifications", "Sync failed: URL error");
                } catch (SAXException | IOException ex) {
                    Log.i("Facebook Notifications", "Sync failed: Feed error");
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<RssItem> result) {

           
            final String savedDate = trayPreferences.getString("saved_date", "nothing");

            
            try {
                if (!result.get(0).getPubDate().toString().equals(savedDate))
                    if (!trayPreferences.getBoolean("activity_visible", false) || trayPreferences.getBoolean("notifications_everywhere", true))
                        notifier(result.get(0).getTitle(), result.get(0).getDescription(), result.get(0).getLink(), false);

                
                trayPreferences.put("saved_date", result.get(0).getPubDate().toString());

                
                trayPreferences.put("ntf_last_status", true);
                Log.i("Facebook Notifications", "Starting sync");
            } catch (NullPointerException | IndexOutOfBoundsException ex) {
                
                trayPreferences.put("ntf_last_status", false);
                Log.i("Facebook Notifications", "Starting failed");
            }
        }

    }

    
    private class CheckMessagesTask extends AsyncTask<Void, Void, String> {

        boolean syncProblemOccurred = false;

        private String getNumber(String connectUrl) {
            try {
                Elements message = Jsoup.connect(connectUrl).userAgent(userAgent).timeout(JSOUP_TIMEOUT)
                        .cookie("https://m.facebook.com", CookieManager.getInstance().getCookie("https://m.facebook.com")).get()
                        .select("div#viewport").select("div#page").select("div._129-")
                        .select("#messages_jewel").select("span._59tg");

                return message.html();
            } catch (IllegalArgumentException ex) {
                Log.i("Message Notifications", "Cookie sync problem occurred");
                if (!syncProblemOccurred) {
                    syncProblemToast();
                    syncProblemOccurred = true;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return "failure";
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = null;
            int tries = 0;

            
            syncCookies();

            while (tries++ < MAX_RETRY && result == null) {
                Log.i("Message Notifications", "Trying sync " + tries);

                
                Log.i("Message Notifications", "Trying: " + MESSAGES_URL);
                String number = getNumber(MESSAGES_URL);
                if (!number.matches("^[+-]?\\d+$")) {
                    Log.i("Message Notifications", "Trying: " + MESSAGES_URL_BACKUP);
                    number = getNumber(MESSAGES_URL_BACKUP);
                }
                if (number.matches("^[+-]?\\d+$"))
                    result = number;
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
               
                int newMessages = Integer.parseInt(result);

                if (!trayPreferences.getBoolean("activity_visible", false) || trayPreferences.getBoolean("notifications_everywhere", true)) {
                    if (newMessages == 1)
                        notifier(getString(R.string.you_have_one_message), null, NOTIFICATION_MESSAGE_URL, true);
                    else if (newMessages > 1)
                        notifier(String.format(getString(R.string.you_have_n_messages), newMessages), null, NOTIFICATION_MESSAGE_URL, true);
                }

                
                trayPreferences.put("msg_last_status", true);
                Log.i("Message Notifications", "Sync started");
            } catch (NumberFormatException ex) {
                
                trayPreferences.put("msg_last_status", false);
                Log.i("Message Notifications", "Sync failed");
            }
        }

    }


    private void syncCookies() {
        if (Build.VERSION.SDK_INT < 21) {
            CookieSyncManager.createInstance(getApplicationContext());
            CookieSyncManager.getInstance().sync();
        }
    }

    
    private void syncProblemToast() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), getString(R.string.sync_problem),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    
    private void restartItself() {
        final Context context = FolioApplication.getContextOfApplication();
        final Intent intent = new Intent(context, FolioNotifications.class);
        context.stopService(intent);
        context.startService(intent);
    }

    
    @SuppressLint("InlinedApi")
	private void notifier(String title, String summary, String url, boolean isMessage) {
        
        final String contentTitle;
        if (isMessage)
            contentTitle = getString(R.string.app_name);
        else
            contentTitle = getString(R.string.app_name);

        
        Log.i(TAG, "Start notification - isMessage: " + isMessage);
        
        Intent actionIntent = new Intent(this, MainActivity.class);
        actionIntent.putExtra("start_url", "https://m.facebook.com/notifications");
        PendingIntent actionPendingIntent =
                PendingIntent.getActivity(this, 0, actionIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        
        Intent messageIntent = new Intent(this, MainActivity.class);
        messageIntent.putExtra("start_url", "https://m.facebook.com/messages");
        PendingIntent messagePendingIntent =
                PendingIntent.getActivity(this, 1, messageIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.ic_public,
                        getString(R.string.app_name), actionPendingIntent)
                        .build();
        
        NotificationCompat.Action message =
                new NotificationCompat.Action.Builder(R.drawable.ic_messenger_new,
                        getString(R.string.app_name), messagePendingIntent)
                        .build();

        
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                        .setSmallIcon(R.drawable.ic_stat_f)
                        .setColor(getResources().getColor(R.color.PrimaryDarkColor))
                        .setContentTitle(contentTitle)
                        .setContentText(title)
                        .setTicker(title)
                        .setWhen(System.currentTimeMillis())
                        .extend(new WearableExtender().addAction(action))                        
                        .setAutoCancel(true);


        
        if (!isMessage) {
            Intent allNotificationsIntent = new Intent(this, MainActivity.class);
            allNotificationsIntent.putExtra("start_url", "https://m.facebook.com/notifications");
            allNotificationsIntent.setAction("ALL_NOTIFICATIONS_ACTION");
            PendingIntent piAllNotifications = PendingIntent.getActivity(getApplicationContext(), 0, allNotificationsIntent, 0);
            mBuilder.addAction(R.drawable.ic_public, getString(R.string.all_notifications), piAllNotifications);
            mBuilder.extend(new WearableExtender().addAction(message));
            
        }


        String ringtoneKey = "ringtone";
        if (isMessage)
            ringtoneKey = "ringtone_msg";

        Uri ringtoneUri = Uri.parse(preferences.getString(ringtoneKey, "content://settings/system/notification_sound"));
        mBuilder.setSound(ringtoneUri);

        if (preferences.getBoolean("vibrate", false))
            mBuilder.setVibrate(new long[] {500, 500});
        else
            mBuilder.setVibrate(new long[] {0l});


        if (preferences.getBoolean("led_light", false))
            mBuilder.setLights(Color.CYAN, 1, 1);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            mBuilder.setDefaults(-1);

        
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

        
        if (isMessage)
            mNotificationManager.notify(1, note);
        else
            mNotificationManager.notify(0, note);
    }

    
    public static void clearNotifications() {
        NotificationManager notificationManager = (NotificationManager)
                FolioApplication.getContextOfApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }
    
    public static void clearMessages() {
        NotificationManager notificationManager = (NotificationManager)
                FolioApplication.getContextOfApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
    }

}
