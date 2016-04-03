package com.creativtrendz.folio.activities;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.creativetrends.folio.app.R;

/**
 * Created by Jorell on 3/15/2016.
 */
public class FolioBrowser extends AppCompatActivity {

    WebView mWebView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.folio_browser);
        Toolbar toolbar = (Toolbar) findViewById(R.id.browser_toolbar);
        setSupportActionBar(toolbar);

        Uri uri = getIntent().getData();

        mWebView = (WebView) findViewById(R.id.webview_folio);
        registerForContextMenu(mWebView);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                setTitle(view.getTitle());
            }
        });

        if (uri != null) {
            mWebView.loadUrl(uri.toString());
        }


    }


    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }

}