package com.creativtrendz.folio.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.creativetrends.folio.app.R;
import com.creativtrendz.folio.notifications.FolioNotifications;
import com.creativtrendz.folio.notifications.FolioReceiver;
import com.creativtrendz.folio.services.Connectivity;
import com.creativtrendz.folio.ui.FolioHelpers;
import com.creativtrendz.folio.ui.FolioInterfaces;
import com.creativtrendz.folio.ui.FolioWebViewScroll;
import com.creativtrendz.folio.utils.FileOperation;
import com.creativtrendz.folio.utils.FolioListener;
import com.creativtrendz.folio.utils.PreferencesUtility;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.ProfilePictureView;
import com.github.clans.fab.FloatingActionMenu;
import com.greysonparrelli.permiso.Permiso;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;
import com.squareup.picasso.Picasso;

import net.grandcentrix.tray.TrayAppPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.eliseomartelli.simplecustomtabs.CustomTabs;


@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
@SuppressWarnings({ "deprecation", "unused" })
public class MainActivity extends AppCompatActivity implements View.OnLongClickListener {
    private static Activity mainActivity;
    private final static String LOG_TAG = "Folio";
    private static final String INIT_URL_MOBILE = null;
    private String mDomainToUse = INIT_URL_MOBILE;
    public static final String FACEBOOK = "https://m.facebook.com/";
    private static final List<String> SITES = Arrays.asList("facebook.com", "*.facebook.com", "*.fbcdn.net", "*.akamaihd.net");
    public View mCoordinatorLayoutView;
    DrawerLayout drawerLayoutFavs;
    private DrawerLayout drawerLayout;
    private static SharedPreferences preferences;
    private TrayAppPreferences trayPreferences;
    private static final int REQUEST_STORAGE = 1;
    private MenuItem mNotificationButton;
    private MenuItem mMessagesButton;
    public Toolbar toolbar;
    private static final int ID_CONTEXT_MENU_SAVE_IMAGE = 2981279;
    NavigationView navigationView;
    NavigationView navigationViewFavs;
    public SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionMenu FAB;
    private FolioWebViewScroll webView;
    private final View.OnClickListener mFABClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.jumpFab:
                    webView.loadUrl("javascript:scroll(0,0)");
                    break;

                case R.id.shareFab:
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, R.string.share_action_subject);
                    i.putExtra(Intent.EXTRA_TEXT, webView.getUrl());
                    startActivity(Intent.createChooser(i, getString(R.string.share_action)));
                    break;

                case R.id.photoFab:
                    CustomTabs.with(getApplicationContext())
                            .setStyle(new CustomTabs.Style(getApplicationContext())
                            .setShowTitle(true)
                            .setStartAnimation(android.R.anim.fade_in, android.R.anim.fade_out)
                            .setExitAnimation(android.R.anim.fade_in, android.R.anim.fade_out)
                            .setToolbarColor(R.color.colorPrimary))
                            .setFallBackActivity(FolioBrowser.class)
                            .openUrl("http://m.facebook.com/photos/upload", MainActivity.this);
                    break;
                case R.id.updateFab:
                    webView.loadUrl("javascript:try{document.querySelector('button[name=\"view_overview\"]').click();}catch(_){window.location.href='http://m.facebook.com/?loadcomposer';}");
                    break;
                default:
                    break;
            }
            FAB.close(true);
        }
    };
    private static final int FILECHOOSER_RESULTCODE = 1;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;


    private String mPendingImageUrlToSave = null;
    protected final static String URL_PAGE_SHARE_LINKS = "/sharer.php?u=%s&t=%s";


    private Context mContext = null;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    View mCustomView;
    FrameLayout customViewContainer;
    View appWindow;
    Window window;

    private CustomViewCallback mCustomViewCallback;


    private Handler mUiHandler = new Handler();

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String PREFS_NAME = "MyPrefsFile";
    private ProfilePictureView profilePictureView;
    private TextView userNameView;
    private UiLifecycleHelper uiHelper;
    List<String> bookmarkUrls;
    List<String> bookmarkTitles;


    public static List<JSONObject> asList(final JSONArray ja) {
        final int len = ja.length();
        final ArrayList<JSONObject> result = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            final JSONObject obj = ja.optJSONObject(i);
            if (obj != null) {
                result.add(obj);
            }
        }
        return result;
    }


    @Override
    @SuppressLint({"setJavaScriptEnabled", "CutPasteId", "ClickableViewAccessibility", "SdCardPath"})
    protected void onCreate(Bundle savedInstanceState) {

        boolean isFolioTheme = PreferencesUtility.getInstance(this).getTheme().equals("folio");
        final boolean isPinkTheme = PreferencesUtility.getInstance(this).getTheme().equals("pink");
        boolean isDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("darktheme");
        final boolean isBlueGreyTheme = PreferencesUtility.getInstance(this).getTheme().equals("bluegrey");
        boolean defaultfont = PreferencesUtility.getInstance(this).getFont().equals("default_font");
        boolean mediumfont = PreferencesUtility.getInstance(this).getFont().equals("medium_font");
        boolean largefont = PreferencesUtility.getInstance(this).getFont().equals("large_font");
        boolean xlfont = PreferencesUtility.getInstance(this).getFont().equals("xl_font");
        boolean xxlfont = PreferencesUtility.getInstance(this).getFont().equals("xxl_font");
        boolean smallfont = PreferencesUtility.getInstance(this).getFont().equals("small_font");
        boolean topnews = PreferencesUtility.getInstance(this).getFeed().equals("top_news");
        boolean defaultfeed = PreferencesUtility.getInstance(this).getFeed().equals("default_news");
        boolean mostrecent = PreferencesUtility.getInstance(this).getFeed().equals("most_recent");
        final boolean fbtheme = PreferencesUtility.getInstance(this).getFreeTheme().equals("facebooktheme");
        final boolean blacktheme = PreferencesUtility.getInstance(this).getFreeTheme().equals("darktheme");
        final boolean dracula = PreferencesUtility.getInstance(this).getFreeTheme().equals("draculatheme");
        final boolean folio = PreferencesUtility.getInstance(this).getFreeTheme().equals("materialtheme");


        boolean mCreatingActivity = true;
        if (!mCreatingActivity) {
            if (isFolioTheme)
                setTheme(R.style.FolioTheme);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

        } else {

            if (isDarkTheme)
                setTheme(R.style.DarkTheme);


            if (isPinkTheme)
                setTheme(R.style.PinkTheme);


            if (isBlueGreyTheme)
                setTheme(R.style.BlueGreyTheme);


            super.onCreate(savedInstanceState);
            AdBuddiz.setPublisherKey("d8466a00-aaaa-4f2a-a763-a690adb16501");
            AdBuddiz.cacheAds(this);
            Permiso.getInstance().setActivity(this);
            setContentView(R.layout.activity_main);
            uiHelper = new UiLifecycleHelper(this, null);
            uiHelper.onCreate(savedInstanceState);
            Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult() {
                @Override
                public void onPermissionResult(Permiso.ResultSet resultSet) {
                    if (resultSet.isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    }
                    if (resultSet.isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    }
                }

               @Override
               public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions) {
              // TODO Permiso.getInstance().showRationaleInDialog("Title", "Message", null, callback);
              callback.onRationaleProvided();
              }
            }, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE);


            CustomTabs.with(this).warm();
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);


            preferences = PreferenceManager.getDefaultSharedPreferences(this);
            trayPreferences = new TrayAppPreferences(getApplicationContext());


            String lockState = (String) getIntent().getSerializableExtra("state");
            if (lockState != null && lockState.equals("unlocked")) {

            } else {
                if (preferences.getBoolean("folio_locker", false)) {
                    startActivity(new Intent(MainActivity.this, FolioUnlock.class));
                }
            }

            if (preferences.getBoolean("quickbar_pref", false)) {
                RemoteViews remoteView = new RemoteViews(MainActivity.this.getPackageName(), R.layout.quickbar);
                NotificationManager notificationmanager = (NotificationManager) FolioApplication.getContextOfApplication().getSystemService(Context.NOTIFICATION_SERVICE);
                remoteView.setTextViewText(R.id.quick, getString(R.string.app_name));
                remoteView.setTextViewText(R.id.quick_bar, getString(R.string.quick_bar));

                Builder builder = new Notification.Builder(FolioApplication.getContextOfApplication());
                builder.setSmallIcon(R.drawable.ic_stat_f)
                        .setOngoing(true)
                        .setContent(remoteView)
                        .setPriority(Notification.PRIORITY_MIN);


                Intent quickNewsfeed = new Intent(getApplicationContext(), QuickInstagram.class);
                quickNewsfeed.putExtra("start_url", "https://m.facebook.com/notifications");
                quickNewsfeed.setAction(Intent.ACTION_VIEW);
                PendingIntent newsIntent = PendingIntent.getActivity(getApplicationContext(), 0, quickNewsfeed,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                remoteView.setOnClickPendingIntent(R.id.quick_notifications, newsIntent);

                Intent quickMessages = new Intent(this, QuickGoogle.class);
                quickMessages.putExtra("start_url", "https://m.facebook.com/messages");
                quickMessages.setAction(Intent.ACTION_VIEW);
                PendingIntent messagesIntent = PendingIntent.getActivity(getApplicationContext(), 0, quickMessages,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                remoteView.setOnClickPendingIntent(R.id.quick_messages, messagesIntent);


                Intent quickFriends = new Intent(this, QuickFacebook.class);
                quickFriends.putExtra("start_url", "https://m.facebook.com/friends/center/friends/");
                quickFriends.setAction(Intent.ACTION_VIEW);
                PendingIntent friendsIntent = PendingIntent.getActivity(getApplicationContext(), 0, quickFriends,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                remoteView.setOnClickPendingIntent(R.id.quick_friends, friendsIntent);


                Intent quickNotifications = new Intent(FolioApplication.getContextOfApplication(), QuickTumblr.class);
                quickNotifications.putExtra("start_url", "https://m.facebook.com/");
                quickNotifications.setAction(Intent.ACTION_VIEW);
                PendingIntent notificationsIntent = PendingIntent.getActivity(getApplicationContext(), 0, quickNotifications,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                remoteView.setOnClickPendingIntent(R.id.quick_about, notificationsIntent);

                notificationmanager.notify(22, builder.build());


            }


            if (preferences.getBoolean("notifications_activated", false) || preferences.getBoolean("messages_activated", false)) {
                FolioReceiver.scheduleAlarms(getApplicationContext(), false);
            }


            if (preferences.getBoolean("hidden", false)) {
                toolbar = (Toolbar) findViewById(R.id.toolbar);
                toolbar.setVisibility(View.GONE);
            } else {
                toolbar = (Toolbar) findViewById(R.id.toolbar);
                toolbar.setVisibility(View.VISIBLE);

            }

            if (preferences.getBoolean("show_fab", false)) {
                FAB = (FloatingActionMenu) findViewById(R.id.fab);
                FAB.setVisibility(View.VISIBLE);

            } else {
                FAB = (FloatingActionMenu) findViewById(R.id.fab);
                FAB.setVisibility(View.GONE);

            }

            toolbar = (Toolbar) findViewById(R.id.toolbar);

            setSupportActionBar(toolbar);

            toolbar.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    webView.loadUrl("javascript:scroll(0,0)");

                }
            });


            navigationView = (NavigationView) findViewById(R.id.navigation_view);
            drawerLayoutFavs = (DrawerLayout) findViewById(R.id.drawer_layout);
            navigationViewFavs = (NavigationView) findViewById(R.id.folio_favorites);
            customViewContainer = (FrameLayout) findViewById(R.id.fullscreen_custom_content);
            FAB = (FloatingActionMenu) findViewById(R.id.fab);
            View headerView = navigationView.inflateHeaderView(R.layout.header);
            FrameLayout drawerHeader = (FrameLayout) headerView.findViewById(R.id.header);
            drawerHeader.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    webView.loadUrl("https://m.facebook.com/profile.php?");
                                                    drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
                                                    drawerLayout.closeDrawers();
                                                }
                                            }
            );


            Session.openActiveSession(this, true, new Session.StatusCallback() {

                @Override
                public void call(final Session session, SessionState state, Exception exception) {
                    if (session.isOpened()) {
                        webView.reload();
                        Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {


                            @Override
                            public void onCompleted(GraphUser user, Response response) {
                                if (user != null) {

                                    final ImageView profilePic = (ImageView) findViewById(R.id.profile_pic);
                                    String picUrl = "https://graph.facebook.com/"+user.getId()+"/picture?type=large";
                                    Picasso.with(getApplicationContext()).load(picUrl).into(profilePic);
                                    TextView name = (TextView) findViewById(R.id.profile_name);
                                    name.setText(user.getName());
                                    Bundle bundle = new Bundle();
                                    bundle.putString("fields", "cover,picture");
                                    new Request(Session.getActiveSession(),
                                            "/me",
                                            bundle,
                                            HttpMethod.GET,
                                            new Request.Callback() {
                                                public void onCompleted(Response response) {

                                                    try
                                                    {
                                                        GraphObject go  = response.getGraphObject();
                                                        JSONObject  jso = go.getInnerJSONObject();

                                                        final String coverUri = jso.getJSONObject("cover").getString("source");

                                                        Picasso.with(getApplicationContext()).load(Uri.parse(coverUri)).into((ImageView) findViewById(R.id.back_color));



                                                    } catch (JSONException e) {


                                                    } catch (NullPointerException e){



                                                        e.printStackTrace();
                                                    }
                                                }}
                                    ).executeAsync();
                                }
                            }
                        });
                        Request.executeMeRequestAsync(session, null);
                    }
                }

            });


            String webViewUrl = "https://m.facebook.com/";

            if (defaultfeed)
                webViewUrl = "https://m.facebook.com/";

            if (mostrecent)
                webViewUrl = "https://m.facebook.com/home.php?sk=h_chr&ref=bookmarks&app_id=608920319153834";

            if (topnews)
                webViewUrl = "https://m.facebook.com/home.php?sk=h_nor&ref=bookmarks";


            mCoordinatorLayoutView = findViewById(R.id.coordinatorLayout);
            swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
            swipeRefreshLayout.setColorSchemeResources(R.color.md_blue_500, R.color.md_deep_purple_700, R.color.bcP);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    webView.reload();
                }
            });

            findViewById(R.id.jumpFab).setOnClickListener(mFABClickListener);
            findViewById(R.id.shareFab).setOnClickListener(mFABClickListener);
            findViewById(R.id.photoFab).setOnClickListener(mFABClickListener);
            findViewById(R.id.updateFab).setOnClickListener(mFABClickListener);


            webView = (FolioWebViewScroll) findViewById(R.id.webView1);
            webView.setVerticalScrollBarEnabled(false);
            webView.getSettings().setAllowFileAccess(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setSupportZoom(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setDisplayZoomControls(false);
            if (Build.VERSION.SDK_INT < 18) {
                webView.getSettings().setAppCacheMaxSize(5 * 1024 * 1024);
            }
            webView.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
            webView.getSettings().setAppCacheEnabled(true);
            webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            webView.setListener(this, new FolioListener(this, webView));
            webView.addJavascriptInterface(new FolioInterfaces(this), "android");


            if (preferences.getBoolean("no_images", false))
                webView.getSettings().setLoadsImagesAutomatically(false);

            if (preferences.getBoolean("show_ads", false)){
            AdBuddiz.showAd(this);
            }else{
            AdBuddiz.showAd(null);
            }

            if (preferences.getBoolean("allow_inside", false)) {
                webView.addPermittedHostnames(SITES);
            }


            if (defaultfont)
                webView.getSettings().setTextZoom(100);

            if (smallfont)
                webView.getSettings().setTextZoom(90);


            if (mediumfont)
                webView.getSettings().setTextZoom(105);


            if (largefont)
                webView.getSettings().setTextZoom(110);


            if (xlfont)
                webView.getSettings().setTextZoom(120);

            if (xxlfont)
                webView.getSettings().setTextZoom(150);


            boolean isConnectedMobile = Connectivity.isConnectedMobile(getApplicationContext());
            boolean isFacebookZero = preferences.getBoolean("facebook_zero", false);


            String sharedSubject = getIntent().getStringExtra(Intent.EXTRA_SUBJECT);
            String sharedUrl = getIntent().getStringExtra(Intent.EXTRA_TEXT);


            if (sharedUrl != null) {
                if (!sharedUrl.equals("")) {

                    if (!sharedUrl.startsWith("http://") || !sharedUrl.startsWith("https://")) {

                        int startUrlIndex = sharedUrl.indexOf("http:");
                        if (startUrlIndex > 0) {

                            sharedUrl = sharedUrl.substring(startUrlIndex);
                        }
                    }

                    webViewUrl = String.format("https://m.facebook.com/sharer.php?u=%s&t=%s", sharedUrl, sharedSubject);

                    webViewUrl = Uri.parse(webViewUrl).toString();
                }
            }


            if ((getIntent() != null && getIntent().getDataString() != null) && (!isFacebookZero || !isConnectedMobile)) {
                webViewUrl = getIntent().getDataString();


            } else if (isFacebookZero && isConnectedMobile) {

                webViewUrl = "https://0.facebook.com";

            }

            if ((getIntent() != null && getIntent().getDataString() != null) && (!isFacebookZero || !isConnectedMobile)) {
                webViewUrl = getIntent().getDataString();


            } else if (isFacebookZero && isConnectedMobile) {


            }


            try {

                if (getIntent().getExtras().getString("start_url") != null) {
                    String temp = getIntent().getExtras().getString("start_url");
                    if (!isFacebookZero || !isConnectedMobile)
                        webViewUrl = temp;

                    if (temp.equals("https://m.facebook.com/notifications"))
                        FolioNotifications.clearNotifications();
                    if (temp.equals("https://m.facebook.com/messages/"))
                        FolioNotifications.clearNotifications();

                }
            } catch (Exception ignored) {
            }


            if (!Connectivity.isConnected(this) && !preferences.getBoolean("offline_mode", false))
                webView.loadUrl("file:///android_asset/error.html");
            webView.loadUrl(webViewUrl);
            webView.setWebViewClient(new FolioWebView() {


                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    swipeRefreshLayout.setRefreshing(true);
                }


                @SuppressLint("ResourceAsColor")
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    initalizeBookmarks(navigationViewFavs);

                    if (fbtheme)
                        injectDefaultCSS(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("facebooktheme", "facebooktheme"));

                    if (folio)
                        injectMaterialCSS(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("materialtheme", "materialtheme"));

                    if (blacktheme)
                        injectDarkCSS(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("darktheme", "darktheme"));

                    if (dracula)
                        injectDraculaCSS(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("draculatheme", "draculatheme"));

                    if (isPinkTheme)
                        injectPinkCSS(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("pinktheme", "pinktheme"));

                    if (isBlueGreyTheme)
                        injectBGCSS(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("bluegrey", "bluegrey"));
                    swipeRefreshLayout.setRefreshing(false);

                }
            });


            webView.setWebChromeClient(new WebChromeClient() {


                @Override
                public void onShowCustomView(View view, CustomViewCallback callback) {

                    if (mCustomView != null) {
                        callback.onCustomViewHidden();
                        return;
                    }
                    mCustomView = view;
                    customViewContainer.setVisibility(View.VISIBLE);
                    toolbar.setVisibility(View.GONE);
                    customViewContainer.addView(view);
                    mCustomViewCallback = callback;
                }


                @Override
                public void onHideCustomView() {
                    super.onHideCustomView();
                    if (mCustomView == null)
                        return;

                    // hide and remove customViewContainer
                    mCustomView.setVisibility(View.GONE);
                    customViewContainer.setVisibility(View.GONE);
                    if (preferences.getBoolean("hidden", false)) {
                        toolbar.setVisibility(View.GONE);
                    } else {
                        toolbar.setVisibility(View.VISIBLE);
                    }
                    customViewContainer.removeView(mCustomView);
                    mCustomViewCallback.onCustomViewHidden();


                    mCustomView = null;


                }


                @Override
                public void onGeolocationPermissionsShowPrompt(String origin,
                                                               Callback callback) {

                    super.onGeolocationPermissionsShowPrompt(origin, callback);
                    callback.invoke(origin, true, false);
                }


                @Override
                public void
                onReceivedTitle(WebView view, String title) {
                    super.onReceivedTitle(view, title);
                    if (title != null && title.contains("Facebook") || title.contains("1")) {
                        MainActivity.this.setTitle(R.string.app_name_toolbar);
                    } else {
                        MainActivity.this.setTitle(title);
                    }


                }

            });


            if (preferences.getBoolean("enable_gplus", false)) {
                navigationView.getMenu().findItem(R.id.googleplus).setVisible(false);

            }

            if (preferences.getBoolean("enable_instagram", false)) {
                navigationView.getMenu().findItem(R.id.instagram).setVisible(false);

            }

            if (preferences.getBoolean("enable_tumblr", false)) {
                navigationView.getMenu().findItem(R.id.tumblr).setVisible(false);

            }

            if (preferences.getBoolean("recent_off", false)) {
                navigationView.getMenu().findItem(R.id.newsfeed).setVisible(false);

            }

            if (preferences.getBoolean("top_off", false)) {
                navigationView.getMenu().findItem(R.id.top_newsdrawer).setVisible(false);

            }


            if (preferences.getBoolean("trending_off", false)) {
                navigationView.getMenu().findItem(R.id.trending).setVisible(false);

            }

            if (preferences.getBoolean("friends_off", false)) {
                navigationView.getMenu().findItem(R.id.friends).setVisible(false);

            }


            if (preferences.getBoolean("groups_off", false)) {
                navigationView.getMenu().findItem(R.id.group).setVisible(false);

            }

            if (preferences.getBoolean("pages_off", false)) {
                navigationView.getMenu().findItem(R.id.pages).setVisible(false);

            }

            if (preferences.getBoolean("photos_off", false)) {
                navigationView.getMenu().findItem(R.id.photos).setVisible(false);

            }

            if (preferences.getBoolean("events_off", false)) {
                navigationView.getMenu().findItem(R.id.events).setVisible(false);

            }


            if (preferences.getBoolean("thisday_off", false)) {
                navigationView.getMenu().findItem(R.id.onthisday).setVisible(false);

            }

            if (preferences.getBoolean("saved_off", false)) {
                navigationView.getMenu().findItem(R.id.saved).setVisible(false);

            }


            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {


                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {


                    drawerLayout.closeDrawers();


                    switch (menuItem.getItemId()) {


                        case R.id.googleplus:
                            Intent plus = new Intent(MainActivity.this, GoogleActivity.class);
                            startActivity(plus);

                            menuItem.setChecked(true);
                            return true;


                        case R.id.instagram:
                            Intent instagram = new Intent(MainActivity.this, InstagramActivity.class);
                            startActivity(instagram);

                            menuItem.setChecked(true);
                            return true;

                        case R.id.tumblr:
                            Intent tumblr = new Intent(MainActivity.this, TumblrActivity.class);
                            startActivity(tumblr);

                            menuItem.setChecked(true);
                            return true;


                        case R.id.newsfeed:
                            webView.loadUrl("https://m.facebook.com/");
                            menuItem.setChecked(true);
                            return true;

                        case R.id.top_newsdrawer:
                            webView.loadUrl("https://m.facebook.com/notes");
                            menuItem.setChecked(true);
                            return true;

                        case R.id.fbmenu:
                            webView.loadUrl("javascript:try{document.querySelector('#bookmarks_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "home.php';}");
                            menuItem.setChecked(true);
                            return true;

                        case R.id.trending:
                            webView.loadUrl("https://m.facebook.com/search/trending-news/?ref=bookmark&app_id=343553122467255");
                            menuItem.setChecked(true);
                            return true;

                        case R.id.friends:
                            webView.loadUrl("javascript:try{document.querySelector('#requests_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "friends/center/friends/';}");
                            menuItem.setChecked(true);
                            return true;
                        case R.id.group:
                            webView.loadUrl("https://m.facebook.com/groups/?category=membership");
                            menuItem.setChecked(true);
                            return true;
                        case R.id.pages:
                            webView.loadUrl("https://m.facebook.com/pages/launchpoint/?from=pages_nav_discover&ref=bookmarks");
                            menuItem.setChecked(true);
                            return true;
                        case R.id.photos:
                            webView.loadUrl("https://m.facebook.com/profile.php?v=photos&soft=composer");
                            menuItem.setChecked(true);
                            return true;
                        case R.id.events:
                            webView.loadUrl("https://m.facebook.com/events");
                            menuItem.setChecked(true);
                            return true;
                        case R.id.onthisday:
                            webView.loadUrl("https://m.facebook.com/onthisday");
                            menuItem.setChecked(true);
                            return true;
                        case R.id.saved:
                            webView.loadUrl("https://m.facebook.com/saved");
                            menuItem.setChecked(true);
                            return true;


                        case R.id.trans:
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/creativetrendsapps/folio/blob/master/strings.xml")));
                            return true;


                        case R.id.settings:
                            Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
                            startActivity(settings);
                            return true;

                        default:

                            return true;
                    }
                }
            });


            drawerLayout = (DrawerLayout) findViewById(R.id.drawer);


            ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {


                @Override
                public void onDrawerClosed(View drawerViewMain) {


                    super.onDrawerClosed(drawerViewMain);


                }

                @Override
                public void onDrawerOpened(View drawerViewMain) {


                    super.onDrawerOpened(drawerViewMain);

                    super.onDrawerSlide(drawerViewMain, 0);

                }
            };


            actionBarDrawerToggle.syncState();

        }


        initalizeBookmarks(navigationViewFavs);
        drawerLayoutFavs.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                initalizeBookmarks(navigationViewFavs);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                initalizeBookmarks(navigationViewFavs);
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        navigationViewFavs.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if (menuItem.getTitle() == getString(R.string.addPage)) {
                    if (!webView.getTitle().equals("Facebook")) {
                        addBookmark(webView.getTitle().replace("Facebook", ""), webView.getUrl());
                    }
                } else if (menuItem.getTitle() == getString(R.string.removePage)) {
                    removeBookmark(webView.getTitle().replace(" Facebook", ""));
                } else {
                    webView.loadUrl(bookmarkUrls.get(bookmarkTitles.indexOf(menuItem.getTitle())));
                    drawerLayoutFavs.closeDrawers();
                }
                return true;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        mNotificationButton = menu.findItem(R.id.action_notifications);

        ActionItemBadge.update(this, mNotificationButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_notifications_none, null), ActionItemBadge.BadgeStyles.RED, Integer.MIN_VALUE);

        mMessagesButton = menu.findItem(R.id.action_messages);

        ActionItemBadge.update(this, mMessagesButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_messenger, null), ActionItemBadge.BadgeStyles.RED, Integer.MIN_VALUE);


        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            if (navigationView.getMenu().getItem(i).isChecked()) {
                navigationView.getMenu().getItem(i).setChecked(false);
            }
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.search:
                webView.loadUrl("javascript:try{document.querySelector('#search_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "search/';}");

                return true;

            case R.id.folio_fav:
                drawerLayoutFavs.openDrawer(findViewById(R.id.folio_favorites));
                return true;


            case R.id.online:
                webView.loadUrl("https://m.facebook.com/buddylist.php");
                return true;


            case R.id.action_notifications:
                webView.loadUrl("javascript:try{document.querySelector('#notifications_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "notifications/';}");
                FolioNotifications.clearNotifications();
                FolioHelpers.updateNotifications(webView);
                return true;

            case R.id.action_messages:
                webView.loadUrl("javascript:try{document.querySelector('#messages_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "messages/';}");
                FolioNotifications.clearMessages();
                FolioHelpers.updateMessages(webView);
                return true;


            case R.id.fb_settings:
                webView.loadUrl("https://m.facebook.com/settings");

                return true;

            case R.id.logout:
                FileOperation.deleteCache(FolioApplication.getContextOfApplication());
                finish();
                return true;


            case R.id.close:
                finish();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }

    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        webView.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data,
                new FacebookDialog.Callback() {
                    @Override
                    public void onError(FacebookDialog.PendingCall pendingCall,
                                        Exception error, Bundle data) {
                        Log.e("Activity",
                                String.format("Error: %s", error.toString()));
                    }

                    @Override
                    public void onComplete(
                            FacebookDialog.PendingCall pendingCall, Bundle data) {
                        Log.i("Activity", "Success!");

                    }
                });

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        String webViewUrl = getIntent().getDataString();


        boolean isConnectedMobile = Connectivity.isConnectedMobile(getApplicationContext());
        boolean isFacebookZero = preferences.getBoolean("facebook_zero", false);


        String sharedSubject = getIntent().getStringExtra(Intent.EXTRA_SUBJECT);
        String sharedUrl = getIntent().getStringExtra(Intent.EXTRA_TEXT);


        if (sharedUrl != null) {
            if (!sharedUrl.equals("")) {

                if (!sharedUrl.startsWith("http://") || !sharedUrl.startsWith("https://")) {

                    int startUrlIndex = sharedUrl.indexOf("http:");
                    if (startUrlIndex > 0) {

                        sharedUrl = sharedUrl.substring(startUrlIndex);
                    }
                }

                webViewUrl = String.format("https://m.facebook.com/sharer.php?&app_id=749196541804006&u=%s&t=%s", sharedUrl, sharedSubject);

                webViewUrl = Uri.parse(webViewUrl).toString();

            }
        }

        try {
            if (getIntent().getExtras().getString("start_url") != null)
                webViewUrl = getIntent().getExtras().getString("start_url");

            if ("https://m.facebook.com/notifications".equals(webViewUrl))
                FolioNotifications.clearNotifications();
            if ("https://m.facebook.com/messages".equals(webViewUrl))
                FolioNotifications.clearMessages();
        } catch (Exception ignored) {
        }


        if (isFacebookZero && isConnectedMobile) {
        } else
            webView.loadUrl(webViewUrl);


        if (!Connectivity.isConnected(getApplicationContext()) && !preferences.getBoolean("offline_mode", false))


            if (getIntent().getBooleanExtra("apply_changes_to_app", false)) {
                finish();
                Intent restart = new Intent(MainActivity.this, MainActivity.class);
                startActivity(restart);
            }
    }


    public void setNotificationNum(int num) {
        if (num > 0) {
            ActionItemBadge.update(mNotificationButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_public, null), num);
        } else {

            ActionItemBadge.update(mNotificationButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_notifications_none, null), Integer.MIN_VALUE);
        }

    }

    public void setMessagesNum(int num) {
        if (num > 0) {
            ActionItemBadge.update(mMessagesButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_messenger_new, null), num);
        } else {

            ActionItemBadge.update(mMessagesButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_messenger, null), Integer.MIN_VALUE);
        }

    }


    public void initalizeBookmarks(NavigationView navigationViewFavs) {
        bookmarkUrls = new ArrayList<>();
        bookmarkTitles = new ArrayList<>();

        final Menu menu = navigationViewFavs.getMenu();
        menu.clear();
        String result = preferences.getString("bookmarks", "[]");
        try {
            JSONArray bookmarksArray = new JSONArray(result);
            for (int i = 0; i < bookmarksArray.length(); i++) {
                JSONObject bookmark = bookmarksArray.getJSONObject(i);
                menu.add(bookmark.getString("title")).setIcon(R.drawable.ic_favorite);
                bookmarkTitles.add(bookmark.getString("title"));
                bookmarkUrls.add(bookmark.getString("url"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!bookmarkUrls.contains(webView.getUrl())) {
            menu.add(getString(R.string.addPage)).setIcon(R.drawable.ic_add);
        } else {
            menu.add(getString(R.string.removePage)).setIcon(R.drawable.ic_close_folio);
        }
    }

    public void addBookmark(String title, String url) {
        String result = preferences.getString("bookmarks", "[]");
        try {
            JSONArray bookmarksArray = new JSONArray(result);
            bookmarksArray.put(new JSONObject("{'title':'" + title + "','url':'" + url + "'}"));
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("bookmarks", bookmarksArray.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initalizeBookmarks(navigationViewFavs);
    }

    public void removeBookmark(String title) {
        String result = preferences.getString("bookmarks", "[]");
        try {
            JSONArray bookmarksArray = new JSONArray(result);
            if (Build.VERSION.SDK_INT >= 19) {
                bookmarksArray.remove(bookmarkTitles.indexOf(title));
            } else {
                final List<JSONObject> objs = asList(bookmarksArray);
                objs.remove(bookmarkTitles.indexOf(title));
                final JSONArray out = new JSONArray();
                for (final JSONObject obj : objs) {
                    out.put(obj);
                }
                bookmarksArray = out;
            }
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("bookmarks", bookmarksArray.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initalizeBookmarks(navigationViewFavs);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerForContextMenu(webView);
        drawerLayout.closeDrawers();
        uiHelper.onResume();
        trayPreferences.put("activity_visible", true);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permiso.getInstance().onRequestPermissionResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterForContextMenu(webView);
        drawerLayout.closeDrawers();
        uiHelper.onPause();
        trayPreferences.put("activity_visible", false);


    }


    @Override
    public void onDestroy() {
        Log.i("MainActivity", "Destroying...");
        super.onDestroy();
        uiHelper.onDestroy();
        webView.removeAllViews();
        webView.destroy();

    }

    @Override
    public boolean onLongClick(View v) {
        openContextMenu(v);
        return true;
    }


    private void injectDefaultCSS(String mode) {
        try {
            InputStream inputStream = getAssets().open("fbdefault.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, 2) + "');" + "parent.appendChild(style)" + "})()");
        } catch (Exception fb) {
            fb.printStackTrace();
        }
    }

    private void injectMaterialCSS(String mode) {
        try {
            InputStream inputStream = getAssets().open("foliotheme.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, 2) + "');" + "parent.appendChild(style)" + "})()");
        } catch (Exception fb) {
            fb.printStackTrace();
        }
    }

    private void injectDarkCSS(String mode) {
        try {
            InputStream inputStream = getAssets().open("black.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, 2) + "');" + "parent.appendChild(style)" + "})()");
        } catch (Exception b) {
            b.printStackTrace();
        }
    }

    private void injectDraculaCSS(String mode) {
        try {
            InputStream inputStream = getAssets().open("dracula.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, 2) + "');" + "parent.appendChild(style)" + "})()");
        } catch (Exception d) {
            d.printStackTrace();
        }
    }

    private void injectPinkCSS(String mode) {
        try {
            InputStream inputStream = getAssets().open("pink_theme.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, 2) + "');" + "parent.appendChild(style)" + "})()");
        } catch (Exception d) {
            d.printStackTrace();
        }
    }

    private void injectBGCSS(String mode) {
        try {
            InputStream inputStream = getAssets().open("blue_grey.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, 2) + "');" + "parent.appendChild(style)" + "})()");
        } catch (Exception d) {
            d.printStackTrace();
        }
    }

}

