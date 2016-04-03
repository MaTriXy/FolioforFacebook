package com.creativtrendz.folio.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.creativtrendz.folio.services.Connectivity;
import com.creativtrendz.folio.ui.Dimension;

/**
 * Created by Jorell on 3/12/2016.
 */
public class FloatingWebView extends WebViewClient {


    private boolean refreshed;


    private static Context context = FolioApplication.getContextOfApplication();


    final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);


    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

        if (Uri.parse(url).getHost().endsWith("facebook.com")
                || Uri.parse(url).getHost().endsWith("m.facebook.com")
                || Uri.parse(url).getHost().endsWith("h.facebook.com")
                || Uri.parse(url).getHost().endsWith("l.facebook.com")
                || Uri.parse(url).getHost().endsWith("0.facebook.com")
                || Uri.parse(url).getHost().endsWith("zero.facebook.com")
                || Uri.parse(url).getHost().endsWith("plus.google.com")
                || Uri.parse(url).getHost().endsWith("accounts.google.com")
                || Uri.parse(url).getHost().endsWith("instagram.com")
                || Uri.parse(url).getHost().endsWith("tumblr.com")
                || Uri.parse(url).getHost().endsWith("fb.me")) {
            return false;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        view.getContext().startActivity(intent);
        return true;
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

        if (Connectivity.isConnected(context) && !failingUrl.contains("edge-chat") && !failingUrl.contains("akamaihd")
                && !failingUrl.contains("atdmt") && !refreshed) {
            view.loadUrl(failingUrl);

            refreshed = true;
        }
    }

    @TargetApi(android.os.Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError err) {

        onReceivedError(view, err.getErrorCode(), err.getDescription().toString(), req.getUrl().toString());
    }


    @Override
    public void onPageFinished(WebView view, String url) {


        view.loadUrl("javascript:function addStyleString(str) { var node = document.createElement('style'); " +
                "node.innerHTML = str; document.body.appendChild(node); } " +
                "addStyleString('[data-sigil*=m-promo-jewel-header]{ display: none; }');");

        if (preferences.getBoolean("hide_people", false))
            view.loadUrl("javascript:function addStyleString(str) { var node = document.createElement('style'); " +
                    "node.innerHTML = str; document.body.appendChild(node); } " +
                    "addStyleString('article._55wo._5rgr._5gh8._35au{ display: none; }');");


        if (preferences.getBoolean("no_images", false))
            view.loadUrl("javascript:function addStyleString(str) { var node = document.createElement('style'); node.innerHTML = str; document.body.appendChild(node); } addStyleString('.img, ._5s61, ._5sgg{ display: none; }');");

        if (preferences.getBoolean("hidden", false)) {
            String cssFixed = "#header{ position: fixed; z-index: 11; top: 0px; } #root{ padding-top: 44px; } .flyout{ max-height: " + Dimension.heightForFixedFacebookNavbar(context) + "px; overflow-y: scroll; }";
            view.loadUrl("javascript:function addStyleString(str) { var node = document.createElement('style'); node.innerHTML = str; document.body.appendChild(node); } addStyleString('" + cssFixed + "');");
        }


    }

}