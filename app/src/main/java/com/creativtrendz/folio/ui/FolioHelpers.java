package com.creativtrendz.folio.ui;

import android.webkit.WebView;

public class FolioHelpers {
	public static void hideMenuBar(WebView view) {

        view.loadUrl("javascript:try{if(!document.URL.match('facebook\\.com\\/composer')){document.getElementById('page').style.top='-45px';android.isComposer(false)}else{android.isComposer(true)}}catch(e){}android.loadingCompleted();");
    }


    public static void updateNotificationsService(WebView view, int interval) {

        view.loadUrl("javascript:function notification_service(){android.getNotifications(document.querySelector('#notifications_jewel > a > div > span[data-sigil=count]').innerHTML);setTimeout(notification_service, " + interval + ");}try{notification_service();}catch(e){}");
    }

    public static void updateNotifications(WebView view) {

        view.loadUrl("javascript:android.getNotifications(document.querySelector('#notifications_jewel > a > div > span[data-sigil=count]').innerHTML);");
    }

    public static void updateMessagesService(WebView view, int interval) {

        view.loadUrl("javascript:function message_service(){android.getMessages(document.querySelector('#messages_jewel > a > div > span[data-sigil=count]').innerHTML);setTimeout(message_service, " + interval + ");}try{message_service();}catch(e){}");
    }

    public static void updateMessages(WebView view) {

        view.loadUrl("javascript:android.getMessages(document.querySelector('#messages_jewel > a > div > span[data-sigil=count]').innerHTML);");
    }


}
    


