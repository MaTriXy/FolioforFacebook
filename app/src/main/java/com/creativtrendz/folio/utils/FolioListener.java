package com.creativtrendz.folio.utils;

import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.creativetrends.folio.app.R;
import com.creativtrendz.folio.activities.MainActivity;
import com.creativtrendz.folio.fragments.Notify;
import com.creativtrendz.folio.ui.FolioHelpers;
import com.creativtrendz.folio.ui.FolioWebViewScroll;
import com.github.clans.fab.FloatingActionMenu;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.List;


public class FolioListener implements FolioWebViewScroll.Listener {
    private static final int ID_SAVE_IMAGE = 0;
    private static final int ID_SHARE_IMAGE = 1;
    private static SharedPreferences preferences;
    private final FolioWebViewScroll mWebView;
    private final FloatingActionMenu FAB;
    private final MainActivity fActivity;
    private final int mScrollThreshold;
    private final DownloadManager mDownloadManager;
    private final View mCoordinatorLayoutView;
    public FolioListener(MainActivity activity, WebView view) {
        fActivity = activity;
        mCoordinatorLayoutView = activity.mCoordinatorLayoutView;
        mWebView = (FolioWebViewScroll) view;
        FAB = (FloatingActionMenu) activity.findViewById(R.id.fab);
        preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        mScrollThreshold = activity.getResources().getDimensionPixelOffset(R.dimen.fab_scroll_threshold);
        mDownloadManager = (DownloadManager) fActivity.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
    }

    @Override
    public void onPageFinished(String url) {
        List<String> uri_segments = Uri.parse(url).getPathSegments();
        if (!(uri_segments.size() > 0 && uri_segments.get(0).equals("composer"))) {

        }


        int update_interval = Integer.parseInt(preferences.getString(Notify.KEY_PREF_UPDATE_INTERVAL, "5000"));


        FolioHelpers.updateNotificationsService(mWebView, update_interval);


        FolioHelpers.updateMessagesService(mWebView, update_interval);


        if (!url.contains("/composer/")) {
            fActivity.swipeRefreshLayout.setEnabled(true);
        } else {
            fActivity.swipeRefreshLayout.setEnabled(false);
        }


    }


    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
    }

    @Override
    public void onDownloadRequested(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
    }

    @Override
    public void onExternalPageRequest(String url) {

        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
        intentBuilder.setShowTitle(true);
        intentBuilder.setToolbarColor(ContextCompat.getColor(fActivity, R.color.colorPrimary));

        Intent actionIntent = new Intent(Intent.ACTION_SEND);
        actionIntent.setType("text/plain");
        actionIntent.putExtra(Intent.EXTRA_TEXT, url);

        PendingIntent menuItemPendingIntent = PendingIntent.getActivity(fActivity, 0, actionIntent, 0);
        intentBuilder.setActionButton(BitmapFactory.decodeResource(getResources(), R.drawable.ic_share), (fActivity.getString(R.string.update_share)), menuItemPendingIntent);
        intentBuilder.addMenuItem(fActivity.getString(R.string.update_share), menuItemPendingIntent);

        intentBuilder.build().launchUrl(fActivity, Uri.parse(url));
    }




    @Override
    public void onScrollChange(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

        if (preferences.getBoolean("show_fab", false) && Math.abs(oldScrollY - scrollY) > mScrollThreshold) {
            if (scrollY > oldScrollY) {

                FAB.hideMenuButton(true);
            } else if (scrollY < oldScrollY) {

                FAB.showMenuButton(true);
            }

        }
    }


    private Resources getResources() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void onCreateContextMenu(ContextMenu contextMenu) {
        final WebView.HitTestResult result = mWebView.getHitTestResult();

        MenuItem.OnMenuItemClickListener handler = new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                if (i == ID_SAVE_IMAGE) {
                    // Save the image
                    Uri uri = Uri.parse(result.getExtra());
                    DownloadManager.Request request = new DownloadManager.Request(uri);

                    // Set the download directory
                    File downloads_dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    if (!downloads_dir.exists()) {
                        if (!downloads_dir.mkdirs()) {
                            return false;
                        }
                    }
                    File destinationFile = new File(downloads_dir, uri.getLastPathSegment());
                    request.setDestinationUri(Uri.fromFile(destinationFile));

                    // Make notification stay after download
                    request.setVisibleInDownloadsUi(true);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                    // Start the download
                    mDownloadManager.enqueue(request);
                    return true;
                } else if (i == ID_SHARE_IMAGE) {
                    final Uri uri = Uri.parse(result.getExtra());
                    // Share image
                    Target target = new Target() {
                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                        }

                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {
                            String path = MediaStore.Images.Media.insertImage(fActivity.getContentResolver(), bitmap, uri.getLastPathSegment(), null);

                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("image/*");
                            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
                            fActivity.startActivity(Intent.createChooser(shareIntent, fActivity.getString(R.string.context_share_image)));
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                        }
                    };

                    Picasso.with(fActivity).load(uri).into(target);
                    Snackbar.make(mCoordinatorLayoutView, R.string.context_share_image_progress, Snackbar.LENGTH_SHORT).show();
                    return true;
                }
                    return false;
                }

        };


        if (result.getType() == WebView.HitTestResult.IMAGE_TYPE || result.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
            contextMenu.add(0, ID_SAVE_IMAGE, 0, R.string.context_save_image).setOnMenuItemClickListener(handler);
            contextMenu.add(0, ID_SHARE_IMAGE, 0, R.string.context_share_image).setOnMenuItemClickListener(handler);
        }


    }


}

