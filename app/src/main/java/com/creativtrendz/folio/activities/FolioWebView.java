// Copyright (C) 2014-2015 Jorell Rutledge/Creative Trends.
//This file is originally apart of Folio for Facebook.
//Copyright notice must remain here if you're using any part of this code.
//Some code taken from Tinfoil for Facebook
//Some code taken from Facebook Lite


package com.creativtrendz.folio.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.creativtrendz.folio.services.Connectivity;
import com.creativtrendz.folio.ui.Dimension;
import com.creativtrendz.folio.ui.FolioHelpers;

@SuppressWarnings("unused")
public class FolioWebView extends WebViewClient {
	
	public static String currentlyLoadedPage;
    private static long lastSavingTime = System.currentTimeMillis();
    public static boolean wasOffline;

    private boolean refreshed;
    

    private static Context context = FolioApplication.getContextOfApplication();

    
       
    final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);




    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

        if (Uri.parse(url).getHost().endsWith("facebook.com")
                || Uri.parse(url).getHost().endsWith("m.facebook.com")
                || Uri.parse(url).getHost().endsWith("mobile.facebook.com")
                || Uri.parse(url).getHost().endsWith("touch.facebook.com")
                || Uri.parse(url).getHost().endsWith("h.facebook.com")
                || Uri.parse(url).getHost().endsWith("l.facebook.com")
                || Uri.parse(url).getHost().endsWith("0.facebook.com")
                || Uri.parse(url).getHost().endsWith("zero.facebook.com")
                || Uri.parse(url).getHost().endsWith("media.giphy.com")
                || Uri.parse(url).getHost().endsWith("fbcdn.net")
                || Uri.parse(url).getHost().endsWith("akamaihd.net")
                || Uri.parse(url).getHost().endsWith("googleusercontent.com")
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
	

	
		

	@Override
	public void onPageFinished(WebView view, String url) {

        if (preferences.getBoolean("auto_scroll", false))
            FolioHelpers.hideMenuBar(view);

        final String cssHideSponsored = "#m_newsfeed_stream article[data-ft*=\"\\\"ei\\\":\\\"\"], " +
                ".aymlCoverFlow, .aymlNewCoverFlow[data-ft*=\"\\\"is_sponsored\\\":\\\"1\\\"\"], .pyml, " +
                ".storyStream > ._6t2[data-sigil=\"marea\"], .storyStream > .fullwidth._539p, .storyStream > " +
                "article[id^=\"u_\"]._676, .storyStream > article[id^=\"u_\"].storyAggregation { display: none; }";
        view.loadUrl("javascript:function addStyleString(str) { var node = document.createElement('style'); " +
                "node.innerHTML = str; document.body.appendChild(node); } addStyleString('" + cssHideSponsored + "');");


			view.loadUrl("javascript:function addStyleString(str) { var node = document.createElement('style'); " +
                    "node.innerHTML = str; document.body.appendChild(node); } " +
                    "addStyleString('[data-sigil*=m-promo-jewel-header]{ display: none; }');");
		
        if (preferences.getBoolean("hide_people", false))
            view.loadUrl("javascript:function addStyleString(str) { var node = document.createElement('style'); node.innerHTML = str; document.body.appendChild(node); } addStyleString('article._55wo._5rgr._5gh8._35au{ display: none; }');");
    
       
        if (preferences.getBoolean("no_images", false))
            view.loadUrl("javascript:function addStyleString(str) { var node = document.createElement('style'); node.innerHTML = str; document.body.appendChild(node); } addStyleString('.img, ._5s61, ._5sgg{ display: none; }');");

        if (preferences.getBoolean("hidden", false)) {
            String cssFixed = "#header{ position: fixed; z-index: 11; top: 0px; } #root{ padding-top: 44px; } .flyout{ max-height: " + Dimension.heightForFixedFacebookNavbar(context) + "px; overflow-y: scroll; }";
            view.loadUrl("javascript:function addStyleString(str) { var node = document.createElement('style'); node.innerHTML = str; document.body.appendChild(node); } addStyleString('" + cssFixed + "');");
        }



	}
	
}