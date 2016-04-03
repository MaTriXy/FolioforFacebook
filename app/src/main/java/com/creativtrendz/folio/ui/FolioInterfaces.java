package com.creativtrendz.folio.ui;

import android.webkit.JavascriptInterface;
import android.widget.TextView;

import com.creativtrendz.folio.activities.MainActivity;

public class FolioInterfaces {
    private final MainActivity mContext;

    public FolioInterfaces(MainActivity c) {
        mContext = c;
    }


    @JavascriptInterface
    public void setNotificationNum(final String number) {
        try {
            final int num = Integer.parseInt(number);
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mContext.setNotificationNum(num);
                }
            });

        } catch (NumberFormatException e) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mContext.setNotificationNum(0);
                }
            });

        }
    }

    @JavascriptInterface
    public void getMessages(final String number) {
        try {
            final int num = Integer.parseInt(number);
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mContext.setMessagesNum(num);
                }
            });

        } catch (NumberFormatException e) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mContext.setMessagesNum(0);
                }
            });

        }
    }

}